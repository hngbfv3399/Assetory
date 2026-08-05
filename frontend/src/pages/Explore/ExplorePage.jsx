import { useSearchParams } from 'react-router-dom'

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
  const activeRootId = categories.find((category) => (
    category.id === categoryId || category.children?.some((child) => child.id === categoryId)
  ))?.id ?? null
  const activeRootCategory = categories.find((category) => category.id === activeRootId)

  return (
    <main className="explore-page">
      {/* 카탈로그 브라우저 메인 그리드 */}
      <section className="catalog-browser" aria-label="상품 카탈로그 브라우저">

        {/* 선택한 상위 분류와 관련 세부 분류를 같은 목록 안에서 이어서 보여 줍니다. */}
        <div className="catalog-toolbar">
          <nav className="category-picker" aria-label="카테고리 선택">
            {catLoading && (
              <div className="category-picker__loading">
                {[...Array(4)].map((_, index) => (
                  <Skeleton key={index} className="h-11 w-28 rounded-full bg-neutral-300/30" />
                ))}
              </div>
            )}
            <div className="category-picker__items">
              <Button
                onClick={() => changeCategory(null)}
                variant="ghost"
                aria-pressed={categoryId === null}
                className={categoryId === null ? 'category-picker__item is-active' : 'category-picker__item'}
              >
                전체 상품
              </Button>
            {categories.map((category) => (
              <Button
                key={category.id}
                onClick={() => changeCategory(category.id)}
                variant="ghost"
                aria-pressed={activeRootId === category.id}
                className={activeRootId === category.id ? 'category-picker__item is-active' : 'category-picker__item'}
              >
                {category.name}
              </Button>
            ))}
            {activeRootCategory ? (
              <>
                <span className="category-picker__separator" aria-hidden="true" />
                <span className="category-picker__related-label">{activeRootCategory.name}의 세부 분류</span>
                {activeRootCategory.children?.map((child) => (
                  <Button
                    key={child.id}
                    onClick={() => changeCategory(child.id)}
                    variant="ghost"
                    aria-pressed={categoryId === child.id}
                    className={categoryId === child.id ? 'category-picker__item category-picker__item--related is-active' : 'category-picker__item category-picker__item--related'}
                  >
                    {child.name}
                  </Button>
                ))}
              </>
            ) : null}
            </div>
          </nav>
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

        <div className="catalog-results-main">
            {errorMsg ? (
              <p className="text-center py-10 text-red-500 font-bold text-sm" role="alert">
                {errorMsg}
              </p>
            ) : null}

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
                      />
                    ))}
                  </div>
                )}
              </>
            )}

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
        </div>
      </section>
    </main>
  )
}
