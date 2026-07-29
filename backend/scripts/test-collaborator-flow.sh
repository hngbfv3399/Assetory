#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
SUFFIX="$(date +%s)"
PASSWORD='CollaboratorTest123!'

response_body=''
response_code=''

request() {
  response_body="$(curl --silent --show-error --write-out $'\n%{http_code}' "$@")"
  response_code="${response_body##*$'\n'}"
  response_body="${response_body%$'\n'*}"
}

assert() {
  local expected_code="$1"
  local filter="$2"
  if [[ "$response_code" != "$expected_code" ]] || ! printf '%s' "$response_body" | jq -e "$filter" >/dev/null; then
    echo "Expected HTTP ${expected_code} with: ${filter}" >&2
    echo "Received HTTP ${response_code}: ${response_body}" >&2
    exit 1
  fi
}

signup() {
  local label="$1"
  local email="${label}.${SUFFIX}@example.com"
  local payload
  payload="$(jq -n --arg email "$email" --arg password "$PASSWORD" --arg nickname "${label}${SUFFIX}" '{email: $email, password: $password, nickname: $nickname}')"
  request --request POST "${BASE_URL}/api/auth/signup" --header 'Content-Type: application/json' --data "$payload"
  assert 201 '.success == true and .data.id != null'
  SIGNED_EMAIL="$email"
  SIGNED_ID="$(printf '%s' "$response_body" | jq -r '.data.id')"
}

login() {
  local email="$1"
  local payload
  payload="$(jq -n --arg email "$email" --arg password "$PASSWORD" '{email: $email, password: $password}')"
  request --request POST "${BASE_URL}/api/auth/login" --header 'Content-Type: application/json' --data "$payload"
  assert 200 '.success == true and (.data.accessToken | type == "string")'
  LOGIN_TOKEN="$(printf '%s' "$response_body" | jq -r '.data.accessToken')"
}

echo '1/15 Create owner, manager, editor, viewer, and third-party accounts'
signup owner; OWNER_EMAIL="$SIGNED_EMAIL"; OWNER_ID="$SIGNED_ID"
signup manager; MANAGER_EMAIL="$SIGNED_EMAIL"; MANAGER_ID="$SIGNED_ID"
signup editor; EDITOR_EMAIL="$SIGNED_EMAIL"; EDITOR_ID="$SIGNED_ID"
signup viewer; VIEWER_EMAIL="$SIGNED_EMAIL"; VIEWER_ID="$SIGNED_ID"
signup third; THIRD_EMAIL="$SIGNED_EMAIL"; THIRD_ID="$SIGNED_ID"

login "$OWNER_EMAIL"; OWNER_TOKEN="$LOGIN_TOKEN"
login "$MANAGER_EMAIL"; MANAGER_TOKEN="$LOGIN_TOKEN"
login "$EDITOR_EMAIL"; EDITOR_TOKEN="$LOGIN_TOKEN"
login "$VIEWER_EMAIL"; VIEWER_TOKEN="$LOGIN_TOKEN"
login "$THIRD_EMAIL"; THIRD_TOKEN="$LOGIN_TOKEN"

CATEGORY_ID="$(curl --silent --show-error "${BASE_URL}/api/categories" | jq -er '.data[0].id')"
PRODUCT_PAYLOAD="$(jq -n --argjson categoryId "$CATEGORY_ID" --arg suffix "$SUFFIX" '{categoryId: $categoryId, name: ("collaboration-" + $suffix), summary: "automation summary", description: "automation description", price: 10000}')"
request --request POST "${BASE_URL}/api/seller/products" --header "Authorization: Bearer ${OWNER_TOKEN}" --header 'Content-Type: application/json' --data "$PRODUCT_PAYLOAD"
assert 201 '.success == true and .data.id != null'
PRODUCT_ID="$(printf '%s' "$response_body" | jq -r '.data.id')"

invite_and_accept() {
  local member_id="$1"
  local role="$2"
  local token="$3"
  local payload
  payload="$(jq -n --argjson userId "$member_id" --arg role "$role" '{userId: $userId, role: $role}')"
  request --request POST "${BASE_URL}/api/seller/products/${PRODUCT_ID}/collaborators" --header "Authorization: Bearer ${OWNER_TOKEN}" --header 'Content-Type: application/json' --data "$payload"
  assert 201 ".success == true and .data.role == \"${role}\" and .data.status == \"PENDING\""
  local collaborator_id
  collaborator_id="$(printf '%s' "$response_body" | jq -r '.data.id')"
  request --request PATCH "${BASE_URL}/api/seller/collaborator-invitations/${collaborator_id}" --header "Authorization: Bearer ${token}" --header 'Content-Type: application/json' --data '{"status":"ACCEPTED"}'
  assert 200 '.success == true and .data.status == "ACCEPTED"'
}

echo '2/15 Invite and accept three roles'
invite_and_accept "$MANAGER_ID" MANAGER "$MANAGER_TOKEN"
invite_and_accept "$EDITOR_ID" EDITOR "$EDITOR_TOKEN"
invite_and_accept "$VIEWER_ID" VIEWER "$VIEWER_TOKEN"

echo '3/15 Block editor direct product update'
request --request PATCH "${BASE_URL}/api/seller/products/${PRODUCT_ID}" --header "Authorization: Bearer ${EDITOR_TOKEN}" --header 'Content-Type: application/json' --data '{"name":"blocked"}'
assert 403 '.success == false and .data.code == "FORBIDDEN"'

echo '4/15 Create editor change request'
CHANGE_PAYLOAD="$(jq -n --arg suffix "$SUFFIX" '{type: "UPDATE_PRODUCT", payload: {name: ("approved-" + $suffix)}}')"
request --request POST "${BASE_URL}/api/seller/products/${PRODUCT_ID}/change-requests" --header "Authorization: Bearer ${EDITOR_TOKEN}" --header 'Content-Type: application/json' --data "$CHANGE_PAYLOAD"
assert 201 '.success == true and .data.status == "PENDING"'
CHANGE_ID="$(printf '%s' "$response_body" | jq -r '.data.id')"

echo '5/15 Confirm pending request has not changed product'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}" --header "Authorization: Bearer ${OWNER_TOKEN}"
assert 200 '.success == true and (.data.name | startswith("collaboration-"))'

echo '6/15 Approve request and apply change'
request --request PATCH "${BASE_URL}/api/seller/product-change-requests/${CHANGE_ID}" --header "Authorization: Bearer ${OWNER_TOKEN}" --header 'Content-Type: application/json' --data '{"status":"APPROVED"}'
assert 200 '.success == true and .data.status == "APPROVED"'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}" --header "Authorization: Bearer ${OWNER_TOKEN}"
assert 200 '.success == true and (.data.name | startswith("approved-"))'

echo '7/15 Block editor sales-state proposal'
request --request POST "${BASE_URL}/api/seller/products/${PRODUCT_ID}/change-requests" --header "Authorization: Bearer ${EDITOR_TOKEN}" --header 'Content-Type: application/json' --data '{"type":"SUSPEND","payload":{}}'
assert 403 '.success == false and .data.code == "FORBIDDEN"'

echo '8/15 Create and reject manager sales-state proposal'
request --request POST "${BASE_URL}/api/seller/products/${PRODUCT_ID}/change-requests" --header "Authorization: Bearer ${MANAGER_TOKEN}" --header 'Content-Type: application/json' --data '{"type":"SUSPEND","payload":{}}'
assert 201 '.success == true and .data.status == "PENDING"'
MANAGER_CHANGE_ID="$(printf '%s' "$response_body" | jq -r '.data.id')"
request --request PATCH "${BASE_URL}/api/seller/product-change-requests/${MANAGER_CHANGE_ID}" --header "Authorization: Bearer ${OWNER_TOKEN}" --header 'Content-Type: application/json' --data '{"status":"REJECTED","rejectionReason":"keep current state"}'
assert 200 '.success == true and .data.status == "REJECTED" and .data.rejectionReason == "keep current state"'

echo '9/15 Allow manager product-scoped order counts'
request "${BASE_URL}/api/seller/orders/counts?productId=${PRODUCT_ID}" --header "Authorization: Bearer ${MANAGER_TOKEN}"
assert 200 '.success == true and .data.total == 0'

echo '10/15 Allow manager product-scoped refunds and inquiries'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}/refunds" --header "Authorization: Bearer ${MANAGER_TOKEN}"
assert 200 '.success == true and (.data.refunds | type == "array")'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}/inquiries" --header "Authorization: Bearer ${MANAGER_TOKEN}"
assert 200 '.success == true and (.data.rooms | type == "array")'

echo '11/15 Allow viewer product-scoped statistics'
request "${BASE_URL}/api/seller/statistics/summary?productId=${PRODUCT_ID}" --header "Authorization: Bearer ${VIEWER_TOKEN}"
assert 200 '.success == true and .data.period != null'

echo '12/15 Block editor from manager operations'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}/refunds" --header "Authorization: Bearer ${EDITOR_TOKEN}"
assert 403 '.success == false and .data.code == "FORBIDDEN"'

echo '13/15 Block viewer from content proposals'
request --request POST "${BASE_URL}/api/seller/products/${PRODUCT_ID}/change-requests" --header "Authorization: Bearer ${VIEWER_TOKEN}" --header 'Content-Type: application/json' --data '{"type":"UPDATE_PRODUCT","payload":{"name":"blocked"}}'
assert 403 '.success == false and .data.code == "FORBIDDEN"'

echo '14/15 Block third-party product access'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}/change-requests" --header "Authorization: Bearer ${THIRD_TOKEN}"
assert 403 '.success == false and .data.code == "FORBIDDEN"'

echo '15/15 Block unauthenticated collaborator management'
request "${BASE_URL}/api/seller/products/${PRODUCT_ID}/collaborators"
assert 401 '.success == false and .data.code == "UNAUTHORIZED"'

echo 'Collaborator role and approval flow test passed.'
