import { useNavigate, useSearchParams } from 'react-router-dom'

import { Button } from '../../common/ui/button.jsx'
import { Skeleton } from '../../common/ui/skeleton.jsx'
import { ProductCard } from '../../components/ProductCard.jsx'
import { useCategoriesQuery, useProductsQuery } from './hooks/useExploreCatalog.js'

const SORT_OPTIONS = [
  { value: 'LATEST', label: '최신순' },
  { value: 'POPULAR', label: '인기순' },
  { value: 'PRICE_LOW', label: '낮은 가격순' },
  { value: 'PRICE_HIGH', label: '높은 가격순' },
]

export function ExplorePage() {
  const navigate = useNavigate()
  const [searchParams, setSearchParams] = useSearchParams()

  // URL Search Params 기반 상태 파싱
  const categoryId = searchParams.get('categoryId') ? Number(searchParams.get('categoryId')) : null
  const keyword = searchParams.get('keyword') || ''
  const sort = searchParams.get('sort') || 'LATEST'
  const page = searchParams.get('page') ? Number(searchParams.get('page')) : 0

  // React Query 데이터 수급
  const { data: categories = [], isLoading: catLoading, error: catError } = useCategoriesQuery()
  const { data: catalog, isLoading: catalogLoading, error: catalogError } = useProductsQuery({
    categoryId,
    keyword,
    sort,
    page,
  })

  // 카테고리 전환 시 URL Params 업데이트
  function changeCategory(nextCategoryId) {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev)
      next.set('page', '0')
      if (nextCategoryId) {
        next.set('categoryId', String(nextCategoryId))
      } else {
        next.delete('categoryId')
      }
      return next
    })
  }

  // 정렬 기준 전환 시 URL Params 업데이트
  function changeSort(event) {
    const nextSort = event.target.value
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev)
      next.set('page', '0')
      next.set('sort', nextSort)
      return next
    })
  }

  // 페이지 전환 시 URL Params 업데이트
  function changePage(nextPage) {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev)
      next.set('page', String(nextPage))
      return next
    })
  }

  const errorMsg = catError?.message || catalogError?.message

  return (
    <main className="explore-page">

      {/* Hero 영역 */}
      <section className="hero" aria-labelledby="explore-heading">
        <span className="eyebrow">Digital Asset Market</span>
        <h1 id="explore-heading">
          아이디어를 완성하는<br />디지털 상품을 찾아보세요.
        </h1>
        <p>
          창작자에게는 더 넓은 판매 기회를, 구매자에게는 바로 쓸 수 있는 결과물을 제공합니다.
        </p>
      </section>

      {/* 카탈로그 브라우저 메인 그리드 */}
      <section className="catalog-browser" aria-label="상품 카탈로그 브라우저">

        {/* 상단 툴바: 카테고리 탭 (중복 검색 폼 제거완료 - 헤더 전담) */}
        <div className="catalog-toolbar">
          <div className="category-tabs" aria-label="카테고리 선택">
            <Button
              onClick={() => changeCategory(null)}
              variant="ghost"
              className={categoryId === null ? 'category-button is-active' : 'category-button'}
            >
              전체
            </Button>
            {catLoading && (
              <div className="flex gap-2 items-center">
                <Skeleton className="h-9 w-16 rounded-xl bg-neutral-300/30" />
                <Skeleton className="h-9 w-20 rounded-xl bg-neutral-300/30" />
                <Skeleton className="h-9 w-24 rounded-xl bg-neutral-300/30" />
              </div>
            )}
            {categories.map((category) => (
              <Button
                key={category.id}
                onClick={() => changeCategory(category.id)}
                variant="ghost"
                className={categoryId === category.id ? 'category-button is-active' : 'category-button'}
              >
                {category.name}
              </Button>
            ))}
          </div>
        </div>

        {/* 툴바 하단 카운트 및 정렬 */}
        <div className="catalog-status">
          <p>{catalog ? `총 ${catalog.totalElements}개의 상품` : '상품을 불러오는 중입니다.'}</p>
          <label>
            <span className="sr-only">정렬 기준</span>
            <select value={sort} onChange={changeSort}>
              {SORT_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
        </div>

        {errorMsg ? (
          <p className="text-center py-10 text-red-500 font-bold text-sm" role="alert">
            {errorMsg}
          </p>
        ) : null}

        {/* 로딩 시 럭셔리 스켈레톤 마운트 */}
        {catalogLoading ? (
          <div className="product-grid">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="product-card p-5 flex flex-col gap-4">
                <Skeleton className="w-full aspect-[1.65/1] rounded-[1.6rem] bg-neutral-300/30" />
                <Skeleton className="h-4 w-20 bg-neutral-300/30 rounded-lg" />
                <Skeleton className="h-6 w-full bg-neutral-300/30 rounded-lg" />
                <Skeleton className="h-4 w-2/3 bg-neutral-300/30 rounded-lg" />
                <div className="flex justify-between items-center mt-2">
                  <Skeleton className="h-4 w-12 bg-neutral-300/30 rounded-lg" />
                  <Skeleton className="h-6 w-24 bg-neutral-300/30 rounded-lg" />
                </div>
              </div>
            ))}
          </div>
        ) : (
          <>
            {!catalogLoading && catalog?.products.length === 0 ? (
              <p className="text-center py-20 text-sm font-bold text-neutral-400 select-none">
                조건에 맞는 상품이 없습니다.
              </p>
            ) : (
              <div className="product-grid">
                {catalog?.products.map((product) => (
                  <ProductCard
                    key={product.id}
                    product={product}
                    onSelect={(id) => navigate(`/products/${id}`)}
                  />
                ))}
              </div>
            )}
          </>
        )}

        {/* 뉴모피즘 페이지네이션 */}
        {catalog && catalog.totalPages > 1 ? (
          <nav className="pagination" aria-label="상품 목록 페이지">
            <Button
              disabled={page === 0}
              onClick={() => changePage(page - 1)}
              variant="ghost"
            >
              이전
            </Button>
            <span>{page + 1} / {catalog.totalPages}</span>
            <Button
              disabled={page + 1 === catalog.totalPages}
              onClick={() => changePage(page + 1)}
              variant="ghost"
            >
              다음
            </Button>
          </nav>
        ) : null}
      </section>
    </main>
  )
}
