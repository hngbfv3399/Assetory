export async function getApiData(path) {
  const response = await fetch(path)
  const body = await response.json().catch(() => null)

  if (!response.ok || !body?.success) {
    throw new Error(body?.message ?? '요청을 처리하지 못했습니다.')
  }

  return body.data
}
