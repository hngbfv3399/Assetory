import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useAuthStore } from '../../common/store/useAuthStore.js'
import * as authApi from './authApi.js'

export function useSessionBootstrap() {
  const setSession = useAuthStore((state) => state.setSession)
  const clearSession = useAuthStore((state) => state.clearSession)
  return useQuery({ queryKey: ['auth', 'session'], retry: false, queryFn: async () => {
    try {
      const refreshed = await authApi.refresh()
      setSession({ accessToken: refreshed.accessToken, user: null })
      const user = await authApi.getMyProfile()
      setSession({ accessToken: refreshed.accessToken, user })
      return user
    } catch {
      // 로그인 mutation이 진행 중이면 이전 refresh 요청의 실패가 새 세션을 지우지 않게 한다.
      if (!useAuthStore.getState().accessToken) clearSession()
      return null
    }
  } })
}

export function useLoginMutation() {
  const setSession = useAuthStore((state) => state.setSession)
  return useMutation({ mutationFn: authApi.login, onSuccess: (data) => setSession(data) })
}

export function useSignupMutation() { return useMutation({ mutationFn: authApi.signup }) }

export function useLogoutMutation() {
  const clearSession = useAuthStore((state) => state.clearSession)
  const queryClient = useQueryClient()
  return useMutation({ mutationFn: authApi.logout, onSettled: () => { clearSession(); queryClient.clear() } })
}
