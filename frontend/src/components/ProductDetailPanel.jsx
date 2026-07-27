const currencyFormatter = new Intl.NumberFormat('ko-KR')

export function ProductDetailPanel({ product, reviews, onClose }) {
  return (
    <aside className="detail-panel" aria-label="상품 상세">
      <div className="detail-panel__header">
        <p>상품 상세</p>
        <button className="icon-button" type="button" onClick={onClose} aria-label="상품 상세 닫기">
          ×
        </button>
      </div>

      <div className="detail-panel__content">
        {product.images[0]?.imageUrl ? (
          <img className="detail-panel__image" src={product.images[0].imageUrl} alt="" />
        ) : null}
        <p className="detail-panel__category">{product.category.name}</p>
        <h2>{product.name}</h2>
        <p className="detail-panel__seller">{product.seller.nickname}</p>
        <p className="detail-panel__description">{product.description}</p>
        <strong className="detail-panel__price">{currencyFormatter.format(product.price)}원</strong>

        <section className="review-section" aria-labelledby="review-heading">
          <div className="review-section__heading">
            <h3 id="review-heading">구매자 후기</h3>
            <span>★ {product.averageRating.toFixed(1)} · {product.reviewCount}개</span>
          </div>
          {reviews.length === 0 ? (
            <p className="empty-review">아직 작성된 후기가 없습니다.</p>
          ) : (
            <ul className="review-list">
              {reviews.map((review) => (
                <li key={review.id}>
                  <div>
                    <strong>{review.writerNickname}</strong>
                    <span>★ {review.rating.toFixed(1)}</span>
                  </div>
                  <p>{review.content}</p>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </aside>
  )
}
