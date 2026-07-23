import { useEffect, useState } from 'react'
import { fetchHealth } from './api/health'
import './App.css'

function App() {
  const [status, setStatus] = useState('백엔드 연결을 확인하는 중입니다.')

  useEffect(() => {
    fetchHealth()
      .then(() => setStatus('백엔드 연결이 정상입니다.'))
      .catch(() => setStatus('백엔드에 연결할 수 없습니다.'))
  }, [])

  return (
    <main className="app">
      <p className="eyebrow">DIGITAL MARKETPLACE</p>
      <h1>Assetory</h1>
      <p>디지털 상품 판매 플랫폼의 개발 기반을 준비하고 있습니다.</p>
      <p className="connection-status">{status}</p>
    </main>
  )
}

export default App
