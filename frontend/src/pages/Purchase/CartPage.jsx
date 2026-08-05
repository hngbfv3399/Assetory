import { Link, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { createCartOrders, payMock, removeCartItem } from './purchaseApi.js'
import { useCartQuery, usePurchaseMutation } from './usePurchase.js'
import { useAuthStore } from '../../common/store/useAuthStore.js'

export function CartPage() {
  const navigate = useNavigate()
  const accessToken = useAuthStore((state) => state.accessToken)
  const { data: cart, isLoading, error } = useCartQuery()
  const removeMutation = usePurchaseMutation(removeCartItem)
  const checkoutMutation = usePurchaseMutation(async (productIds) => {
    const created = await createCartOrders(productIds)
    await Promise.all(created.orders.map((order) => payMock(order.orderId)))
  })

  if (!accessToken) return <PurchaseAccess title="장바구니는 로그인 후 이용할 수 있습니다." />
  if (isLoading) return <main className="mx-auto max-w-4xl p-8">장바구니를 불러오는 중입니다.</main>
  if (error) return <main className="mx-auto max-w-4xl p-8">장바구니를 불러오지 못했습니다.</main>
  const items = cart?.items ?? []
  const checkout = async () => {
    if (!items.length) return
    try { await checkoutMutation.mutateAsync(items.map((item) => item.productId)); toast.success('Mock 결제가 완료되었습니다.'); navigate('/library') } catch (requestError) { toast.error(requestError.message) }
  }
  return <main className="mx-auto max-w-4xl p-8"><div className="mb-8 flex items-end justify-between"><div><p className="text-sm font-bold tracking-widest text-emerald-800">CART</p><h1 className="text-3xl font-bold">장바구니</h1></div><Link to="/library" className="underline">내 라이브러리</Link></div>{items.length ? <><section className="space-y-3">{items.map((item) => <article key={item.cartItemId} className="flex items-center justify-between rounded-xl border border-stone-300 bg-white p-5"><div><p className="text-sm text-stone-500">{item.sellerNickname}</p><h2 className="font-bold">{item.name}</h2><strong>₩ {Number(item.price).toLocaleString('ko-KR')}</strong></div><button type="button" className="underline" onClick={() => removeMutation.mutate(item.cartItemId, { onError: (requestError) => toast.error(requestError.message) })}>삭제</button></article>)}</section><section className="mt-6 flex items-center justify-between rounded-xl bg-stone-900 p-6 text-white"><strong>합계 ₩ {Number(cart.totalPrice).toLocaleString('ko-KR')}</strong><button type="button" className="rounded bg-lime-300 px-5 py-3 font-bold text-stone-950" disabled={checkoutMutation.isPending} onClick={checkout}>{checkoutMutation.isPending ? '결제 처리 중' : 'Mock 결제하기'}</button></section></> : <section className="rounded-xl border border-dashed border-stone-300 p-12 text-center">장바구니가 비어 있습니다. <Link className="underline" to="/products">상품 탐색하기</Link></section>}</main>
}

export function PurchaseAccess({ title }) { return <main className="mx-auto max-w-xl p-16 text-center"><h1 className="text-2xl font-bold">{title}</h1><Link className="mt-6 inline-block rounded bg-stone-900 px-5 py-3 text-white" to="/login" state={{ from: '/cart' }}>로그인하기</Link></main> }
