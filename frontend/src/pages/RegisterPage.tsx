import { useState, type FormEvent } from 'react'
import { register, setToken } from '../api'

type Props = {
  onRegistered: () => void
  onBack: () => void
  onGoLogin: () => void
}

export function RegisterPage({ onRegistered, onBack, onGoLogin }: Props) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [inviteCode, setInviteCode] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const result = await register({ username, password, inviteCode })
      setToken(result.token)
      onRegistered()
    } catch (err) {
      // 用户名重复、邀请码无效等提示都从这里显示
      setError(err instanceof Error ? err.message : '注册失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="card">
      <h1>注册</h1>
      <form onSubmit={handleSubmit}>
        <label>
          用户名
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="2-32 个字符"
            autoComplete="username"
          />
        </label>
        <label>
          密码
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="至少 6 个字符"
            autoComplete="new-password"
          />
        </label>
        <label>
          邀请码<span className="optional">（选填）</span>
          <input
            value={inviteCode}
            onChange={(e) => setInviteCode(e.target.value)}
            placeholder="没有就留空"
            autoComplete="off"
          />
        </label>

        {error && <p className="error">{error}</p>}

        <button type="submit" className="primary" disabled={submitting}>
          {submitting ? '提交中…' : '注册'}
        </button>
      </form>
      <p className="hint">
        已经有账号了？
        <button type="button" className="link" onClick={onGoLogin}>
          去登录
        </button>
      </p>
      <button type="button" className="link back" onClick={onBack}>
        ← 返回首页
      </button>
    </div>
  )
}
