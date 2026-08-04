import { Link } from 'react-router-dom'

const principles = [
  { number: '01', title: '판매가의 10%', description: '플랫폼 수수료는 판매가의 10%로 고정됩니다. 복잡한 구간별 요금이나 별도 옵션 비용은 없습니다.' },
  { number: '02', title: '90% 예상 정산', description: '결제 완료 매출과 환불 완료 차감을 기준으로, 판매자가 받을 것으로 예상되는 금액을 확인합니다.' },
  { number: '03', title: '실제 송금 없음', description: 'Assetory는 Mock 결제를 사용합니다. 실제 PG 결제와 송금 정산은 제공하지 않습니다.' },
]

export function PricingPage() {
  return (
    <main className="pricing-page-container pricing-page">
      <section className="pricing-statement" aria-labelledby="pricing-title">
        <span className="pricing-statement__eyebrow">SIMPLE &amp; TRANSPARENT</span>
        <h1 id="pricing-title">수수료는<br /><strong>단 10%</strong>입니다.</h1>
        <p>판매가의 90%는 창작자의 다음 작업을 위해 남겨둡니다.</p>
        <div className="pricing-statement__formula" aria-label="예상 정산 계산 예시">
          <span>판매가 <b>₩100,000</b></span><i>−</i><span>수수료 <b>₩10,000</b></span><i>=</i><strong>예상 정산 <b>₩90,000</b></strong>
        </div>
        <div className="pricing-statement__shape pricing-statement__shape--one" aria-hidden="true" />
        <div className="pricing-statement__shape pricing-statement__shape--two" aria-hidden="true" />
      </section>

      <section className="pricing-principles" aria-labelledby="pricing-principles-title">
        <header>
          <span>HOW IT WORKS</span>
          <h2 id="pricing-principles-title">가격은 단순하게,<br /><span className="pricing-title-keep">판매 흐름은 투명하게</span></h2>
          <p>판매자는 복잡한 계산 대신 상품과 작업에 집중할 수 있어야 한다고 생각합니다.</p>
        </header>
        <div className="pricing-principles__list">
          {principles.map((principle) => <article key={principle.number}>
            <span>{principle.number}</span><h3>{principle.title}</h3><p>{principle.description}</p>
          </article>)}
        </div>
      </section>

      <section className="pricing-note" aria-labelledby="pricing-note-title">
        <div><span>SELLER DASHBOARD</span><h2 id="pricing-note-title">판매가 쌓일수록,<br /><span className="pricing-title-keep">다음 선택은 더 선명하게</span></h2></div>
        <div className="pricing-note__receipt" aria-hidden="true"><span>이번 달 판매</span><strong>₩ 1,240,000</strong><i /><span>예상 정산</span><b>₩ 1,116,000</b><em>수수료 10% 반영</em></div>
      </section>

      <section className="pricing-page-cta" aria-labelledby="pricing-cta-title">
        <span>READY WHEN YOU ARE</span><h2 id="pricing-cta-title">판매할 준비가 되었다면,<br /><span className="pricing-title-keep">첫 상품부터 시작해 보세요</span></h2>
        <div><Link to="/products">상품 탐색하기 <i aria-hidden="true">→</i></Link><Link to="/login">판매자 시작하기 <i aria-hidden="true">→</i></Link></div>
      </section>
    </main>
  )
}
