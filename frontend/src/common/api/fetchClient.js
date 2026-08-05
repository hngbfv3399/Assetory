import { useAuthStore } from '../store/useAuthStore.js'
import { ApiError } from './ApiError.js'

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')

export async function fetchClient(path, options = {}) {
  const { body, headers, responseType, ...requestOptions } = options
  const token = useAuthStore.getState().accessToken
  const requestUrl = path.startsWith('http') ? path : `${apiBaseUrl}${path}`

  let response
  try {
    response = await fetch(requestUrl, {
      ...requestOptions,
      credentials: 'include',
      headers: {
        ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...headers,
      },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new ApiError({ status: 0, code: 'NETWORK_ERROR', message: '서버에 연결할 수 없습니다.' })
  }

  if (responseType === 'blob' && response.ok) return response.blob()

  const result = await response.json().catch(() => null)
  const code = result?.data?.code ?? 'UNKNOWN_ERROR'

  if (!response.ok || !result?.success) {
    throw new ApiError({
      status: response.status,
      code,
      message: result?.message ?? '요청을 처리하지 못했습니다.',
      details: result,
    })
  }

  return result.data
}
