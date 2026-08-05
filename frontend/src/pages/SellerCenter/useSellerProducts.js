import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import * as api from './sellerProductApi.js'

const key = ['seller', 'products']
export const useSellerProductsQuery = (params) => useQuery({ queryKey: [...key, params], queryFn: () => api.getSellerProducts(params) })
export const useSellerProductQuery = (id) => useQuery({ queryKey: [...key, id], queryFn: () => api.getSellerProduct(id), enabled: Boolean(id) })
export function useSellerProductMutation(action) {
  const queryClient = useQueryClient()
  return useMutation({ mutationFn: action, onSuccess: () => queryClient.invalidateQueries({ queryKey: key }) })
}
