const currencyFormatter = new Intl.NumberFormat('ko-KR')

export function ProductCard({ product, onSelect }) {
  return (
    <article className="product-card">
      <button className="product-card__button" type="button" onClick={() => onSelect(product.id)}>
        <div className="product-card__image-wrap">
          {product.thumbnailUrl ? (
            <img className="product-card__image" src={product.thumbnailUrl} alt="" />
          ) : (
            <div className="product-card__image product-card__image--empty">ASSET</div>
          )}
        </div>
        <div className="product-card__body">
          <p className="product-card__seller">{product.sellerNickname}</p>
          <h2>{product.name}</h2>
          <p className="product-card__summary">{product.summary}</p>
          <div className="product-card__meta">
            <span>★ {product.averageRating.toFixed(1)}</span>
            <span>후기 {product.reviewCount}</span>
          </div>
          <strong>{currencyFormatter.format(product.price)}원</strong>
        </div>
      </button>
    </article>
  )
}
