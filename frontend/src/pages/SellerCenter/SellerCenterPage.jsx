import { useState } from 'react'
import { Link, NavLink, useLocation } from 'react-router-dom'

import { Button } from '../../common/ui/button.jsx'
import { useCategoriesQuery } from '../Explore/hooks/useExploreCatalog.js'
import { useAuthStore } from '../../common/store/useAuthStore.js'
import { useSessionBootstrap } from '../Login/useAuthSession.js'
import { LiveSellerProductDetail, LiveSellerProductForm, LiveSellerProducts } from './SellerProductLivePages.jsx'

const DEMO_PRODUCTS = [
  { id: 'brand-kit', name: '모노톤 브랜드 UI 키트', category: '디자인 · UI/UX', price: '29,000', status: 'ON_SALE', role: 'OWNER', readiness: '판매 중', changes: 2, summary: '바로 적용해 브랜드 화면을 빠르게 완성하는 UI 키트', description: '## 한 번에 정리되는 브랜드 화면\n\n일관된 인터페이스를 만들기 위한 **실무용 UI 키트**입니다.\n\n### 포함 항목\n\n- Figma 컴포넌트 120개\n- 데스크톱·모바일 화면 예시\n- 색상과 타이포그래피 토큰\n\n---\n\n상업 프로젝트에 사용할 수 있으며, 구매 후 라이브러리에서 언제든 다시 확인할 수 있습니다.', license: '개인·상업 프로젝트 1인 사용' },
  { id: 'saas-template', name: 'React SaaS 대시보드 템플릿', category: '개발 · 웹·앱 템플릿', price: '42,000', status: 'DRAFT', role: 'OWNER', readiness: '대표 이미지와 구매 자료 필요', changes: 0, summary: '구독형 서비스의 관리자 화면을 빠르게 시작하는 템플릿', description: '## 제품에 맞게 빠르게 확장하세요\n\nReact 기반의 관리자 화면 구조와 화면 예시를 제공합니다.\n\n### 포함 예정\n\n- 대시보드 레이아웃\n- 테이블과 필터 패턴\n- 설정 화면 템플릿', license: '개인·상업 프로젝트 1인 사용' },
  { id: 'creator-guide', name: '1인 창작자 판매 운영 가이드', category: '전자책 · 판매·운영 가이드', price: '18,000', status: 'ON_SALE', role: 'MANAGER', readiness: '변경 요청 1건 대기', changes: 1, summary: '디지털 상품을 기획하고 운영하는 실전 워크북', description: '## 혼자서도 지속 가능한 판매 운영\n\n상품 기획부터 구매자 응대까지 실제 운영의 기준을 정리했습니다.\n\n- 판매 준비 체크리스트\n- 문의와 환불 응대 예시\n- 판매 지표 읽는 법', license: '개인 학습용' },
]

const NEW_PRODUCT_TEMPLATE = { ...DEMO_PRODUCTS[0], id: 'new-product', name: '새 상품', price: '0', status: 'DRAFT', summary: '구매자가 한눈에 이해할 수 있는 소개 문구', description: '# 새 디지털 상품\n\n구매자가 상품을 이해할 수 있도록 소개를 작성하세요.', license: '개인·상업 프로젝트 1인 사용' }
const SALE_OPTIONS = [
  { value: 'ONE_TIME', label: '일회 구매', description: '한 번 결제하고 자료를 계속 이용해요.' },
  { value: 'PAY_WHAT_YOU_WANT', label: '가격 자율 설정', description: '최소 금액 이상에서 구매자가 가격을 정해요.' },
  { value: 'PREORDER', label: '선주문', description: '출시일에 구매자 라이브러리에 자료가 제공돼요.' },
  { value: 'MEMBERSHIP', label: '멤버십', description: '정기 결제로 새 자료와 업데이트를 제공해요.' },
]
const CATEGORY_FALLBACK = [
  ['디자인', ['UI/UX', '게임 에셋', '아트·일러스트', '브랜드·그래픽', '폰트·타이포그래피']],
  ['개발', ['웹·앱 템플릿', '코드·라이브러리', '플러그인·확장', '프로그램·도구']],
  ['전자책', ['판매·운영 가이드', '영상 강의', '비즈니스 자료']],
  ['3D', ['3D 모델', '텍스처·머티리얼', '3D 프린팅']],
  ['음악·사운드', ['음악 샘플·루프', '사운드 이펙트', '음원 제작 도구']],
  ['영상·영화', ['영상 템플릿', '모션 그래픽', '영상 편집 도구']],
  ['게임', ['게임 개발 도구', '게임 UI·아이콘', '맵·레벨 에셋']],
  ['교육·자기계발', ['전문 강의', '학습 워크북', '자기계발 자료']],
].map(([name, children], rootIndex) => ({ id: `root-${rootIndex}`, name, children: children.map((child, childIndex) => ({ id: `child-${rootIndex}-${childIndex}`, name: child })) }))

const roleLabel = { OWNER: '소유자', MANAGER: '매니저', EDITOR: '편집자', VIEWER: '뷰어' }
const statusLabel = { DRAFT: '임시저장', ON_SALE: '판매 중', STOPPED: '판매 중지' }

export function SellerCenterPage() {
  const { pathname } = useLocation()
  const productId = pathname.split('/')[3]
  const isStorefrontPreview = pathname.endsWith('/preview')
  const { data: categories = [] } = useCategoriesQuery()
  const accessToken = useAuthStore((state) => state.accessToken)
  const { isLoading: isSessionLoading } = useSessionBootstrap()

  if (isSessionLoading) return <main className="seller-studio"><p className="seller-auth-loading">로그인 상태를 확인하고 있습니다.</p></main>
  if (!accessToken) return <SellerAccessDenied returnPath={pathname} />

  if (isStorefrontPreview) {
    const product = DEMO_PRODUCTS.find((item) => item.id === productId) ?? NEW_PRODUCT_TEMPLATE
    return <SellerStorefrontPreviewPage product={product} />
  }

  const content = pathname.endsWith('/products/new')
    ? <LiveSellerProductForm categories={categories} />
    : productId
      ? <LiveSellerProductDetail productId={productId} />
      : pathname.endsWith('/products')
        ? <LiveSellerProducts />
        : pathname.endsWith('/sales')
          ? <SellerSales />
          : pathname.endsWith('/settlements')
            ? <SellerSettlements />
            : <SellerHome />

  return (
    <main className="seller-studio">
      <div className="seller-studio__shell">
        <SellerSidebar />
        <section className="seller-studio__workspace">{content}</section>
      </div>
    </main>
  )
}

function SellerAccessDenied({ returnPath }) {
  return <main className="seller-access-denied"><section><span>SELLER ACCESS</span><h1>판매자 권한이 필요한 페이지입니다.</h1><p>상품 등록, 구매 자료 관리, 판매 운영은 로그인한 회원만 이용할 수 있습니다.</p><div><Link className="seller-studio__primary-button" to="/login" state={{ from: returnPath }}>로그인하고 계속하기</Link><Link to="/products">상품 탐색으로 돌아가기</Link></div></section></main>
}

function SellerSidebar() {
  return (
    <aside className="seller-studio__sidebar" aria-label="판매자 스튜디오 메뉴">
      <Link className="seller-studio__brand" to="/seller">Assetory</Link>
      <span className="seller-studio__label">SELLER STUDIO</span>
      <nav className="seller-studio__primary-nav">
        <StudioLink to="/seller" end label="Home" />
        <StudioLink to="/seller/products" label="Products" />
        <StudioLink to="/seller/sales" label="Sales" />
        <StudioLink to="/seller/settlements" label="예상 정산" />
        <Link to="/products" className="seller-studio__discover">Discover <span>↗</span></Link>
      </nav>
      <div className="seller-studio__utility-nav">
        <span>MORE</span>
        <button type="button">판매자 프로필</button>
        <button type="button">도움말</button>
      </div>
      <button type="button" className="seller-studio__profile"><i>취</i><span>취미로개발</span><b>⌄</b></button>
    </aside>
  )
}

function StudioLink({ to, end, label }) {
  return <NavLink end={end} to={to} className={({ isActive }) => isActive ? 'seller-studio__nav-link is-active' : 'seller-studio__nav-link'}>{label}<span>→</span></NavLink>
}

function SellerHome() {
  return (
    <>
      <PageHeader eyebrow="HOME" title="판매를 시작할 준비를 해볼까요?" action={<Link className="seller-studio__primary-button" to="/seller/products/new">+ 새 상품 등록</Link>} />
      <section className="seller-home__checklist">
        <div className="seller-home__section-heading"><span>GETTING STARTED</span><h2>판매 시작 체크리스트</h2><p>상품을 공개하고 첫 판매를 만들기까지 필요한 단계입니다.</p></div>
        <div className="seller-home__steps">
          <HomeStep number="01" title="상품 초안 만들기" description="카테고리, 이름, 가격을 입력하고 임시저장합니다." to="/seller/products/new" />
          <HomeStep number="02" title="대표 이미지 추가" description="상품을 설명하는 대표 이미지를 준비합니다." to="/seller/products/saas-template?tab=assets" muted />
          <HomeStep number="03" title="구매 자료 등록" description="구매자가 이용할 디지털 자료를 추가합니다." to="/seller/products/saas-template?tab=assets" muted />
          <HomeStep number="04" title="판매 시작" description="준비 조건을 모두 채운 뒤 공개합니다." to="/seller/products/saas-template" muted />
        </div>
      </section>
      <section className="seller-home__priority-grid">
        <article className="seller-home__approval-card"><span>NEEDS YOUR REVIEW</span><strong>2</strong><h2>승인 대기 변경 요청</h2><p>공동 작업자가 보낸 이미지·상품 정보 변경 요청을 확인하세요.</p><Link to="/seller/products/brand-kit?tab=changes">변경 요청 검토하기 →</Link></article>
        <article className="seller-home__activity-card"><span>ACTIVITY</span><h2>최근 활동</h2><ul><li><b>편집자 민서</b>가 브랜드 UI 키트의 이미지 추가를 요청했습니다.<time>12분 전</time></li><li><b>React SaaS 템플릿</b>은 판매 시작 조건 2개가 남았습니다.<time>어제</time></li></ul></article>
      </section>
      <section className="seller-home__metrics" aria-label="판매 현황">
        <Metric label="이번 달 매출" value="₩ 0" /><Metric label="결제 완료 주문" value="0건" /><Metric label="판매 중 상품" value="1개" /><Metric label="예상 정산" value="₩ 0" />
      </section>
    </>
  )
}

function HomeStep({ number, title, description, to, muted }) {
  return <Link to={to} className={muted ? 'seller-home__step is-pending' : 'seller-home__step'}><span>{number}</span><div><strong>{title}</strong><p>{description}</p></div><b>→</b></Link>
}

export function SellerProducts({ scope }) {
  const products = scope === 'owned' ? DEMO_PRODUCTS.filter((product) => product.role === 'OWNER') : scope === 'collab' ? DEMO_PRODUCTS.filter((product) => product.role !== 'OWNER') : DEMO_PRODUCTS
  return (
    <>
      <PageHeader eyebrow="PRODUCTS" title="상품 관리" action={<Link className="seller-studio__primary-button" to="/seller/products/new">+ 새 상품 등록</Link>} />
      <nav className="seller-products__scope-tabs" aria-label="상품 범위">
        <ScopeLink scope="all" active={scope} label="전체 상품" /><ScopeLink scope="owned" active={scope} label="내 소유 상품" /><ScopeLink scope="collab" active={scope} label="공동 작업 상품" />
      </nav>
      <section className="seller-products__intro"><div><span>PRODUCT WORKSPACE</span><h2>상품 하나가 곧 하나의 작업 공간입니다.</h2><p>상품 정보, 자료, 공동 작업, 변경 요청, 판매 운영을 한 흐름으로 관리합니다.</p></div><Link to="/seller/products/new">새 상품 등록 →</Link></section>
      <div className="seller-products__filters"><button className="is-active">전체</button><button>임시저장</button><button>판매 중</button><button>판매 중지</button></div>
      <p className="seller-products__count">총 {products.length}개 상품</p>
      <div className="seller-products__grid">{products.map((product) => <ProductCard key={product.id} product={product} />)}</div>
    </>
  )
}

function ScopeLink({ scope, active, label }) { return <Link to={`/seller/products?scope=${scope}`} className={scope === active ? 'is-active' : ''}>{label}</Link> }

function ProductCard({ product }) {
  return <Link to={`/seller/products/${product.id}`} className="seller-product-card"><div className={`seller-product-card__preview seller-product-card__preview--${product.id}`}><span>{product.category.split(' · ')[0]}</span></div><div className="seller-product-card__content"><div><span>{product.category}</span><h3>{product.name}</h3></div><strong>₩ {product.price}</strong><div className="seller-product-card__meta"><i className={`seller-status seller-status--${product.status.toLowerCase()}`}>{statusLabel[product.status]}</i><i className={`seller-role seller-role--${product.role.toLowerCase()}`}>{roleLabel[product.role]}</i></div><p>{product.readiness}{product.changes ? ` · 승인 대기 ${product.changes}건` : ''}</p></div></Link>
}

export function SellerProductDetail({ productId, activeTab, categories }) {
  const product = DEMO_PRODUCTS.find((item) => item.id === productId) ?? (productId === 'new-product' ? NEW_PRODUCT_TEMPLATE : DEMO_PRODUCTS[0])
  const isOwner = product.role === 'OWNER'
  return (
    <>
      <Link to="/seller/products" className="seller-detail__back">← 상품 목록</Link>
      <PageHeader eyebrow={`${roleLabel[product.role]} · ${statusLabel[product.status]}`} title={product.name} action={isOwner ? <Button className="seller-studio__primary-button">판매 중지</Button> : <Button className="seller-studio__primary-button">변경 요청 제출</Button>} />
      <nav className="seller-detail__tabs" aria-label="상품 관리 메뉴">
        <DetailTab product={product} tab="overview" active={activeTab} label="개요" /><DetailTab product={product} tab="edit" active={activeTab} label="상품 정보" /><DetailTab product={product} tab="assets" active={activeTab} label="구매 자료" /><DetailTab product={product} tab="receipt" active={activeTab} label="구매 후 안내" /><DetailTab product={product} tab="team" active={activeTab} label="공동 작업" /><DetailTab product={product} tab="changes" active={activeTab} label="변경 요청" badge={2} /><DetailTab product={product} tab="operations" active={activeTab} label="판매 운영" /><DetailTab product={product} tab="storefront" active={activeTab} label="구매자 미리보기" />
      </nav>
      {activeTab === 'overview' ? <ProductOverview product={product} isOwner={isOwner} /> : null}
      {activeTab === 'edit' ? <ProductEdit product={product} isOwner={isOwner} categories={categories} /> : null}
      {activeTab === 'assets' ? <ProductAssets isOwner={isOwner} /> : null}
      {activeTab === 'receipt' ? <ProductReceipt isOwner={isOwner} /> : null}
      {activeTab === 'team' ? <ProductTeam isOwner={isOwner} /> : null}
      {activeTab === 'changes' ? <ProductChanges isOwner={isOwner} /> : null}
      {activeTab === 'operations' ? <ProductOperations product={product} /> : null}
      {activeTab === 'storefront' ? <StorefrontPreview product={product} /> : null}
    </>
  )
}

function DetailTab({ product, tab, active, label, badge }) { return <Link to={`/seller/products/${product.id}?tab=${tab}`} className={tab === active ? 'is-active' : ''}>{label}{badge ? <b>{badge}</b> : null}</Link> }

function ProductOverview({ product, isOwner }) {
  return <section className="seller-detail__overview"><article><span>SELLING STATUS</span><h2>{statusLabel[product.status]}</h2><p>{product.status === 'DRAFT' ? '대표 이미지와 활성 구매 자료를 추가하면 판매를 시작할 수 있습니다.' : '구매자에게 공개되어 있으며 판매 중입니다.'}</p><div className="seller-detail__progress"><span className="is-complete">기본 정보</span><span className={product.status === 'DRAFT' ? '' : 'is-complete'}>대표 이미지</span><span className={product.status === 'DRAFT' ? '' : 'is-complete'}>구매 자료</span><span className={product.status === 'ON_SALE' ? 'is-complete' : ''}>판매 시작</span></div></article><article><span>YOUR ROLE</span><h2>{roleLabel[product.role]}</h2><p>{isOwner ? '공동 작업자를 관리하고 모든 변경 요청을 최종 승인할 수 있습니다.' : '직접 저장 대신 변경 요청을 제출하며, 승인된 요청만 상품에 반영됩니다.'}</p><Link to={`/seller/products/${product.id}?tab=${isOwner ? 'changes' : 'team'}`}>{isOwner ? '승인 대기 요청 보기 →' : '내 권한 보기 →'}</Link></article></section>
}

function ProductEdit({ product, isOwner, categories }) { return <SellerProductEditor product={product} isOwner={isOwner} categories={categories} /> }

function ProductAssets({ isOwner }) { return <section className="seller-assets"><div><span>PREVIEW IMAGES</span><h2>대표 이미지</h2><p>구매 전 상품을 이해할 수 있는 이미지를 관리합니다.</p><button type="button" className="seller-assets__placeholder">+ 이미지 추가</button></div><div><span>PURCHASE RESOURCES</span><h2>구매 자료</h2><p>결제 완료 구매자만 접근할 디지털 자료입니다.</p><button type="button" className="seller-assets__placeholder">+ 구매 자료 추가</button></div><aside>{isOwner ? '소유자는 자료를 바로 관리합니다.' : '공동 작업자의 추가·수정·삭제는 변경 요청으로 저장됩니다.'}</aside></section> }
function ProductReceipt({ isOwner }) { return <section className="seller-receipt"><div><span>POST-PURCHASE</span><h2>구매 후 안내</h2><p>구매 완료 화면과 내 라이브러리에서 구매자에게 보여 줄 안내를 작성합니다.</p><label>라이브러리 버튼 문구<input defaultValue="구매 자료 보기" maxLength="26" /></label><label>구매 완료 안내<textarea rows="6" defaultValue="구매해 주셔서 감사합니다. 내 라이브러리에서 자료를 이용할 수 있습니다." /></label><Button className="seller-studio__primary-button">{isOwner ? '안내 저장' : '변경 요청 제출'}</Button></div><aside><span>RECEIPT PREVIEW</span><h3>구매가 완료되었습니다</h3><p>상품명과 결제 정보, 구매 후 안내가 이곳에 표시됩니다.</p><button type="button">구매 자료 보기</button></aside></section> }

function ProductTeam({ isOwner }) { return <section className="seller-team"><header><div><span>COLLABORATORS</span><h2>공동 작업자</h2><p>역할은 상품별로 적용되며 소유자는 항상 최종 승인 권한을 가집니다.</p></div>{isOwner ? <Button className="seller-studio__primary-button">+ 공동 작업자 초대</Button> : null}</header><div className="seller-team__members"><TeamRow name="취미로개발" role="OWNER" status="소유자" /><TeamRow name="민서" role="EDITOR" status="수락됨" /><TeamRow name="준" role="MANAGER" status="수락됨" /><TeamRow name="수아" role="VIEWER" status="초대 대기" /></div><div className="seller-team__roles"><RoleCard role="MANAGER" description="콘텐츠·판매 상태 요청과 상품 범위 운영·통계 확인" /><RoleCard role="EDITOR" description="상품 정보, 이미지, 구매 자료 변경 요청" /><RoleCard role="VIEWER" description="해당 상품의 판매 통계 열람" /></div></section> }

function TeamRow({ name, role, status }) { return <article><i>{name.slice(0, 1)}</i><strong>{name}</strong><span className={`seller-role seller-role--${role.toLowerCase()}`}>{roleLabel[role]}</span><small>{status}</small><button type="button">···</button></article> }
function RoleCard({ role, description }) { return <article><span>{roleLabel[role]}</span><p>{description}</p></article> }

function ProductChanges({ isOwner }) { return <section className="seller-changes"><header><span>CHANGE REQUESTS</span><h2>{isOwner ? '승인 대기 변경 요청' : '내 변경 요청'}</h2><p>변경 내용은 소유자의 승인 후 실제 상품에 반영됩니다.</p></header><ChangeRow type="이미지 추가" requester="민서" time="12분 전" status="PENDING" owner={isOwner} /><ChangeRow type="판매 시작" requester="준" time="어제" status="PENDING" owner={isOwner} /><ChangeRow type="구매 자료 수정" requester="민서" time="7월 30일" status="APPROVED" owner={isOwner} /></section> }
function ChangeRow({ type, requester, time, status, owner }) { return <article className="seller-change-row"><div><span>{type}</span><h3>{requester}님의 변경 요청</h3><p>요청 시각: {time}</p></div><i className={`seller-change-status seller-change-status--${status.toLowerCase()}`}>{status === 'PENDING' ? '승인 대기' : '승인됨'}</i>{owner && status === 'PENDING' ? <div><Button size="sm">승인</Button><Button variant="outline" size="sm">반려</Button></div> : null}</article> }

function ProductOperations({ product }) { return <section className="seller-operations"><article><span>SALES</span><h2>주문·문의·환불</h2><p>이 상품의 주문과 구매자 운영 이력을 확인합니다.</p><Link to="/seller/sales">판매 운영으로 이동 →</Link></article><article><span>ANALYTICS</span><h2>상품별 판매 통계</h2><p>판매 수량, 환불 차감, 기간별 매출을 상품 범위로 확인합니다.</p><Link to="/seller/settlements">예상 정산 보기 →</Link></article><aside>{product.role === 'VIEWER' ? '뷰어는 상품별 통계만 열람할 수 있습니다.' : '매니저는 자신이 참여한 상품 범위에서 주문·문의·환불을 운영할 수 있습니다.'}</aside></section> }

function SellerSales() { return <><PageHeader eyebrow="SALES" title="판매 운영" action={<Button variant="outline">내보내기</Button>} /><div className="seller-toolbar"><button>기간: 최근 30일</button><button>전체 상품</button><button>전체 상태</button><button>검색</button></div><section className="seller-sales__summary"><Metric label="결제 완료" value="0건" /><Metric label="환불 요청" value="0건" /><Metric label="답변 대기 문의" value="0건" /></section><section className="seller-sales__tabs"><button className="is-active">주문</button><button>문의</button><button>환불</button></section><div className="seller-empty-operation"><span>NO SALES YET</span><h2>판매가 시작되면 주문과 구매자 활동이 여기에 표시됩니다.</h2><Link to="/seller/products">상품 관리로 이동 →</Link></div></> }

function SellerSettlements() { return <><PageHeader eyebrow="SETTLEMENTS" title="예상 정산" action={<Button variant="outline">기간 선택</Button>} /><section className="seller-settlement__hero"><div><span>ESTIMATED</span><h2>₩ 0</h2><p>실제 송금이 아닌 예상 정산 계산 결과입니다.</p></div><ul><li><span>결제 완료 매출</span><b>₩ 0</b></li><li><span>환불 완료 차감</span><b>₩ 0</b></li><li><span>플랫폼 수수료 (10%)</span><b>₩ 0</b></li></ul></section><section className="seller-settlement__grid"><article><span>SALES TREND</span><h2>기간별 매출 추이</h2><div className="seller-chart-placeholder">판매 데이터가 쌓이면 추이가 표시됩니다.</div></article><article><span>TOP PRODUCTS</span><h2>상품별 판매</h2><div className="seller-chart-placeholder">판매 기록이 있는 상품을 순위로 보여 줍니다.</div></article></section></> }

export function SellerProductForm({ categories }) {
  const product = { ...NEW_PRODUCT_TEMPLATE, name: '', price: '', summary: '' }
  return <><Link to="/seller/products" className="seller-detail__back">← 상품 목록</Link><PageHeader eyebrow="NEW PRODUCT" title="새 상품 등록" action={<Button variant="outline">임시저장</Button>} /><section className="seller-new-product__steps"><span className="is-active">01 상품 정보</span><span>02 이미지·구매 자료</span><span>03 구매 후 안내</span><span>04 검토·판매 시작</span></section><SellerProductEditor product={product} isOwner isNew categories={categories} /></>
}

function SellerProductEditor({ product, isOwner, isNew = false, categories }) {
  const [name, setName] = useState(product.name)
  const [summary, setSummary] = useState(product.summary)
  const [price, setPrice] = useState(product.price.replaceAll(',', ''))
  const [description, setDescription] = useState(product.description)
  const availableCategories = categories.length ? categories : CATEGORY_FALLBACK
  const initialRoot = availableCategories.find((item) => item.name === product.category.split(' · ')[0]) ?? availableCategories[0]
  const initialChild = initialRoot.children?.find((item) => item.name === product.category.split(' · ')[1]) ?? initialRoot.children?.[0]
  const [rootId, setRootId] = useState(initialRoot.id)
  const [childId, setChildId] = useState(initialChild?.id ?? '')
  const activeRoot = availableCategories.find((item) => String(item.id) === String(rootId)) ?? availableCategories[0]
  const activeChild = activeRoot.children?.find((item) => String(item.id) === String(childId)) ?? activeRoot.children?.[0]
  const category = activeChild ? `${activeRoot.name} · ${activeChild.name}` : activeRoot.name
  const [saleMode, setSaleMode] = useState('ONE_TIME')
  const [releaseDate, setReleaseDate] = useState('2026-09-01')
  const selectedSaleOption = SALE_OPTIONS.find((option) => option.value === saleMode)
  const previewProduct = { ...product, name: name || '상품 이름', summary: summary || '구매자가 한눈에 이해할 수 있는 소개 문구', price: price ? Number(price).toLocaleString('ko-KR') : '0', description, category, saleMode, saleLabel: selectedSaleOption.label, releaseDate }
  const appendMarkdown = (snippet) => setDescription((value) => `${value}${value.endsWith('\n') ? '' : '\n'}${snippet}`)

  return <section className="seller-editor">
    <div className="seller-editor__form">
      <div className="seller-editor__section-heading"><span>{isNew ? 'PRODUCT TYPE' : 'PRODUCT INFORMATION'}</span><h2>{isNew ? '구매자에게 보여 줄 상품을 만드세요.' : '상품 정보와 구매자용 소개를 편집하세요.'}</h2><p>초안은 저장 전까지 공개되지 않습니다. Markdown 설명은 오른쪽의 공개 페이지 미리보기에 바로 반영됩니다.</p></div>
      <section className="seller-category-selector"><div><span>CATEGORY</span><h3>상품 카테고리</h3><p>Gumroad의 탐색 축처럼 상품 성격에 맞는 상위 분류와 세부 분류를 고르세요.</p></div><div className="seller-category-selector__roots">{availableCategories.map((item) => <button key={item.id} type="button" className={String(item.id) === String(activeRoot.id) ? 'is-active' : ''} onClick={() => { setRootId(item.id); setChildId(item.children?.[0]?.id ?? '') }}>{item.name}</button>)}</div><label>세부 분류<select value={activeChild?.id ?? ''} onChange={(event) => setChildId(event.target.value)}>{activeRoot.children?.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label></section>
      <div className="seller-editor__fields">
        <label>상품명<input value={name} onChange={(event) => setName(event.target.value)} placeholder="상품의 이름을 입력하세요" /></label>
        <label>한 줄 소개<input value={summary} onChange={(event) => setSummary(event.target.value)} placeholder="구매자가 한눈에 이해할 수 있는 설명" /></label>
        <label>판매 가격<input value={price} onChange={(event) => setPrice(event.target.value.replace(/[^0-9]/g, ''))} inputMode="numeric" placeholder="예: 29000" /></label>
        <label>이용 조건<select defaultValue={product.license}><option>개인·상업 프로젝트 1인 사용</option><option>개인 학습용</option><option>팀 라이선스 별도 문의</option></select></label>
      </div>
      <section className="seller-sale-model"><div><span>SELLING MODEL</span><h3>어떤 방식으로 판매할까요?</h3><p>지금은 화면 미리보기용 설정입니다. 실제 결제·접근 규칙은 판매자 API 연결 단계에서 적용됩니다.</p></div><div className="seller-sale-model__options">{SALE_OPTIONS.map((option) => <button type="button" key={option.value} className={saleMode === option.value ? 'is-active' : ''} onClick={() => setSaleMode(option.value)}><strong>{option.label}</strong><span>{option.description}</span></button>)}</div>{saleMode === 'PAY_WHAT_YOU_WANT' ? <label className="seller-sale-model__field">최소 결제 금액<input value={price} onChange={(event) => setPrice(event.target.value.replace(/[^0-9]/g, ''))} inputMode="numeric" placeholder="예: 5000" /></label> : null}{saleMode === 'PREORDER' ? <label className="seller-sale-model__field">출시 예정일<input value={releaseDate} onChange={(event) => setReleaseDate(event.target.value)} type="date" /></label> : null}{saleMode === 'MEMBERSHIP' ? <label className="seller-sale-model__field">월 구독료<input value={price} onChange={(event) => setPrice(event.target.value.replace(/[^0-9]/g, ''))} inputMode="numeric" placeholder="예: 9900" /></label> : null}</section>
      <div className="seller-markdown-editor"><div className="seller-markdown-editor__heading"><div><span>MARKDOWN DESCRIPTION</span><h3>상세 설명</h3></div><a href="https://www.markdownguide.org/basic-syntax/" target="_blank" rel="noreferrer">문법 도움말 ↗</a></div><div className="seller-markdown-editor__toolbar" aria-label="Markdown 빠른 입력"><button type="button" onClick={() => appendMarkdown('## 새 소제목')}>H2</button><button type="button" onClick={() => appendMarkdown('### 작은 소제목')}>H3</button><button type="button" onClick={() => appendMarkdown('**강조할 문장**')}>굵게</button><button type="button" onClick={() => appendMarkdown('- 목록 항목')}>목록</button><button type="button" onClick={() => appendMarkdown('[링크 이름](https://)')}>링크</button></div><textarea value={description} onChange={(event) => setDescription(event.target.value)} rows="15" spellCheck="false" aria-label="Markdown 상품 설명" /><p>제목, 목록, 굵은 글씨, 링크, 구분선을 사용할 수 있습니다.</p></div>
      <div className="seller-editor__actions"><Button variant="outline">임시저장</Button><Button className="seller-studio__primary-button">{isOwner ? (isNew ? '초안 저장 후 이미지·자료 등록' : '변경사항 저장') : '변경 요청 제출'}</Button></div>
    </div>
    <aside className="seller-editor__preview"><div className="seller-editor__preview-heading"><span>BUYER PREVIEW</span><strong>구매자에게 보이는 페이지</strong><Link to={`/seller/products/${product.id}/preview`}>미리보기 페이지 열기 ↗</Link></div><StorefrontPreview product={previewProduct} compact /></aside>
  </section>
}

function SellerStorefrontPreviewPage({ product }) {
  return <main className="seller-storefront-preview-page"><header><Link to={`/seller/products/${product.id}?tab=edit`}>← 편집으로 돌아가기</Link><span>PREVIEW MODE · 구매자에게 보이는 공개 상품 페이지입니다.</span></header><StorefrontPreview product={product} /></main>
}

function StorefrontPreview({ product, compact = false }) {
  return <section className={compact ? 'seller-storefront seller-storefront--compact' : 'seller-storefront'}>
    {!compact ? <header className="seller-storefront__bar"><span>ASSETORY</span><Link to="/products">상품 탐색</Link></header> : null}
    <div className="seller-storefront__hero"><div className={`seller-storefront__cover seller-storefront__cover--${product.id}`}><span>{product.category.split(' · ')[0]}</span><strong>{product.name || '상품 이름'}</strong><i>PREVIEW IMAGE</i></div><div className="seller-storefront__buy"><span>{product.category}</span><h2>{product.name || '상품 이름'}</h2><p>{product.summary}</p><i className="seller-storefront__sale-label">{product.saleLabel || '일회 구매'}</i><strong>₩ {product.price || '0'}{product.saleMode === 'MEMBERSHIP' ? ' / 월' : ''}</strong><button type="button">{product.saleMode === 'PREORDER' ? '선주문하기' : product.saleMode === 'MEMBERSHIP' ? '멤버십 시작하기' : 'Mock 결제로 구매하기'}</button><small>{product.saleMode === 'PREORDER' ? `${product.releaseDate || '출시 예정일'}에 자료가 제공됩니다.` : product.saleMode === 'PAY_WHAT_YOU_WANT' ? '최소 금액 이상에서 원하는 가격으로 구매할 수 있어요.' : product.saleMode === 'MEMBERSHIP' ? '구독 기간 동안 새 자료와 업데이트를 이용할 수 있어요.' : '결제 완료 후 내 라이브러리에서 이용할 수 있어요.'}</small></div></div>
    <div className="seller-storefront__body"><article><MarkdownPreview value={product.description} /></article><aside><span>INCLUDED</span><h3>상품 구성</h3><ul><li>바로 사용할 수 있는 디지털 자료</li><li>구매 후 라이브러리에서 재이용</li><li>{product.license || '이용 조건은 판매자 설명을 확인하세요.'}</li></ul><span>SELLER</span><p>취미로개발 · 디지털 상품 창작자</p></aside></div>
  </section>
}

function MarkdownPreview({ value }) {
  return <div className="seller-markdown-preview">{value.split('\n').map((line, index) => {
    if (!line.trim()) return null
    if (line === '---') return <hr key={`${line}-${index}`} />
    if (line.startsWith('### ')) return <h4 key={`${line}-${index}`}>{renderMarkdownInline(line.slice(4))}</h4>
    if (line.startsWith('## ')) return <h3 key={`${line}-${index}`}>{renderMarkdownInline(line.slice(3))}</h3>
    if (line.startsWith('# ')) return <h2 key={`${line}-${index}`}>{renderMarkdownInline(line.slice(2))}</h2>
    if (line.startsWith('- ')) return <li key={`${line}-${index}`}>{renderMarkdownInline(line.slice(2))}</li>
    return <p key={`${line}-${index}`}>{renderMarkdownInline(line)}</p>
  })}</div>
}

function renderMarkdownInline(value) {
  return value.split(/(\*\*[^*]+\*\*|\[[^\]]+\]\(https?:\/\/[^)\s]+\))/g).map((part, index) => {
    if (part.startsWith('**') && part.endsWith('**')) return <strong key={`${part}-${index}`}>{part.slice(2, -2)}</strong>
    const link = part.match(/^\[([^\]]+)\]\((https?:\/\/[^)\s]+)\)$/)
    return link ? <a key={`${part}-${index}`} href={link[2]} target="_blank" rel="noreferrer">{link[1]}</a> : part
  })
}

function PageHeader({ eyebrow, title, action }) { return <header className="seller-page-header"><div><span>{eyebrow}</span><h1>{title}</h1></div>{action}</header> }
function Metric({ label, value }) { return <article><span>{label}</span><strong>{value}</strong></article> }
