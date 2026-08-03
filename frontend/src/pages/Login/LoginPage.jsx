import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

export function LoginPage() {
  const navigate = useNavigate()
  const [isRegister, setIsRegister] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    if (!email || !password || (isRegister && !name)) {
      toast.error('모든 정보를 채워주세요.')
      return
    }

    if (isRegister) {
      toast.success(`${name}님, 회원가입이 완료되었습니다! (Mock)`)
      setIsRegister(false)
    } else {
      toast.success('로그인에 성공했습니다! (Mock)')
      navigate('/')
    }
  }

  return (
    <main className="login-page-container">
      <div className="login-neomorphic-card">
        <h2 className="login-card-title text-brand">
          {isRegister ? 'Assetory 회원가입' : 'Assetory 로그인'}
        </h2>
        <p className="login-card-subtitle">
          {isRegister
            ? '에셋토리에 가입하고 당신의 에셋 가치를 마켓에 올려보세요.'
            : '아이디어 에셋을 구매하고 100시간의 개발을 아껴보세요.'}
        </p>

        <form onSubmit={handleSubmit} className="login-form-fields">
          {isRegister && (
            <div className="form-group-slot">
              <label htmlFor="name-input" className="form-label">이름</label>
              <input
                id="name-input"
                type="text"
                className="neomorphic-text-input"
                placeholder="홍길동"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>
          )}

          <div className="form-group-slot">
            <label htmlFor="email-input" className="form-label">이메일 주소</label>
            <input
              id="email-input"
              type="email"
              className="neomorphic-text-input"
              placeholder="user@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group-slot">
            <label htmlFor="password-input" className="form-label">비밀번호</label>
            <input
              id="password-input"
              type="password"
              className="neomorphic-text-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button type="submit" className="login-submit-btn">
            {isRegister ? '회원가입하기' : '로그인하기'}
          </button>
        </form>

        <div className="login-toggle-switch">
          <button
            type="button"
            className="toggle-text-btn font-black text-brand"
            onClick={() => setIsRegister(!isRegister)}
          >
            {isRegister ? '이미 계정이 있으신가요? 로그인' : '처음이신가요? 회원가입'}
          </button>
        </div>
      </div>
    </main>
  )
}
