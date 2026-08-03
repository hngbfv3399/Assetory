import { Toaster } from 'sonner'
import { Link, Outlet, useLocation, useSearchParams } from 'react-router-dom'
import { useState, useEffect } from 'react'

export function RootLayout() {
  const { pathname } = useLocation()
  const [searchParams, setSearchParams] = useSearchParams()

  // URL 'keyword' 파라미터 감지 및 로컬 입력 동기화
  const keyword = searchParams.get('keyword') || ''
  const [keywordInput, setKeywordInput] = useState(keyword)

  useEffect(() => {
    setKeywordInput(keyword)
  }, [keyword])

  // 헤더 검색 실행 핸들러
  function handleSearchSubmit(e) {
    e.preventDefault()
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev)
      next.set('page', '0')
      if (keywordInput.trim()) {
        next.set('keyword', keywordInput.trim())
      } else {
        next.delete('keyword')
      }
      return next
    })
  }

  // '/products' 목록 페이지를 제외한 모든 개별 상품 상세/판매(/product/123, /products/123) 페이지는 헤더 메뉴를 감추고 로고만 보여줍니다.
  const isProductPage = pathname.startsWith('/product') && pathname !== '/products'
  const isExplorePage = pathname === '/products'

  // 현재 활성화된 페이지 탭 검사 (Active State)
  const isHomeActive = pathname === '/'
  const isExploreActive = pathname === '/products'
  const isFeaturesActive = pathname === '/features'
  const isPricingActive = pathname === '/pricing'

  return (
    <div className="min-h-screen bg-[#ECEFF2]">
      <header className={`site-header-nav ${isHomeActive ? 'site-header-nav--home' : ''}`}>
        <Link className="brand-logo" to="/">Assetory</Link>

        {/* 1. 상품 상세 페이지 (Focus Mode) ➡️ 아무것도 안 보여줌 (로고만) */}

        {/* 2. 상품 탐색 페이지 (Explore Mode) ➡️ 검색창 통합 */}
        {isExplorePage && (
          <>
            <form className="header-search-form" onSubmit={handleSearchSubmit}>
              <label className="sr-only" htmlFor="header-product-search">상품 검색</label>
              <input
                id="header-product-search"
                value={keywordInput}
                onChange={(e) => setKeywordInput(e.target.value)}
                placeholder="에셋, 개발 코드, 템플릿 검색..."
              />
              <button type="submit" className="header-search-btn">
                🔍
              </button>
            </form>
            <Link className="login-button-link" to="/login">
              로그인 / 회원가입
            </Link>
          </>
        )}

        {/* 3. 일반 안내 페이지 ➡️ 4단 메뉴바 노출 */}
        {!isProductPage && !isExplorePage && (
          <nav className="header-nav-menu" aria-label="주요 메뉴">
            <Link
              className={`menu-item ${isHomeActive ? 'active-menu-item' : ''}`}
              to="/"
            >
              홈
            </Link>
            <Link
              className={`menu-item ${isExploreActive ? 'active-menu-item' : ''}`}
              to="/products"
            >
              상품 탐색
            </Link>
            <Link
              className={`menu-item ${isFeaturesActive ? 'active-menu-item' : ''}`}
              to="/features"
            >
              주요 기능
            </Link>
            <Link
              className={`menu-item ${isPricingActive ? 'active-menu-item' : ''}`}
              to="/pricing"
            >
              가격 정책
            </Link>
            <Link className="login-button-link" to="/login">
              로그인 / 회원가입
            </Link>
          </nav>
        )}
      </header>
      <div className="pt-20">
        <Outlet />
      </div>
      <Toaster position="top-center" richColors />
    </div>
  )
}
