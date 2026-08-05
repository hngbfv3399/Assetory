import { Toaster } from 'sonner'
import { Link, Outlet, useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { SiteFooter } from '../common/ui/SiteFooter.jsx'
import { useAuthStore } from '../common/store/useAuthStore.js'
import { useLogoutMutation } from '../pages/Login/useAuthSession.js'

export function RootLayout() {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const accessToken = useAuthStore((state) => state.accessToken)
  const logoutMutation = useLogoutMutation()
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

  const isExplorePage = pathname === '/products'

  // 현재 활성화된 페이지 탭 검사 (Active State)
  const isHomeActive = pathname === '/'
  const isExploreActive = pathname === '/products'
  const isFeaturesActive = pathname === '/features'
  const isPricingActive = pathname === '/pricing'
  const isSellerActive = pathname === '/seller'
  const isSellerStudio = pathname.startsWith('/seller')

  const logout = async () => {
    await logoutMutation.mutateAsync()
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-[#fbf8ef]">
      {!isSellerStudio ? <header className="site-header-nav site-header-nav--brand">
        <Link className="brand-logo" to="/">Assetory</Link>

        {/* 상품 탐색에서는 검색을 헤더에 통합합니다. */}
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
            {accessToken ? <button type="button" className="login-button-link" onClick={logout}>로그아웃</button> : <Link className="login-button-link" to="/login">로그인 / 회원가입</Link>}
          </>
        )}

        {/* 그 외 화면은 같은 공통 메뉴를 사용합니다. */}
        {!isExplorePage && (
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
            <Link
              className={`menu-item ${isSellerActive ? 'active-menu-item' : ''}`}
              to="/seller"
            >
              판매자 센터
            </Link>
            {accessToken ? <button type="button" className="login-button-link" onClick={logout}>로그아웃</button> : <Link className="login-button-link" to="/login">로그인 / 회원가입</Link>}
          </nav>
        )}
      </header> : null}
      <div className={isSellerStudio ? '' : 'pt-20'}>
        <Outlet />
      </div>
      {!isSellerStudio ? <SiteFooter /> : null}
      <Toaster position="top-center" richColors />
    </div>
  )
}
