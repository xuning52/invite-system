import { useState, type FormEvent } from 'react'
import { login, setToken } from '../api'

type Props = {
  onLoggedIn: () => void
  onBack: () => void
  onGoRegister: () => void
}

export function LoginPage({ onLoggedIn, onBack, onGoRegister }: Props) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const result = await login({ username, password })
      setToken(result.token)
      onLoggedIn()
    } catch (err) {
      setError(err instanceof Error ? err.message : '登录失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="card">
      <h1>登录</h1>
      <form onSubmit={handleSubmit}>
        <label>
          用户名
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
          />
        </label>
        <label>
          密码
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </label>

        {error && <p className="error">{error}</p>}

        <button type="submit" className="primary" disabled={submitting}>
          {submitting ? '登录中…' : '登录'}
        </button>
      </form>
      <p className="hint">
        还没有账号？
        <button type="button" className="link" onClick={onGoRegister}>
          去注册
        </button>
      </p>
      <button type="button" className="link back" onClick={onBack}>
        ← 返回首页
      </button>
    </div>
  )
}
