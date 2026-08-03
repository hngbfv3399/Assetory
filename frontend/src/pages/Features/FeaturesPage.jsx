import { Link } from 'react-router-dom'

const featureDetailsList = [
  {
    icon: '🎨',
    title: '디자인 에셋 (Design Assets)',
    desc: '브랜드 가이드라인부터 로고 에셋, UI 와이어프레임 템플릿까지 완벽히 제공하여 전문 디자이너 고용 없이도 창작품의 첫인상을 극대화시킵니다.',
    gain: '수백만 원 고용 리스크 제거',
    color: '#E9A9A2'
  },
  {
    icon: '💻',
    title: '개발 컴포넌트 (Code Blocks)',
    desc: '리액트, 뷰, 스프링 등 다양한 언어와 라이브러리 스펙의 코드 조각(코드 블록)을 1KB 단위로 슬림하게 떼어내어 즉시 적용 가능한 모듈로 이식합니다.',
    gain: '일주일 단위의 중복 개발 병목 탈출',
    color: '#A8C7B7'
  },
  {
    icon: '📐',
    title: '시스템 구조도 (System Architecture)',
    desc: '데이터베이스 엔티티 설계도(ERD), 아키텍처 스키마 및 마이그레이션 스크립트를 도면화하여 첫 서버 구축 시 붕괴될지 모를 설계 리스크를 원천 차단합니다.',
    gain: 'DB 및 아키텍처 재설계 두려움 완화',
    color: '#9EB9DA'
  },
  {
    icon: '🌐',
    title: '결합 템플릿 (Integrated Boilerplates)',
    desc: '디자인과 개발 코드, 서버 구조가 완벽하게 한 세트로 결합된 1인 기업 런칭용 보일러플레이트 빌딩 블록을 직접 레고 조립하듯이 탑재합니다.',
    gain: '비개발 기획자의 기술 장벽 완전 소멸',
    color: '#E5C679'
  },
  {
    icon: '⚙️',
    title: '가치 마켓 플레이스 (Value Marketplace)',
    desc: '개인 하드디스크에 잠들어 있던 아주 사소한 에셋 조각 하나도, 가치 마켓 플레이스에 올리는 즉시 상용급 패키지로 탈바꿈하여 즉시 판매 수익을 창출합니다.',
    gain: '소박한 창작물 무가치화 제거 및 마네타이징',
    color: '#B7A8D7'
  }
]

export function FeaturesPage() {
  return (
    <main className="features-page-container">
      <div className="features-hero">
        <span className="features-badge">Key Features</span>
        <h1 className="features-main-title">에셋토리가 지탱하는 창작 가치</h1>
        <p className="features-lead-text">
          단순한 에셋 판매를 넘어, 아이디어를 상용 서비스로 런칭할 수 있는<br />
          5대 기술 에코시스템의 구체적인 특징을 소개합니다.
        </p>
      </div>

      <div className="features-showcase-section">
        {featureDetailsList.map((item, index) => (
          <div key={index} className="showcase-neomorphic-item" style={{ borderLeft: `5px solid ${item.color}` }}>
            <div className="showcase-icon-slot" style={{ backgroundColor: `${item.color}15` }}>
              <span className="icon-text">{item.icon}</span>
            </div>

            <div className="showcase-info-content">
              <h2 className="showcase-title">{item.title}</h2>
              <p className="showcase-desc">{item.desc}</p>

              <div className="showcase-metric-plate">
                <span className="metric-label text-brand font-black">해결 리스크 감소량 :</span>
                <strong className="metric-value text-neutral-800 font-extrabold ml-1.5">{item.gain}</strong>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="features-cta-box text-center mt-12">
        <Link to="/products" className="features-page-cta-btn">
          상품 탐색 시작하기 &rarr;
        </Link>
      </div>
    </main>
  )
}
