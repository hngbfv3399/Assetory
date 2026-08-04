import { Link } from 'react-router-dom'
import { FeatureChapter } from './components/FeatureChapter.jsx'

const features = [
  { stage: 'start', number: '01', role: 'SELLER', title: <>처음이어도<br />바로 판매를 시작할 수 있게</>, description: '계정을 만들고 상품을 등록하면 판매를 시작할 수 있습니다. 책, 강의, 디자인 자료, 코드 등 다양한 작업을 하나의 상품으로 선보일 수 있습니다.', tags: ['계정 생성', '상품 등록', '판매 시작'], visual: 'files' },
  { stage: 'start', number: '02', role: 'SELLER', title: <>상품의 매력을<br />페이지에 담을 수 있게</>, description: '상품 소개와 대표 이미지, 가격을 직접 구성합니다. 구매자가 받을 자료를 미리 명확하게 보여줄 수 있습니다.', tags: ['상품 소개', '대표 이미지', '구매 자료'], visual: 'store' },
  { stage: 'sell', number: '03', role: 'BUYER', title: <>필요한 에셋을<br />더 빠르게 발견할 수 있게</>, description: '카테고리와 키워드로 필요한 상품을 찾습니다. 정렬과 후기를 참고해 지금의 작업에 맞는 에셋을 골라볼 수 있습니다.', tags: ['카테고리', '키워드 검색', '후기'], visual: 'search' },
  { stage: 'sell', number: '04', role: 'BUYER', title: <>결제하고<br />바로 이용할 수 있게</>, description: '필요한 상품을 장바구니에 담아 결제를 진행합니다. 결제가 끝나면 구매 자료는 내 라이브러리에 바로 준비됩니다.', tags: ['장바구니', 'Mock 결제', '자료 제공'], visual: 'checkout' },
  { stage: 'grow', number: '05', role: 'BUYER', title: <>구매한 작업물을<br />언제든 다시 이용할 수 있게</>, description: '구매한 디지털 자료는 한곳에 모아둡니다. 필요할 때 구매 내역에서 다시 이용할 수 있습니다.', tags: ['구매 내역', '다운로드 권한', '재이용'], visual: 'library' },
  { stage: 'grow', number: '06', role: 'SELLER', title: <>판매 이후의 흐름까지<br />관리할 수 있게</>, description: '주문과 후기, 판매 흐름을 한곳에서 확인합니다. 수수료 10%를 반영한 예상 정산도 함께 살펴볼 수 있습니다.', tags: ['주문 관리', '판매 통계', '예상 정산'], visual: 'chart' },
  { stage: 'trust', number: '07', role: 'BUYER', title: <>구매 전에 궁금한 점을<br />확인할 수 있게</>, description: '상품 문의 채팅으로 판매자에게 직접 질문합니다. 구매 전 필요한 정보를 확인하고 결정할 수 있습니다.', tags: ['상품 문의', '1:1 채팅', '참여자 권한'], visual: 'chat' },
  { stage: 'trust', number: '08', role: 'SELLER', title: <>후기와 환불까지<br />투명하게 관리할 수 있게</>, description: '구매 후기와 환불 요청을 판매 흐름 안에서 관리합니다. 환불이 완료되면 자료 접근 권한도 함께 회수됩니다.', tags: ['구매 후기', '환불 처리', '접근 권한 회수'], visual: 'shield' },
  { stage: 'together', number: '09', role: 'SELLER', title: <>한 상품을<br />함께 운영할 수 있게</>, description: '상품마다 관리자·편집자·뷰어 역할로 동료를 초대해 필요한 범위에서 함께 작업할 수 있습니다.', tags: ['공동 작업자', '역할 관리', '상품별 권한'], visual: 'team' },
  { stage: 'together', number: '10', role: 'OWNER', title: <>중요한 변경을<br />한 번 더 확인할 수 있게</>, description: '공동 작업자의 상품 수정과 판매 상태 변경은 변경 요청으로 남기고, 소유자가 최종 승인합니다.', tags: ['변경 요청', '최종 승인', '반려 사유'], visual: 'approval' },
]

const chapters = [
  { stage: 'start', eyebrow: '01 · Start selling', title: <>생각을 올리고,<br /><span className="chapter-title-keep">판매를 시작할 수 있게</span></>, description: '복잡한 쇼핑몰 설정 없이도, 디지털 상품에 필요한 정보와 자료를 준비해 바로 판매할 수 있습니다.', visual: 'files' },
  { stage: 'sell', eyebrow: '02 · Make it easy', title: <>구매부터 이용까지,<br /><span className="chapter-title-keep">기다리지 않을 수 있게</span></>, description: '필요한 상품을 찾고 결제한 뒤, 구매한 자료를 바로 사용할 수 있는 단순한 흐름을 만듭니다.', visual: 'checkout' },
  { stage: 'grow', eyebrow: '03 · Keep growing', title: <>거래 이후에도,<br /><span className="chapter-title-keep">다음 창작을 이어갈 수 있게</span></>, description: '구매 자료와 판매 흐름을 관리하고, 숫자를 통해 다음 아이디어의 방향을 발견합니다.', visual: 'chart' },
  { stage: 'trust', eyebrow: '04 · Build trust', title: <>대화와 신뢰를,<br /><span className="chapter-title-keep">거래 안에서 이어갈 수 있게</span></>, description: '판매자와 구매자가 안심하고 거래를 이어갈 수 있게, 문의·후기·환불의 흐름을 연결합니다.', visual: 'chat' },
  { stage: 'together', eyebrow: '05 · Work together', title: <>좋은 작업을<br /><span className="chapter-title-keep">함께 다듬을 수 있게</span></>, description: '혼자 만든 상품도, 함께 운영하는 상품도 각자의 역할과 최종 승인을 지키며 관리합니다.', visual: 'team' },
]

export function FeaturesPage() {
  return <main className="features-page-container">
    <section className="features-showcase-section features-chapters" aria-labelledby="features-showcase-title">
      <h2 id="features-showcase-title" className="sr-only">Assetory 주요 기능</h2>
      {chapters.map((chapter, index) => <FeatureChapter chapter={chapter} features={features} isReversed={index % 2 === 1} key={chapter.stage} />)}
    </section>
    <section className="features-cta-box" aria-labelledby="features-cta-title"><span className="section-eyebrow">Ready when you are</span><h2 id="features-cta-title">이제, 필요한 에셋을 찾아볼까요?</h2><p>누군가의 시간을 덜어주는 작업을 발견해 보세요.</p><Link to="/products" className="features-page-cta-btn">상품 탐색 시작하기 <span aria-hidden="true">→</span></Link></section>
  </main>
}
