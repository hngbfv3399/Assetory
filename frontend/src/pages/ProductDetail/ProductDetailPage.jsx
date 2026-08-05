import { useNavigate, useParams, Link } from 'react-router-dom'
import { useProductDetailQuery } from '../Explore/hooks/useExploreCatalog.js'
import { Skeleton } from '../../common/ui/skeleton.jsx'
import { Badge } from '../../common/ui/badge.jsx'
import { toast } from 'sonner'
import { useAuthStore } from '../../common/store/useAuthStore.js'
import { addCartItem, createDirectOrder, payMock } from '../Purchase/purchaseApi.js'
import { usePurchaseMutation } from '../Purchase/usePurchase.js'

const currencyFormatter = new Intl.NumberFormat('ko-KR')

// 이미지 주소 유효성 검사 헬퍼 (콘솔 에러 방어)
const isValidImage = (url) => {
  return url && !url.includes('example.com') && url.startsWith('http')
}

export function ProductDetailPage() {
  const { productId } = useParams()
  const navigate = useNavigate()
  const accessToken = useAuthStore((state) => state.accessToken)
  const { data, isLoading, error } = useProductDetailQuery(productId ? Number(productId) : null)
  const addCartMutation = usePurchaseMutation(addCartItem)
  const buyMutation = usePurchaseMutation(async (id) => {
    const order = await createDirectOrder(id)
    await Promise.all(order.orders.map((created) => payMock(created.orderId)))
  })

  async function handleMockBuy() {
    if (!accessToken) { navigate('/login', { state: { from: `/products/${productId}` } }); return }
    try { await buyMutation.mutateAsync(Number(productId)); toast.success('Mock 결제가 완료되었습니다. 내 라이브러리에서 자료를 이용하세요.'); navigate('/library') } catch (requestError) { toast.error(requestError.message) }
  }

  async function handleAddCart() {
    if (!accessToken) { navigate('/login', { state: { from: `/products/${productId}` } }); return }
    try { await addCartMutation.mutateAsync(Number(productId)); toast.success('장바구니에 담았습니다.') } catch (requestError) { toast.error(requestError.message) }
  }

  if (isLoading) {
    return (
      <div className="product-detail-page-container">
        <div className="detail-skeleton-wrapper">
          <Skeleton className="h-[300px] w-full rounded-[2rem] bg-neutral-200" />
          <Skeleton className="h-[40px] w-[150px] mt-6 bg-neutral-200" />
          <Skeleton className="h-[30px] w-[300px] mt-4 bg-neutral-200" />
          <Skeleton className="h-[200px] w-full mt-6 bg-neutral-200" />
        </div>
      </div>
    )
  }

  if (error || !data || !data.product) {
    return (
      <div className="product-detail-page-container text-center py-20">
        <h2 className="text-xl font-bold text-red-500">상품 정보를 불러오는데 실패했습니다.</h2>
        <Link to="/products" className="mt-6 inline-block text-brand font-bold">
          상품 탐색 페이지로 돌아가기
        </Link>
      </div>
    )
  }

  const { product, reviews = [] } = data

  return (
    <main className="product-detail-page-container">
      {/* 뒤로가기 브레드크럼 */}
      <div className="detail-page-breadcrumb">
        <Link to="/products" className="breadcrumb-back-link">
          &larr; 상품 목록으로 돌아가기
        </Link>
      </div>

      <div className="detail-main-layout">

        {/* 왼쪽: 3D 액자식 이미지 렌더러 */}
        <div className="detail-image-deck">
          {isValidImage(product.images && product.images[0]?.imageUrl) ? (
            <img
              className="detail-main-img"
              src={product.images[0].imageUrl}
              alt={product.name}
            />
          ) : (
            <div className="detail-no-img-holder">
              <span className="no-img-label">ASSET</span>
            </div>
          )}
        </div>

        {/* 오른쪽: 상세 스펙 및 3D 결제 카드 */}
        <div className="detail-specs-deck">
          {product.category?.name && (
            <Badge className="detail-category-badge">
              {product.category.name}
            </Badge>
          )}

          <h1 className="detail-product-title">{product.name}</h1>
          <p className="detail-seller-info">판매자: <span className="seller-name">{product.seller?.nickname}</span></p>

          <div className="detail-price-slot">
            <span className="price-label">판매가</span>
            <strong className="price-value">{currencyFormatter.format(product.price)}원</strong>
          </div>

          <p className="detail-product-desc">{product.description}</p>

          <div className="detail-cta-box">
            <button type="button" className="detail-buy-btn" disabled={buyMutation.isPending} onClick={handleMockBuy}>
              {buyMutation.isPending ? '결제 처리 중' : 'Mock 결제로 구매하기'}
            </button>
            <button type="button" className="mt-3 w-full rounded-xl border border-stone-400 px-4 py-3 font-bold" disabled={addCartMutation.isPending} onClick={handleAddCart}>장바구니에 담기</button>
          </div>
        </div>

      </div>

      <section className="product-selling-guide" aria-labelledby="product-selling-guide-title">
        <div className="product-selling-guide__heading">
          <span>HOW IT WORKS</span>
          <h2 id="product-selling-guide-title">이 상품은 이렇게 판매돼요</h2>
          <p>구매 전에 결제 방식과 이용 흐름을 확인하세요.</p>
        </div>
        <div className="product-selling-guide__items">
          <article>
            <span>01 · 판매 방식</span>
            <h3>디지털 상품을 한 번 구매해요</h3>
            <p>배송 없이 상품 소개와 구매 자료를 확인하는 디지털 상품입니다.</p>
          </article>
          <article>
            <span>02 · 결제 방식</span>
            <h3>Mock 결제로 주문을 확인해요</h3>
            <p>현재 서비스는 Mock 결제만 제공합니다. 실제 청구나 송금은 진행되지 않습니다.</p>
          </article>
          <article>
            <span>03 · 이용 방식</span>
            <h3>결제 후 내 라이브러리에서 이용해요</h3>
            <p>결제가 완료되면 구매 자료 접근 권한이 부여되어 필요할 때 다시 이용할 수 있습니다.</p>
          </article>
        </div>
      </section>

      {/* 하단부: 구매 후기 섹션 */}
      <section className="detail-reviews-container" aria-labelledby="detail-review-heading">
        <div className="reviews-section-header">
          <h2 id="detail-review-heading">구매자 평가 및 후기</h2>
          <div className="reviews-average-rating">
            <span className="star-icon">★</span>
            <strong className="rating-score">{product.averageRating.toFixed(1)}</strong>
            <span className="rating-count">({product.reviewCount}개의 후기)</span>
          </div>
        </div>

        {reviews.length === 0 ? (
          <div className="empty-reviews-neomorphic">
            <p className="empty-text">아직 작성된 에셋 평가 후기가 없습니다.</p>
          </div>
        ) : (
          <div className="reviews-grid-list">
            {reviews.map((review) => (
              <div key={review.id} className="review-neomorphic-card-item">
                <div className="review-card-header">
                  <strong className="reviewer-name">{review.writerNickname}</strong>
                  <span className="reviewer-star">★ {review.rating.toFixed(1)}</span>
                </div>
                <p className="review-card-content">{review.content}</p>
              </div>
            ))}
          </div>
        )}
      </section>

    </main>
  )
}
