import { useState } from 'react'
import { Link } from 'react-router-dom'
import { toast } from 'sonner'
import { downloadPurchaseResource, openPurchaseResource, getPurchaseResources } from './purchaseApi.js'
import { usePurchasesQuery, usePurchaseMutation } from './usePurchase.js'
import { useAuthStore } from '../../common/store/useAuthStore.js'
import { PurchaseAccess } from './CartPage.jsx'

export function LibraryPage() {
  const accessToken = useAuthStore((state) => state.accessToken)
  const { data, isLoading, error } = usePurchasesQuery()
  const [resources, setResources] = useState({})
  const loadResources = usePurchaseMutation(async (orderItemId) => ({ orderItemId, data: await getPurchaseResources(orderItemId) }))
  const openResource = usePurchaseMutation(({ orderItemId, resourceId }) => openPurchaseResource(orderItemId, resourceId))
  const downloadResource = usePurchaseMutation(({ orderItemId, resourceId }) => downloadPurchaseResource(orderItemId, resourceId))
  if (!accessToken) return <PurchaseAccess title="내 라이브러리는 로그인 후 이용할 수 있습니다." />
  if (isLoading) return <main className="mx-auto max-w-4xl p-8">구매 자료를 불러오는 중입니다.</main>
  if (error) return <main className="mx-auto max-w-4xl p-8">라이브러리를 불러오지 못했습니다.</main>
  const products = data?.products ?? []
  const showResources = async (orderItemId) => { try { const result = await loadResources.mutateAsync(orderItemId); setResources((value) => ({ ...value, [orderItemId]: result.data.resources })) } catch (requestError) { toast.error(requestError.message) } }
  const open = async (orderItemId, resourceId) => { try { const result = await openResource.mutateAsync({ orderItemId, resourceId }); window.open(result.url, '_blank', 'noopener,noreferrer') } catch (requestError) { toast.error(requestError.message) } }
  const download = async (orderItemId, resource) => { try { const blob = await downloadResource.mutateAsync({ orderItemId, resourceId: resource.id }); const url = URL.createObjectURL(blob); const anchor = document.createElement('a'); anchor.href = url; anchor.download = resource.name; anchor.click(); URL.revokeObjectURL(url) } catch (requestError) { toast.error(requestError.message) } }
  return <main className="mx-auto max-w-4xl p-8"><div className="mb-8 flex items-end justify-between"><div><p className="text-sm font-bold tracking-widest text-emerald-800">LIBRARY</p><h1 className="text-3xl font-bold">내 라이브러리</h1></div><Link to="/cart" className="underline">장바구니</Link></div>{products.length ? <section className="space-y-4">{products.map((product) => <article key={product.orderItemId} className="rounded-xl border border-stone-300 bg-white p-6"><p className="text-sm text-stone-500">{product.sellerNickname} · 구매가 ₩ {Number(product.purchasedPrice).toLocaleString('ko-KR')}</p><h2 className="mt-1 text-xl font-bold">{product.name}</h2><button type="button" className="mt-4 rounded bg-stone-900 px-4 py-2 text-white" onClick={() => showResources(product.orderItemId)}>구매 자료 보기</button>{resources[product.orderItemId]?.length ? <ul className="mt-4 space-y-2">{resources[product.orderItemId].map((resource) => <li className="flex justify-between rounded bg-stone-100 p-3" key={resource.id}><span>{resource.name}</span><button className="underline" type="button" onClick={() => resource.type === 'FILE' ? download(product.orderItemId, resource) : open(product.orderItemId, resource.id)}>{resource.type === 'FILE' ? '다운로드' : '열기'}</button></li>)}</ul> : null}</article>)}</section> : <section className="rounded-xl border border-dashed border-stone-300 p-12 text-center">아직 구매한 상품이 없습니다. <Link className="underline" to="/products">상품 탐색하기</Link></section>}</main>
}
