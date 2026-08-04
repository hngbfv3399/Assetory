import { Link } from 'react-router-dom'

export function SiteFooter() {
  return (
    <footer className="site-footer">
      <div className="site-footer__inner">
        <Link className="site-footer__brand" to="/">Assetory</Link>
        <p>당신의 작업이 누군가의 시간을 덜어주도록.</p>
        <small>© 2026 Assetory Market Platform. All rights reserved.</small>
      </div>
    </footer>
  )
}
