import { api } from '../../../common/api/index.js'

export function getHomeProducts() {
  const params = new URLSearchParams({ page: '0', size: '7', sort: 'LATEST' })
  return api.get(`/api/products?${params.toString()}`)
}

export function getTrendingProducts() {
  const params = new URLSearchParams({ page: '0', size: '5', sort: 'POPULAR' })
  return api.get(`/api/products?${params.toString()}`)
}
