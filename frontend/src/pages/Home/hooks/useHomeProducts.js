import { useQuery } from '@tanstack/react-query'

import { queryKeys } from '../../../common/constants/queryKeys.js'
import { getHomeProducts, getTrendingProducts } from '../api/homeApi.js'

const homeParams = { page: 0, size: 7, sort: 'LATEST' }

export function useHomeProducts() {
  return useQuery({
    queryKey: queryKeys.home.products(homeParams),
    queryFn: getHomeProducts,
  })
}

export function useTrendingProducts() {
  return useQuery({
    queryKey: queryKeys.home.trending(),
    queryFn: getTrendingProducts,
  })
}
