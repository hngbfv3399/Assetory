import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

import { HomeProductCard } from './HomeProductCard.jsx'
import { HomeProductSkeleton } from './HomeProductSkeleton.jsx'

const coreValues = [
  {
    step: '01',
    audience: '판매자',
    title: '상품을 올리고',
    description: <>코드, 템플릿, 디자인, 문서를 하나의 디지털 상품으로<br />선보입니다.</>,
    icon: 'file',
  },
  {
    step: '02',
    audience: '판매자',
    title: '판매 흐름을 보고',
    description: <>주문과 판매 현황, 예상 정산을 확인하며<br />다음 창작의 방향을 찾습니다.</>,
    icon: 'chart',
  },
  {
    step: '03',
    audience: '구매자',
    title: '필요한 에셋을 찾고',
    description: <>카테고리와 검색을 통해 지금 작업에 필요한<br />디지털 에셋을 발견합니다.</>,
    icon: 'plane',
  },
  {
    step: '04',
    audience: '구매자',
    title: <>내 라이브러리에<br />담고</>,
    description: <>구매한 자료를 한곳에서 확인하고,<br />필요할 때 다시 이용합니다.</>,
    icon: 'library',
  },
  {
    step: '05',
    audience: '함께',
    title: '거래 후에도 이어지고',
    description: '판매자는 수익을 만들고, 구매자는 시간을 아끼는 건강한 거래를 이어갑니다.',
    icon: 'access',
  },
]

function CoreValueIcon({ type }) {
  if (type === 'plane') {
    return (
      <svg viewBox="0 0 32 32" fill="none" aria-hidden="true">
        <path d="M28 4 16.7 27.1l-4.2-10.8L3.9 12.1 28 4Z" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
        <path d="m12.5 16.3 7.3-6.1" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
      </svg>
    )
  }

  if (type === 'chart') {
    return (
      <svg viewBox="0 0 32 32" fill="none" aria-hidden="true">
        <path d="M5 25V18m7 7V12m7 13V16m7 9V7" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" />
        <path d="m20 7 6-1-1 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    )
  }

  if (type === 'access') {
    return (
      <svg viewBox="0 0 32 32" fill="none" aria-hidden="true">
        <path d="M5 10.5h22v15H5zM10 10.5V7h12v3.5" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
        <path d="M10 17h12M10 21h7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
      </svg>
    )
  }

  if (type === 'library') {
    return (
      <svg viewBox="0 0 32 32" fill="none" aria-hidden="true">
        <path d="M6 7.5h6v17H6zm7 0h6v17h-6zm7 0h6v17h-6z" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
        <path d="M8.5 11h1m6 0h1m6 0h1" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
      </svg>
    )
  }

  return (
    <svg viewBox="0 0 32 32" fill="none" aria-hidden="true">
      <path d="M9 4.5h10l5 5v18H9a2 2 0 0 1-2-2v-19a2 2 0 0 1 2-2Z" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
      <path d="M19 4.5v6h5M11.5 16h9m-9 4.5h9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
    </svg>
  )
}

export function HomeIntro({ products, isProductsLoading }) {
  const navigate = useNavigate()
  const [keyword, setKeyword] = useState('')

  function handleMarketplaceSearch(event) {
    event.preventDefault()
    const params = new URLSearchParams()
    if (keyword.trim()) {
      params.set('keyword', keyword.trim())
    }
    navigate(`/products${params.size ? `?${params.toString()}` : ''}`)
  }

  const marqueeProducts = products.length > 0 ? [...products, ...products] : []

  return (
    <div className="static-intro">
      {/* 1. Hero Section */}
      <section className="intro-hero" aria-labelledby="intro-hero-heading">
        <div className="hero-glow-bg" />
        <div className="hero-transfer-visual" aria-hidden="true">
          <div className="hero-role-panel hero-role-panel--seller">
            <span className="hero-role-panel__label">SELLER</span>
            <strong>상품 등록</strong>
            <span>디자인 시스템 템플릿</span>
            <em>₩ 12,000</em>
          </div>
          <div className="hero-file-card hero-file-card--code">
            <span className="hero-file-card__icon">&lt;/&gt;</span>
            <span>CODE</span>
          </div>
          <div className="hero-file-card hero-file-card--template">
            <span className="hero-file-card__icon">✦</span>
            <span>TEMPLATE</span>
          </div>
          <svg className="hero-transfer-path" viewBox="0 0 760 300" fill="none" preserveAspectRatio="none">
            <path d="M 82 210 C 210 42, 454 290, 678 94" pathLength="100" />
          </svg>
          <div className="hero-paper-plane">
            <svg viewBox="0 0 32 32" fill="none" aria-hidden="true">
              <path d="M28 4 16.7 27.1l-4.2-10.8L3.9 12.1 28 4Z" fill="currentColor" stroke="currentColor" strokeLinejoin="round" />
              <path d="m12.5 16.3 7.3-6.1" stroke="#ECEFF2" strokeWidth="2" strokeLinecap="round" />
            </svg>
          </div>
          <div className="hero-sale-card">
            <span className="hero-sale-card__check">✓</span>
            <span>판매 완료</span>
          </div>
          <div className="hero-role-panel hero-role-panel--buyer">
            <span className="hero-role-panel__label">BUYER</span>
            <strong>내 라이브러리</strong>
            <span>다운로드 준비됨</span>
            <em>✓ 이용 가능</em>
          </div>
        </div>
        <span className="intro-eyebrow">Premium Digital Asset Hub</span>
        <h1 id="intro-hero-heading" className="intro-title">
          작은 작업 하나가 수익이 되고,<br />{' '}
          누군가의 긴 작업 시간을 덜어줍니다.
        </h1>
        <p className="intro-subtitle">
          에셋부터 코드, DB 구조도까지. 당신의 아이디어 한 조각을 가치로 바꾸어 보세요.
        </p>
        <form className="hero-market-search" onSubmit={handleMarketplaceSearch}>
          <label className="sr-only" htmlFor="home-marketplace-search">마켓 상품 검색</label>
          <input
            id="home-marketplace-search"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="코드, 템플릿, 디자인 에셋 검색"
          />
          <button type="submit">에셋 찾기 <span aria-hidden="true">→</span></button>
        </form>
        <div className="intro-cta-box">
          <Link to="/products" className="cta-primary-btn">
            마켓플레이스 입장하기 &rarr;
          </Link>
          <Link to="/login" className="cta-secondary-btn">
            지금 시작하기
          </Link>
        </div>
      </section>

      <section className="core-values-section" aria-labelledby="core-values-heading">
        <div className="core-values-heading">
          <span className="section-eyebrow">The Assetory Flow</span>
          <h2 id="core-values-heading" className="core-values-title">
            창작자는 수익을 만들고,<br />구매자는 시간을 얻습니다.
          </h2>
          <p className="core-values-description">
            한쪽만을 위한 마켓이 아니라, 판매와 구매가 모두 편해지는 디지털 자산의 흐름입니다.
          </p>
        </div>

        <div className="core-values-grid">
          {coreValues.map((value) => (
            <article className={`core-value-card core-value-card--${value.icon}`} key={value.step}>
              <div className="core-value-copy">
                <span className="core-value-step">{value.audience} · {value.step}</span>
                <h3>{value.title}</h3>
                <p>{value.description}</p>
              </div>
              <div className="core-value-visual" aria-hidden="true">
                <div className="core-value-icon"><CoreValueIcon type={value.icon} /></div>
                {value.icon === 'file' && (
                  <div className="core-visual-files">
                    <span>CODE</span><span>DESIGN</span><span>TEMPLATE</span>
                  </div>
                )}
                {value.icon === 'plane' && (
                  <div className="core-visual-transfer">
                    <span className="core-visual-transfer__from">상품 등록</span>
                    <span className="core-visual-transfer__line">·····✈·····</span>
                    <span className="core-visual-transfer__to">구매 완료</span>
                  </div>
                )}
                {value.icon === 'chart' && (
                  <div className="core-visual-chart">
                    <span /><span /><span /><span />
                  </div>
                )}
                {value.icon === 'access' && (
                  <div className="core-visual-access">
                    <span>판매 수익</span>
                    <i>↔</i>
                    <span>내 라이브러리</span>
                  </div>
                )}
                {value.icon === 'library' && (
                  <div className="core-visual-library">
                    <span>다운로드 가능</span>
                    <span>언제든 다시 이용</span>
                  </div>
                )}
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className="home-market-preview" aria-labelledby="home-market-preview-heading">
        <div className="home-section-heading">
          <div>
            <span className="section-eyebrow">Explore the market</span>
            <h2 id="home-market-preview-heading">지금, 누군가의 작업을 덜어주는 에셋</h2>
          </div>
          <Link to="/products" className="home-section-link">모든 상품 보기 <span aria-hidden="true">→</span></Link>
        </div>
        <div className="home-product-marquee">
          {isProductsLoading ? (
            <div className="home-product-preview-grid">
              {Array.from({ length: 4 }, (_, index) => <HomeProductSkeleton key={index} />)}
            </div>
          ) : products.length > 0 ? (
            <div className="home-product-marquee__track">
              {marqueeProducts.map((product, index) => (
                <HomeProductCard key={`${product.id}-${index}`} product={product} index={index} />
              ))}
            </div>
          ) : (
            <p className="home-product-empty">새로운 디지털 에셋을 준비하고 있습니다.</p>
          )}
        </div>
      </section>

      {/* 2. Pricing Section (투명한 10% 수수료) */}
      <section className="intro-pricing" id="pricing" aria-labelledby="pricing-heading">
        <div className="pricing-neomorphic-board">
          <div className="pricing-copy">
            <span className="pricing-eyebrow">Transparent Pricing</span>
            <h2 id="pricing-heading" className="pricing-title">
              수수료 단 10%
            </h2>
            <p className="pricing-subtitle">
              기존 플랫폼의 30~50% 폭리 수수료에<br />창작 의지가 꺾이셨나요?
            </p>
            <p className="pricing-notice">
              에셋토리는 복잡한 중간 마진을 완전히 제거하고,<br />정산금의 90%를 창작자에게 고스란히 이식해 드립니다.
            </p>
            <div className="mt-8">
              <Link to="/products" className="pricing-cta-btn">
                창작물 등록하고 판매하기 &rarr;
              </Link>
            </div>
          </div>
          <div className="pricing-visual">
            <div className="pricing-comparison">
              <div className="comparison-slot shadow-inner bg-[#e6e9ee]">
                <span className="label">타사 평균 수수료</span>
                <strong className="value text-red-400">30% ~ 50%</strong>
              </div>
              <div className="comparison-slot highlighted-slot">
                <span className="label">에셋토리 수수료</span>
                <strong className="value text-brand">단 10%</strong>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="home-role-cta" aria-labelledby="home-role-cta-heading">
        <span className="section-eyebrow">Choose your next step</span>
        <h2 id="home-role-cta-heading">무엇을 시작하시겠어요?</h2>
        <p>창작물을 판매하거나, 지금 필요한 에셋을 찾아보세요.</p>
        <div className="home-role-cta__actions">
          <Link className="home-role-cta__seller" to="/login">에셋을 판매하고 싶어요 <span aria-hidden="true">→</span></Link>
          <Link className="home-role-cta__buyer" to="/products">필요한 에셋을 찾고 있어요 <span aria-hidden="true">→</span></Link>
        </div>
      </section>

      {/* 4. Foot note */}
      <footer className="intro-footer">
        <p className="footer-logo">Assetory</p>
        <p className="footer-copyright">
          © 2026 Assetory Market Platform. All rights reserved. (수수료 10% 정산 정책 및 부가세 별도)
        </p>
      </footer>

    </div>
  )
}
