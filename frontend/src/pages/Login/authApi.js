import { fetchClient } from '../../common/api/fetchClient.js'

export const signup = (body) => fetchClient('/api/auth/signup', { method: 'POST', body })
export const login = (body) => fetchClient('/api/auth/login', { method: 'POST', body })
export const refresh = () => fetchClient('/api/auth/refresh', { method: 'POST' })
export const logout = () => fetchClient('/api/auth/logout', { method: 'POST' })
export const getMyProfile = () => fetchClient('/api/users/me')
