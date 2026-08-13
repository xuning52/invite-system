import { useState } from 'react'
import type { Profile } from '../api'
import { formatDateTime } from '../format'

type Props = {
  profile: Profile
  onRefresh: () => void
  onLogout: () => void
}

export function ProfilePage({ profile, onRefresh, onLogout }: Props) {
  const [copied, setCopied] = useState(false)

  async function copyCode() {
    try {
      await navigator.clipboard.writeText(profile.inviteCode)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch {
      // 浏览器不给剪贴板权限时，用户手动选中复制即可
      setCopied(false)
    }
  }

  return (
    <div className="card profile">
      <header className="profile-header">
        <div>
          <h1>{profile.username}</h1>
          <p className="subtitle">注册于 {formatDateTime(profile.createdAt)}</p>
        </div>
        <button type="button" className="link" onClick={onLogout}>
          退出登录
        </button>
      </header>

      <section className="stats">
        <div className="stat">
          <span className="stat-label">我的积分</span>
          <span className="stat-value">{profile.points}</span>
        </div>
        <div className="stat">
          <span className="stat-label">已邀请</span>
          <span className="stat-value">{profile.invitees.length} 人</span>
        </div>
      </section>

      <section>
        <h2>我的邀请码</h2>
        <div className="invite-code-row">
          <code className="invite-code">{profile.inviteCode}</code>
          <button type="button" className="secondary" onClick={copyCode}>
            {copied ? '已复制' : '复制'}
          </button>
        </div>
        <p className="hint">
          把这串码发给朋友，他在注册页填进「邀请码」，你就能 +10 积分。
        </p>
      </section>

      <section>
        <div className="section-header">
          <h2>我邀请的用户</h2>
          <button type="button" className="link" onClick={onRefresh}>
            刷新
          </button>
        </div>
        {profile.invitees.length === 0 ? (
          <p className="empty">还没有人通过你的邀请码注册。</p>
        ) : (
          <table className="invitees">
            <thead>
              <tr>
                <th>用户名</th>
                <th>注册时间</th>
              </tr>
            </thead>
            <tbody>
              {profile.invitees.map((invitee) => (
                <tr key={invitee.username}>
                  <td>{invitee.username}</td>
                  <td>{formatDateTime(invitee.registeredAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  )
}
