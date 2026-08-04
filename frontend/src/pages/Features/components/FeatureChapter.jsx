import { useEffect, useRef, useState } from 'react'

function FeatureVisual({ type }) {
  if (type === 'files') return <div className="feature-visual-files"><span>CODE</span><span>DESIGN</span><span>TEMPLATE</span></div>
  if (type === 'store') return <div className="feature-visual-store"><span>상품 소개</span><i>대표 이미지</i><strong>₩ 24,000</strong></div>
  if (type === 'search') return <div className="feature-visual-search"><span>필요한 에셋 검색</span><i>⌕</i><em>UI KIT · 128</em></div>
  if (type === 'checkout') return <div className="feature-visual-checkout"><span>장바구니</span><b>₩ 24,000</b><strong>결제 완료 ✓</strong></div>
  if (type === 'library') return <div className="feature-visual-library"><span>다운로드 가능</span><span>언제든 다시 이용</span><span>나의 구매 자료</span></div>
  if (type === 'chat') return <div className="feature-visual-chat"><span>상품에 대해 궁금한 점이 있어요.</span><strong>무엇이든 편하게 물어보세요.</strong></div>
  if (type === 'shield') return <div className="feature-visual-shield"><span>후기</span><strong>환불 처리</strong><span>접근 권한 관리</span></div>
  if (type === 'team') return <div className="feature-visual-team"><span>OWNER</span><span>EDITOR</span><span>VIEWER</span></div>
  if (type === 'approval') return <div className="feature-visual-approval"><span>변경 요청</span><strong>승인 대기</strong><em>최종 승인 → 반영</em></div>
  return <div className="feature-visual-chart"><span /><span /><span /><span /><em>+ 24.8%</em></div>
}

export function FeatureChapter({ chapter, features, isReversed }) {
  const chapterRef = useRef(null)
  const [isVisible, setIsVisible] = useState(false)

  useEffect(() => {
    const element = chapterRef.current
    if (!element) return undefined

    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        setIsVisible(true)
        observer.unobserve(element)
      }
    }, { threshold: 0.18 })

    observer.observe(element)
    return () => observer.disconnect()
  }, [])

  const chapterFeatures = features.filter((feature) => feature.stage === chapter.stage)

  return (
    <article ref={chapterRef} className={`feature-chapter feature-chapter--${chapter.stage} ${isReversed ? 'feature-chapter--reverse' : ''} ${isVisible ? 'is-revealed' : ''}`}>
      <div className="feature-chapter-intro">
        <span>{chapter.eyebrow}</span>
        <h3>{chapter.title}</h3>
        <p>{chapter.description}</p>
        <div className="feature-chapter-art" aria-hidden="true"><FeatureVisual type={chapter.visual} /></div>
      </div>
      <div className="feature-chapter-details">
        {chapterFeatures.map((feature) => (
          <section className="feature-detail" key={feature.number}>
            <span>{feature.number} · {feature.role}</span>
            <h4>{feature.title}</h4>
            <p>{feature.description}</p>
            <ul>{feature.tags.map((tag) => <li key={tag}>{tag}</li>)}</ul>
          </section>
        ))}
      </div>
    </article>
  )
}
