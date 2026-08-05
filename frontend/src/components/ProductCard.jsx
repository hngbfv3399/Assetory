import { Badge } from '../common/ui/badge.jsx'
import { Link } from 'react-router-dom'

const currencyFormatter = new Intl.NumberFormat('ko-KR')

export function ProductCard({ product }) {
  return (
    <article className="product-card">
      <Link
        className="product-card__button"
        to={`/products/${product.id}`}
      >
        <div className="product-card__image-wrap">
          {product.thumbnailUrl ? (
            <img
              className="product-card__image"
              src={product.thumbnailUrl}
              alt={`${product.name} 미리보기`}
              loading="lazy"
              onError={(event) => {
                event.currentTarget.hidden = true
                event.currentTarget.parentElement?.classList.add('has-image-error')
              }}
            />
          ) : (
            <div className="product-card__image product-card__image--empty">ASSET</div>
          )}
          {product.categoryName && (
            <Badge className="product-card__category absolute top-4 left-4 border-none shadow-sm select-none">
              {product.categoryName}
            </Badge>
          )}
        </div>

        <div className="product-card__body">
          <p className="product-card__seller">{product.sellerNickname}</p>
          <h2>{product.name}</h2>
          <p className="product-card__summary">{product.summary}</p>
          <div className="product-card__meta">
            <span>★ {product.averageRating.toFixed(1)}</span>
            <span className="text-neutral-300">|</span>
            <span>후기 {product.reviewCount}</span>
          </div>
          <strong>{currencyFormatter.format(product.price)}원</strong>
        </div>
      </Link>
    </article>
  )
}
