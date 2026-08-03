import { Link } from 'react-router-dom'

export function PricingPage() {
  return (
    <main className="pricing-page-container">
      <div className="pricing-hero">
        <span className="pricing-badge">Pricing Policy</span>
        <h1 className="pricing-main-title">정직하고 투명한 수수료</h1>
        <p className="pricing-lead-text">
          에셋토리는 창작자의 피와 땀으로 만들어진 가치를 헐값으로 갈취하지 않습니다.<br />
          오직 서비스 유지를 위한 최소한의 비용만을 투명하게 책정합니다.
        </p>
      </div>

      <div className="pricing-table-section">
        {/* 에셋토리 vs 타 플랫폼 대비 보드 */}
        <div className="pricing-comparison-table-neomorphic">
          <div className="table-header-row">
            <div className="header-cell">플랫폼 구분</div>
            <div className="header-cell highlighted-cell">Assetory (에셋토리)</div>
            <div className="header-cell">타 에셋 스토어</div>
          </div>

          <div className="table-data-row">
            <div className="data-cell font-bold">기본 중개 수수료</div>
            <div className="data-cell highlighted-cell font-black text-brand">단 10%</div>
            <div className="data-cell text-red-500 font-bold">30% ~ 50%</div>
          </div>

          <div className="table-data-row">
            <div className="data-cell font-bold">세금 및 원천징수</div>
            <div className="data-cell highlighted-cell font-semibold text-neutral-800">
              세법에 따른 3.3% 원천징수 후 실시간 예상 금액 고지
            </div>
            <div className="data-cell">해외 플랫폼 수수료 및 별도 세금 폭탄</div>
          </div>

          <div className="table-data-row">
            <div className="data-cell font-bold">정산 주기</div>
            <div className="data-cell highlighted-cell font-semibold text-neutral-800">
              구매 확정 후 익일 즉시 가상 정산 가능 금액 반영
            </div>
            <div className="data-cell">최소 14일 ~ 익월 말일 일괄 정산</div>
          </div>
        </div>
      </div>

      <div className="pricing-details-cards">
        <div className="details-neomorphic-card">
          <h3 className="card-title text-brand">01. 10% 수수료의 쓰임새</h3>
          <p className="card-desc">
            에셋토리가 공제하는 10%의 수수료는 호스팅 비용, 결제 모듈 연동 수수료, 그리고 창작자 간의 건강한 에셋 에코시스템을 유지하기 위한 최소한의 플랫폼 운영 리소스로만 사용됩니다.
          </p>
        </div>

        <div className="details-neomorphic-card">
          <h3 className="card-title text-neutral-800">02. 가상 정산 프로세스</h3>
          <p className="card-desc">
            에셋토리는 모의 결제 시스템을 기반으로 운영되므로 실제 신용카드 정산은 발생하지 않습니다. 대신 등록된 예상 정산 금액을 마이페이지 판매 대시보드에서 실시간으로 완벽하게 확인해 볼 수 있습니다.
          </p>
        </div>
      </div>

      <div className="pricing-cta-section mt-16 text-center">
        <Link to="/products" className="pricing-page-cta-btn">
          첫 에셋 등록하러 가기 &rarr;
        </Link>
      </div>
    </main>
  )
}
