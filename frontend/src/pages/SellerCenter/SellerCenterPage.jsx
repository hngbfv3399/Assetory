import { Link, NavLink, useLocation, useSearchParams } from 'react-router-dom'

import { Button } from '../../common/ui/button.jsx'

const DEMO_PRODUCTS = [
  { id: 'brand-kit', name: '모노톤 브랜드 UI 키트', category: '디자인 · UI/UX', price: '29,000', status: 'ON_SALE', role: 'OWNER', readiness: '판매 중', changes: 2 },
  { id: 'saas-template', name: 'React SaaS 대시보드 템플릿', category: '개발 · 웹·앱 템플릿', price: '42,000', status: 'DRAFT', role: 'OWNER', readiness: '대표 이미지와 구매 자료 필요', changes: 0 },
  { id: 'creator-guide', name: '1인 창작자 판매 운영 가이드', category: '전자책 · 판매·운영 가이드', price: '18,000', status: 'ON_SALE', role: 'MANAGER', readiness: '변경 요청 1건 대기', changes: 1 },
]

const roleLabel = { OWNER: '소유자', MANAGER: '매니저', EDITOR: '편집자', VIEWER: '뷰어' }
const statusLabel = { DRAFT: '임시저장', ON_SALE: '판매 중', STOPPED: '판매 중지' }

export function SellerCenterPage() {
  const { pathname } = useLocation()
  const [searchParams] = useSearchParams()
  const detailTab = searchParams.get('tab') || 'overview'
  const productScope = searchParams.get('scope') || 'all'
  const productId = pathname.split('/')[3]

  const content = pathname.endsWith('/products/new')
    ? <SellerProductForm />
    : productId
      ? <SellerProductDetail productId={productId} activeTab={detailTab} />
      : pathname.endsWith('/products')
        ? <SellerProducts scope={productScope} />
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

function SellerProducts({ scope }) {
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

function SellerProductDetail({ productId, activeTab }) {
  const product = DEMO_PRODUCTS.find((item) => item.id === productId) ?? DEMO_PRODUCTS[0]
  const isOwner = product.role === 'OWNER'
  return (
    <>
      <Link to="/seller/products" className="seller-detail__back">← 상품 목록</Link>
      <PageHeader eyebrow={`${roleLabel[product.role]} · ${statusLabel[product.status]}`} title={product.name} action={isOwner ? <Button className="seller-studio__primary-button">판매 중지</Button> : <Button className="seller-studio__primary-button">변경 요청 제출</Button>} />
      <nav className="seller-detail__tabs" aria-label="상품 관리 메뉴">
        <DetailTab product={product} tab="overview" active={activeTab} label="개요" /><DetailTab product={product} tab="edit" active={activeTab} label="기본 정보" /><DetailTab product={product} tab="assets" active={activeTab} label="이미지·구매 자료" /><DetailTab product={product} tab="team" active={activeTab} label="공동 작업" /><DetailTab product={product} tab="changes" active={activeTab} label="변경 요청" badge={2} /><DetailTab product={product} tab="operations" active={activeTab} label="판매 운영" />
      </nav>
      {activeTab === 'overview' ? <ProductOverview product={product} isOwner={isOwner} /> : null}
      {activeTab === 'edit' ? <ProductEdit product={product} isOwner={isOwner} /> : null}
      {activeTab === 'assets' ? <ProductAssets isOwner={isOwner} /> : null}
      {activeTab === 'team' ? <ProductTeam isOwner={isOwner} /> : null}
      {activeTab === 'changes' ? <ProductChanges isOwner={isOwner} /> : null}
      {activeTab === 'operations' ? <ProductOperations product={product} /> : null}
    </>
  )
}

function DetailTab({ product, tab, active, label, badge }) { return <Link to={`/seller/products/${product.id}?tab=${tab}`} className={tab === active ? 'is-active' : ''}>{label}{badge ? <b>{badge}</b> : null}</Link> }

function ProductOverview({ product, isOwner }) {
  return <section className="seller-detail__overview"><article><span>SELLING STATUS</span><h2>{statusLabel[product.status]}</h2><p>{product.status === 'DRAFT' ? '대표 이미지와 활성 구매 자료를 추가하면 판매를 시작할 수 있습니다.' : '구매자에게 공개되어 있으며 판매 중입니다.'}</p><div className="seller-detail__progress"><span className="is-complete">기본 정보</span><span className={product.status === 'DRAFT' ? '' : 'is-complete'}>대표 이미지</span><span className={product.status === 'DRAFT' ? '' : 'is-complete'}>구매 자료</span><span className={product.status === 'ON_SALE' ? 'is-complete' : ''}>판매 시작</span></div></article><article><span>YOUR ROLE</span><h2>{roleLabel[product.role]}</h2><p>{isOwner ? '공동 작업자를 관리하고 모든 변경 요청을 최종 승인할 수 있습니다.' : '직접 저장 대신 변경 요청을 제출하며, 승인된 요청만 상품에 반영됩니다.'}</p><Link to={`/seller/products/${product.id}?tab=${isOwner ? 'changes' : 'team'}`}>{isOwner ? '승인 대기 요청 보기 →' : '내 권한 보기 →'}</Link></article></section>
}

function ProductEdit({ product, isOwner }) { return <section className="seller-detail__form-grid"><div><span>PRODUCT INFORMATION</span><label>상품명<input defaultValue={product.name} /></label><label>한 줄 소개<input defaultValue="바로 활용할 수 있는 실무용 디지털 자료입니다." /></label><label>판매 가격<input defaultValue={product.price.replace(',', '')} /></label><label>상세 설명<textarea rows="6" defaultValue="상품 구성과 활용 방법을 자세히 설명합니다." /></label></div><aside><span>{isOwner ? 'DIRECT EDIT' : 'CHANGE REQUEST'}</span><h2>{isOwner ? '변경 내용을 저장할 수 있어요.' : '변경 내용은 소유자 승인 후 반영돼요.'}</h2><p>{isOwner ? '저장한 내용은 즉시 상품에 반영됩니다.' : '요청 내용을 검토한 뒤 소유자가 승인 또는 반려합니다.'}</p><Button className="seller-studio__primary-button">{isOwner ? '변경사항 저장' : '변경 요청 제출'}</Button></aside></section> }

function ProductAssets({ isOwner }) { return <section className="seller-assets"><div><span>PREVIEW IMAGES</span><h2>대표 이미지</h2><p>구매 전 상품을 이해할 수 있는 이미지를 관리합니다.</p><button type="button" className="seller-assets__placeholder">+ 이미지 추가</button></div><div><span>PURCHASE RESOURCES</span><h2>구매 자료</h2><p>결제 완료 구매자만 접근할 디지털 자료입니다.</p><button type="button" className="seller-assets__placeholder">+ 구매 자료 추가</button></div><aside>{isOwner ? '소유자는 자료를 바로 관리합니다.' : '공동 작업자의 추가·수정·삭제는 변경 요청으로 저장됩니다.'}</aside></section> }

function ProductTeam({ isOwner }) { return <section className="seller-team"><header><div><span>COLLABORATORS</span><h2>공동 작업자</h2><p>역할은 상품별로 적용되며 소유자는 항상 최종 승인 권한을 가집니다.</p></div>{isOwner ? <Button className="seller-studio__primary-button">+ 공동 작업자 초대</Button> : null}</header><div className="seller-team__members"><TeamRow name="취미로개발" role="OWNER" status="소유자" /><TeamRow name="민서" role="EDITOR" status="수락됨" /><TeamRow name="준" role="MANAGER" status="수락됨" /><TeamRow name="수아" role="VIEWER" status="초대 대기" /></div><div className="seller-team__roles"><RoleCard role="MANAGER" description="콘텐츠·판매 상태 요청과 상품 범위 운영·통계 확인" /><RoleCard role="EDITOR" description="상품 정보, 이미지, 구매 자료 변경 요청" /><RoleCard role="VIEWER" description="해당 상품의 판매 통계 열람" /></div></section> }

function TeamRow({ name, role, status }) { return <article><i>{name.slice(0, 1)}</i><strong>{name}</strong><span className={`seller-role seller-role--${role.toLowerCase()}`}>{roleLabel[role]}</span><small>{status}</small><button type="button">···</button></article> }
function RoleCard({ role, description }) { return <article><span>{roleLabel[role]}</span><p>{description}</p></article> }

function ProductChanges({ isOwner }) { return <section className="seller-changes"><header><span>CHANGE REQUESTS</span><h2>{isOwner ? '승인 대기 변경 요청' : '내 변경 요청'}</h2><p>변경 내용은 소유자의 승인 후 실제 상품에 반영됩니다.</p></header><ChangeRow type="이미지 추가" requester="민서" time="12분 전" status="PENDING" owner={isOwner} /><ChangeRow type="판매 시작" requester="준" time="어제" status="PENDING" owner={isOwner} /><ChangeRow type="구매 자료 수정" requester="민서" time="7월 30일" status="APPROVED" owner={isOwner} /></section> }
function ChangeRow({ type, requester, time, status, owner }) { return <article className="seller-change-row"><div><span>{type}</span><h3>{requester}님의 변경 요청</h3><p>요청 시각: {time}</p></div><i className={`seller-change-status seller-change-status--${status.toLowerCase()}`}>{status === 'PENDING' ? '승인 대기' : '승인됨'}</i>{owner && status === 'PENDING' ? <div><Button size="sm">승인</Button><Button variant="outline" size="sm">반려</Button></div> : null}</article> }

function ProductOperations({ product }) { return <section className="seller-operations"><article><span>SALES</span><h2>주문·문의·환불</h2><p>이 상품의 주문과 구매자 운영 이력을 확인합니다.</p><Link to="/seller/sales">판매 운영으로 이동 →</Link></article><article><span>ANALYTICS</span><h2>상품별 판매 통계</h2><p>판매 수량, 환불 차감, 기간별 매출을 상품 범위로 확인합니다.</p><Link to="/seller/settlements">예상 정산 보기 →</Link></article><aside>{product.role === 'VIEWER' ? '뷰어는 상품별 통계만 열람할 수 있습니다.' : '매니저는 자신이 참여한 상품 범위에서 주문·문의·환불을 운영할 수 있습니다.'}</aside></section> }

function SellerSales() { return <><PageHeader eyebrow="SALES" title="판매 운영" action={<Button variant="outline">내보내기</Button>} /><div className="seller-toolbar"><button>기간: 최근 30일</button><button>전체 상품</button><button>전체 상태</button><button>검색</button></div><section className="seller-sales__summary"><Metric label="결제 완료" value="0건" /><Metric label="환불 요청" value="0건" /><Metric label="답변 대기 문의" value="0건" /></section><section className="seller-sales__tabs"><button className="is-active">주문</button><button>문의</button><button>환불</button></section><div className="seller-empty-operation"><span>NO SALES YET</span><h2>판매가 시작되면 주문과 구매자 활동이 여기에 표시됩니다.</h2><Link to="/seller/products">상품 관리로 이동 →</Link></div></> }

function SellerSettlements() { return <><PageHeader eyebrow="SETTLEMENTS" title="예상 정산" action={<Button variant="outline">기간 선택</Button>} /><section className="seller-settlement__hero"><div><span>ESTIMATED</span><h2>₩ 0</h2><p>실제 송금이 아닌 예상 정산 계산 결과입니다.</p></div><ul><li><span>결제 완료 매출</span><b>₩ 0</b></li><li><span>환불 완료 차감</span><b>₩ 0</b></li><li><span>플랫폼 수수료 (10%)</span><b>₩ 0</b></li></ul></section><section className="seller-settlement__grid"><article><span>SALES TREND</span><h2>기간별 매출 추이</h2><div className="seller-chart-placeholder">판매 데이터가 쌓이면 추이가 표시됩니다.</div></article><article><span>TOP PRODUCTS</span><h2>상품별 판매</h2><div className="seller-chart-placeholder">판매 기록이 있는 상품을 순위로 보여 줍니다.</div></article></section></> }

function SellerProductForm() { return <><Link to="/seller/products" className="seller-detail__back">← 상품 목록</Link><PageHeader eyebrow="NEW PRODUCT" title="새 상품 등록" action={<Button variant="outline">임시저장</Button>} /><section className="seller-new-product__steps"><span className="is-active">01 기본 정보</span><span>02 이미지·자료</span><span>03 검토·판매 시작</span></section><section className="seller-detail__form-grid seller-detail__form-grid--new"><div><span>PRODUCT TYPE</span><h2>어떤 디지털 상품을 만들고 있나요?</h2><div className="seller-new-product__types"><button className="is-active">디자인 에셋</button><button>개발 리소스</button><button>전자책·강의</button></div><label>세부 카테고리<select defaultValue=""><option value="" disabled>세부 카테고리를 선택하세요</option><option>UI/UX</option><option>게임 에셋</option><option>웹·앱 템플릿</option></select></label><label>상품명<input placeholder="상품의 이름을 입력하세요" /></label><label>한 줄 소개<input placeholder="구매자가 한눈에 이해할 수 있는 설명" /></label><label>판매 가격<input inputMode="numeric" placeholder="예: 29000" /></label></div><aside><span>DRAFT FIRST</span><h2>먼저 초안으로 저장합니다.</h2><p>기본 정보를 저장한 뒤 대표 이미지와 구매 자료를 추가할 수 있습니다.</p><Button className="seller-studio__primary-button">다음: 이미지·자료</Button></aside></section></> }

function PageHeader({ eyebrow, title, action }) { return <header className="seller-page-header"><div><span>{eyebrow}</span><h1>{title}</h1></div>{action}</header> }
function Metric({ label, value }) { return <article><span>{label}</span><strong>{value}</strong></article> }
