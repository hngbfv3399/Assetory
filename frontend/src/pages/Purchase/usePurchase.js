import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import * as api from './purchaseApi.js'

const cartKey = ['buyer', 'cart']
const purchaseKey = ['buyer', 'purchases']

export const useCartQuery = () => useQuery({ queryKey: cartKey, queryFn: api.getCart })
export const usePurchasesQuery = () => useQuery({ queryKey: purchaseKey, queryFn: api.getPurchases })

export function usePurchaseMutation(action) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: action,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: cartKey })
      queryClient.invalidateQueries({ queryKey: purchaseKey })
    },
  })
}
