import { useEffect, useState } from 'react'

import { fetchCategories, fetchProduct, fetchProductReviews, fetchProducts } from '../api/catalog'
import { ProductCard } from '../components/ProductCard'
import { ProductDetailPanel } from '../components/ProductDetailPanel'

const SORT_OPTIONS = [
  { value: 'LATEST', label: '최신순' },
  { value: 'POPULAR', label: '인기순' },
  { value: 'PRICE_LOW', label: '낮은 가격순' },
  { value: 'PRICE_HIGH', label: '높은 가격순' },
]

export function ExplorePage() {
  const [categories, setCategories] = useState([])
  const [catalog, setCatalog] = useState(null)
  const [categoryId, setCategoryId] = useState(null)
  const [keywordInput, setKeywordInput] = useState('')
  const [keyword, setKeyword] = useState('')
  const [sort, setSort] = useState('LATEST')
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedProduct, setSelectedProduct] = useState(null)
  const [reviews, setReviews] = useState([])
  const [detailLoading, setDetailLoading] = useState(false)

  useEffect(() => {
    let active = true

    fetchCategories()
      .then((data) => active && setCategories(data))
      .catch(() => active && setError('카테고리를 불러오지 못했습니다. 백엔드 실행 상태를 확인해 주세요.'))

    return () => {
      active = false
    }
  }, [])

  useEffect(() => {
    let active = true
    setLoading(true)
    setError('')

    fetchProducts({ categoryId, keyword, sort, page })
      .then((data) => {
        if (active) {
          setCatalog(data)
        }
      })
      .catch(() => {
        if (active) {
          setError('상품 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.')
        }
      })
      .finally(() => active && setLoading(false))

    return () => {
      active = false
    }
  }, [categoryId, keyword, sort, page])

  function applySearch(event) {
    event.preventDefault()
    setPage(0)
    setKeyword(keywordInput.trim())
  }

  function changeCategory(nextCategoryId) {
    setCategoryId(nextCategoryId)
    setPage(0)
  }

  function changeSort(event) {
    setSort(event.target.value)
    setPage(0)
  }

  async function selectProduct(productId) {
    setDetailLoading(true)
    setError('')

    try {
      const [product, reviewData] = await Promise.all([
        fetchProduct(productId),
        fetchProductReviews(productId),
      ])
      setSelectedProduct(product)
      setReviews(reviewData.reviews)
    } catch {
      setError('상품 상세 정보를 불러오지 못했습니다.')
    } finally {
      setDetailLoading(false)
    }
  }

  function closeDetail() {
    setSelectedProduct(null)
    setReviews([])
  }

  return (
    <main className="explore-page">
      <header className="site-header">
        <a className="brand" href="/" aria-label="Assetory 홈">Assetory</a>
        <span>디지털 상품 마켓</span>
      </header>

      <section className="hero" aria-labelledby="explore-heading">
        <p className="eyebrow">DIGITAL ASSET MARKET</p>
        <h1 id="explore-heading">아이디어를 완성하는<br />디지털 상품을 찾아보세요.</h1>
        <p>창작자에게는 더 넓은 판매 기회를, 구매자에게는 바로 쓸 수 있는 결과물을 제공합니다.</p>
      </section>

      <section className="catalog-section" aria-label="상품 탐색">
        <div className="catalog-toolbar">
          <div className="category-list" aria-label="카테고리 필터">
            <button
              className={categoryId === null ? 'category-button is-active' : 'category-button'}
              type="button"
              onClick={() => changeCategory(null)}
            >
              전체
            </button>
            {categories.map((category) => (
              <button
                className={categoryId === category.id ? 'category-button is-active' : 'category-button'}
                key={category.id}
                type="button"
                onClick={() => changeCategory(category.id)}
              >
                {category.name}
              </button>
            ))}
          </div>

          <form className="search-form" onSubmit={applySearch}>
            <label className="sr-only" htmlFor="product-search">상품 검색</label>
            <input
              id="product-search"
              value={keywordInput}
              onChange={(event) => setKeywordInput(event.target.value)}
              placeholder="상품명 또는 소개 검색"
            />
            <button type="submit">검색</button>
          </form>
        </div>

        <div className="catalog-status">
          <p>{catalog ? `총 ${catalog.totalElements}개의 상품` : '상품을 불러오는 중입니다.'}</p>
          <label>
            <span className="sr-only">정렬 기준</span>
            <select value={sort} onChange={changeSort}>
              {SORT_OPTIONS.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
            </select>
          </label>
        </div>

        {error ? <p className="feedback feedback--error" role="alert">{error}</p> : null}
        {detailLoading ? <p className="feedback">상품 상세를 불러오는 중입니다.</p> : null}
        {loading ? <p className="feedback">상품 목록을 불러오는 중입니다.</p> : null}

        {!loading && catalog?.products.length === 0 ? (
          <p className="feedback">조건에 맞는 상품이 없습니다.</p>
        ) : null}

        <div className="product-grid">
          {catalog?.products.map((product) => (
            <ProductCard key={product.id} product={product} onSelect={selectProduct} />
          ))}
        </div>

        {catalog && catalog.totalPages > 1 ? (
          <nav className="pagination" aria-label="상품 목록 페이지">
            <button type="button" disabled={page === 0} onClick={() => setPage(page - 1)}>이전</button>
            <span>{page + 1} / {catalog.totalPages}</span>
            <button type="button" disabled={page + 1 === catalog.totalPages} onClick={() => setPage(page + 1)}>다음</button>
          </nav>
        ) : null}
      </section>

      {selectedProduct ? (
        <div className="detail-backdrop" role="presentation" onMouseDown={closeDetail}>
          <div role="presentation" onMouseDown={(event) => event.stopPropagation()}>
            <ProductDetailPanel product={selectedProduct} reviews={reviews} onClose={closeDetail} />
          </div>
        </div>
      ) : null}
    </main>
  )
}
