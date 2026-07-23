#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
COOKIE_JAR="$(mktemp)"
trap 'rm -f "$COOKIE_JAR"' EXIT

UNIQUE_SUFFIX="$(date +%s)"
EMAIL="assetory.auth.${UNIQUE_SUFFIX}@example.com"
NICKNAME="assetory${UNIQUE_SUFFIX}"
PASSWORD="TestPassword123!"

split_response() {
  RESPONSE_BODY="${1%$'\n'*}"
  RESPONSE_STATUS="${1##*$'\n'}"
}

assert_response() {
  local expected_status="$1"
  local json_filter="$2"

  if [[ "$RESPONSE_STATUS" != "$expected_status" ]]; then
    echo "Expected HTTP ${expected_status}, received ${RESPONSE_STATUS}."
    echo "$RESPONSE_BODY"
    exit 1
  fi

  if ! printf '%s' "$RESPONSE_BODY" | jq -e "$json_filter" >/dev/null; then
    echo "Unexpected response body:"
    echo "$RESPONSE_BODY"
    exit 1
  fi
}

SIGNUP_PAYLOAD="$(jq -n \
  --arg email "$EMAIL" \
  --arg password "$PASSWORD" \
  --arg nickname "$NICKNAME" \
  '{email: $email, password: $password, nickname: $nickname}')"

echo "1/11 Sign up"
split_response "$(curl --silent --show-error --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/signup" \
  --header 'Content-Type: application/json' \
  --data "$SIGNUP_PAYLOAD")"
assert_response "201" '.success == true and .data.email != null and .message == null'

echo "2/11 Reject duplicate email"
split_response "$(curl --silent --show-error --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/signup" \
  --header 'Content-Type: application/json' \
  --data "$SIGNUP_PAYLOAD")"
assert_response "409" '.success == false and .data.code == "EMAIL_ALREADY_EXISTS"'

LOGIN_PAYLOAD="$(jq -n --arg email "$EMAIL" --arg password "$PASSWORD" '{email: $email, password: $password}')"

echo "3/11 Log in and receive refresh-token cookie"
split_response "$(curl --silent --show-error --cookie-jar "$COOKIE_JAR" --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/login" \
  --header 'Content-Type: application/json' \
  --data "$LOGIN_PAYLOAD")"
assert_response "200" '.success == true and (.data.accessToken | type == "string") and .data.user.email != null'
ACCESS_TOKEN="$(printf '%s' "$RESPONSE_BODY" | jq -r '.data.accessToken')"
REFRESH_TOKEN="$(awk '$6 == "refreshToken" {print $7}' "$COOKIE_JAR")"

if [[ -z "$REFRESH_TOKEN" ]]; then
  echo "Refresh token cookie was not created."
  exit 1
fi

echo "4/11 Get profile with access token"
split_response "$(curl --silent --show-error --write-out $'\n%{http_code}' \
  --request GET "${BASE_URL}/api/users/me" \
  --header "Authorization: Bearer ${ACCESS_TOKEN}")"
assert_response "200" '.success == true and .data.email != null and .data.profileImageUrl == null'

UPDATED_NICKNAME="${NICKNAME}updated"
PROFILE_IMAGE_URL="https://example.com/profile.png"
UPDATE_PAYLOAD="$(jq -n \
  --arg nickname "$UPDATED_NICKNAME" \
  --arg profileImageUrl "$PROFILE_IMAGE_URL" \
  '{nickname: $nickname, profileImageUrl: $profileImageUrl}')"

echo "5/11 Update only supplied profile fields"
split_response "$(curl --silent --show-error --write-out $'\n%{http_code}' \
  --request PATCH "${BASE_URL}/api/users/me" \
  --header 'Content-Type: application/json' \
  --header "Authorization: Bearer ${ACCESS_TOKEN}" \
  --data "$UPDATE_PAYLOAD")"
assert_response "200" '.success == true and .data.nickname != null and .data.profileImageUrl == "https://example.com/profile.png"'

echo "6/11 Reissue access token from refresh-token cookie"
split_response "$(curl --silent --show-error --cookie "$COOKIE_JAR" --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/refresh")"
assert_response "200" '.success == true and (.data.accessToken | type == "string")'

echo "7/11 Log out and revoke refresh token"
split_response "$(curl --silent --show-error --cookie "$COOKIE_JAR" --cookie-jar "$COOKIE_JAR" --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/logout" \
  --header "Authorization: Bearer ${ACCESS_TOKEN}")"
assert_response "200" '.success == true and .data == null and .message == "로그아웃되었습니다."'

echo "8/11 Reject refresh token after logout"
split_response "$(curl --silent --show-error --cookie "refreshToken=${REFRESH_TOKEN}" --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/refresh")"
assert_response "401" '.success == false and .data.code == "INVALID_REFRESH_TOKEN"'

echo "9/11 Log in again before withdrawal"
split_response "$(curl --silent --show-error --cookie-jar "$COOKIE_JAR" --write-out $'\n%{http_code}' \
  --request POST "${BASE_URL}/api/auth/login" \
  --header 'Content-Type: application/json' \
  --data "$LOGIN_PAYLOAD")"
assert_response "200" '.success == true and (.data.accessToken | type == "string")'
ACCESS_TOKEN="$(printf '%s' "$RESPONSE_BODY" | jq -r '.data.accessToken')"

echo "10/11 Withdraw user and revoke all refresh tokens"
split_response "$(curl --silent --show-error --cookie "$COOKIE_JAR" --cookie-jar "$COOKIE_JAR" --write-out $'\n%{http_code}' \
  --request DELETE "${BASE_URL}/api/users/me" \
  --header "Authorization: Bearer ${ACCESS_TOKEN}")"
assert_response "200" '.success == true and .data == null and .message == "회원 탈퇴가 완료되었습니다."'

echo "11/11 Reject access token after withdrawal"
split_response "$(curl --silent --show-error --write-out $'\n%{http_code}' \
  --request GET "${BASE_URL}/api/users/me" \
  --header "Authorization: Bearer ${ACCESS_TOKEN}")"
assert_response "403" '.success == false and .data.code == "USER_INACTIVE"'

echo "Authentication and member flow test passed."
