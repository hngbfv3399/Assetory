import { Star } from 'lucide-react'

const currencyFormatter = new Intl.NumberFormat('ko-KR')

export function TrendingProductCard({ product, rank }) {
  return (
    <article className="group relative overflow-hidden rounded-2xl border border-line bg-white p-4 transition-transform duration-300 hover:-translate-y-1">
      <span className="absolute right-4 top-4 text-sm font-black tracking-[-0.08em] text-brand/35">0{rank}</span>
      <div className="mb-4 aspect-[1.25] overflow-hidden rounded-xl bg-brand-soft">
        {product.thumbnailUrl ? <img className="h-full w-full object-cover transition duration-500 group-hover:scale-105" src={product.thumbnailUrl} alt="" /> : <div className="flex h-full items-center justify-center text-xs font-bold tracking-[0.18em] text-brand/45">TRENDING</div>}
      </div>
      <p className="m-0 truncate pr-8 text-xs text-muted">{product.sellerNickname}</p>
      <h3 className="mb-0 mt-1 truncate text-sm font-semibold tracking-[-0.03em]">{product.name}</h3>
      <div className="mt-4 flex items-center justify-between text-xs">
        <span className="flex items-center gap-1 text-muted"><Star className="fill-amber-400 text-amber-400" size={14} />{Number(product.averageRating ?? 0).toFixed(1)} · 후기 {product.reviewCount}</span>
        <strong>{currencyFormatter.format(product.price)}원</strong>
      </div>
    </article>
  )
}
