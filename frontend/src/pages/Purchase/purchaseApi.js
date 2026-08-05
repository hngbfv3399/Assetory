import { fetchClient } from '../../common/api/fetchClient.js'

export const getCart = () => fetchClient('/api/cart')
export const addCartItem = (productId) => fetchClient('/api/cart/items', { method: 'POST', body: { productId } })
export const removeCartItem = (cartItemId) => fetchClient(`/api/cart/items/${cartItemId}`, { method: 'DELETE' })
export const createCartOrders = (productIds) => fetchClient('/api/orders', { method: 'POST', body: { productIds } })
export const createDirectOrder = (productId) => fetchClient('/api/orders/direct', { method: 'POST', body: { productId } })
export const payMock = (orderId) => fetchClient('/api/payments/mock', { method: 'POST', body: { orderId, result: 'SUCCESS' } })
export const getPurchases = () => fetchClient('/api/purchases')
export const getPurchaseResources = (orderItemId) => fetchClient(`/api/purchases/${orderItemId}/resources`)
export const openPurchaseResource = (orderItemId, resourceId) => fetchClient(`/api/purchases/${orderItemId}/resources/${resourceId}/open`)
export const downloadPurchaseResource = (orderItemId, resourceId) => fetchClient(`/api/purchases/${orderItemId}/resources/${resourceId}/download`, { responseType: 'blob' })
