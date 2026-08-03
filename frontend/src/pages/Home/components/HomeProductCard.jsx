import { Star } from 'lucide-react'
import { Link } from 'react-router-dom'

const currencyFormatter = new Intl.NumberFormat('ko-KR')

export function HomeProductCard({ product, index }) {
  const hue = ["#dfe9e2", "#e9e0d8", "#dde5ef", "#e9e4d3"][index % 4]

  return (
    <Link className="home-product-card group overflow-hidden rounded-2xl bg-white" to={`/products/${product.id}`}>
      <div className="aspect-[1.12] overflow-hidden" style={{ backgroundColor: hue }}>
        {product.thumbnailUrl ? (
          <img className="h-full w-full object-cover transition duration-500 group-hover:scale-105" src={product.thumbnailUrl} alt="" />
        ) : (
          <div className="flex h-full items-end p-6 text-2xl font-semibold tracking-[-0.06em] text-brand/45">ASSETORY<br />COLLECTION</div>
        )}
      </div>
      <div className="p-5">
        <p className="m-0 text-xs font-medium text-muted">{product.sellerNickname}</p>
        <h2 className="mt-2 truncate text-base font-semibold tracking-[-0.03em]">{product.name}</h2>
        <div className="mt-4 flex items-center justify-between gap-2">
          <div className="flex items-center gap-1 text-sm text-muted"><Star className="fill-amber-400 text-amber-400" size={15} />{Number(product.averageRating ?? 0).toFixed(1)}</div>
          <strong className="text-sm">{currencyFormatter.format(product.price)}원</strong>
        </div>
      </div>
    </Link>
  )
}
