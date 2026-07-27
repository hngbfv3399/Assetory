import { getApiData } from './client'

export function fetchCategories() {
  return getApiData('/api/categories')
}

export function fetchProducts({ categoryId, keyword, sort, page = 0, size = 12 }) {
  const params = new URLSearchParams({ page: String(page), size: String(size), sort })

  if (categoryId) {
    params.set('categoryId', String(categoryId))
  }
  if (keyword) {
    params.set('keyword', keyword)
  }

  return getApiData(`/api/products?${params.toString()}`)
}

export function fetchProduct(productId) {
  return getApiData(`/api/products/${productId}`)
}

export function fetchProductReviews(productId) {
  return getApiData(`/api/products/${productId}/reviews?sort=LATEST&page=0&size=10`)
}
