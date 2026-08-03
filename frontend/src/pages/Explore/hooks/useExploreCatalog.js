import { useQuery } from '@tanstack/react-query'

import { fetchCategories, fetchProduct, fetchProductReviews, fetchProducts } from '../../../api/catalog.js'

// 1. 카테고리 목록 쿼리 훅
export function useCategoriesQuery() {
  return useQuery({
    queryKey: ['categories'],
    queryFn: fetchCategories,
    staleTime: 1000 * 60 * 5, // 5분
  })
}

// 2. 상품 그리드 목록 쿼리 훅
export function useProductsQuery({ categoryId, keyword, sort, page }) {
  return useQuery({
    queryKey: ['products', { categoryId, keyword, sort, page }],
    queryFn: () => fetchProducts({ categoryId, keyword, sort, page }),
    placeholderData: (previousData) => previousData, // 페이지네이션 중 깜빡임 방지
    staleTime: 1000 * 60 * 1, // 1분
  })
}

// 3. 상품 상세 및 리뷰 병렬 결합 쿼리 훅
export function useProductDetailQuery(productId) {
  return useQuery({
    queryKey: ['productDetail', productId],
    queryFn: async () => {
      const [product, reviewData] = await Promise.all([
        fetchProduct(productId),
        fetchProductReviews(productId),
      ])
      return {
        product,
        reviews: reviewData.reviews || [],
      }
    },
    enabled: !!productId,
    staleTime: 1000 * 60 * 2, // 2분
  })
}
