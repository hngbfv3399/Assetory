import { fetchClient } from '../../common/api/fetchClient.js'

export const getSellerProducts = ({ status, page = 0, size = 20 } = {}) => {
  const params = new URLSearchParams({ page: String(page), size: String(size) })
  if (status) params.set('status', status)
  return fetchClient(`/api/seller/products?${params}`)
}
export const getSellerProduct = (productId) => fetchClient(`/api/seller/products/${productId}`)
export const createSellerProduct = (body) => fetchClient('/api/seller/products', { method: 'POST', body })
export const updateSellerProduct = (productId, body) => fetchClient(`/api/seller/products/${productId}`, { method: 'PATCH', body })
export const deleteSellerProduct = (productId) => fetchClient(`/api/seller/products/${productId}`, { method: 'DELETE' })
export const addImage = (productId, body) => fetchClient(`/api/seller/products/${productId}/images`, { method: 'POST', body })
export const deleteImage = (productId, imageId) => fetchClient(`/api/seller/products/${productId}/images/${imageId}`, { method: 'DELETE' })
export const addResource = (productId, body) => fetchClient(`/api/seller/products/${productId}/resources`, { method: 'POST', body })
export const deleteResource = (productId, resourceId) => fetchClient(`/api/seller/products/${productId}/resources/${resourceId}`, { method: 'DELETE' })
export const publishProduct = (productId) => fetchClient(`/api/seller/products/${productId}/publish`, { method: 'PATCH' })
export const suspendProduct = (productId) => fetchClient(`/api/seller/products/${productId}/suspend`, { method: 'PATCH' })
