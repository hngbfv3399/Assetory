import { fetchClient } from './fetchClient.js'

export const api = {
  get: (path, options) => fetchClient(path, { ...options, method: 'GET' }),
  post: (path, body, options) => fetchClient(path, { ...options, method: 'POST', body }),
  patch: (path, body, options) => fetchClient(path, { ...options, method: 'PATCH', body }),
  delete: (path, options) => fetchClient(path, { ...options, method: 'DELETE' }),
}

export { ApiError } from './ApiError.js'
