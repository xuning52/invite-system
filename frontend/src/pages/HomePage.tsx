type Props = {
  onRegister: () => void
  onLogin: () => void
}

export function HomePage({ onRegister, onLogin }: Props) {
  return (
    <div className="card home">
      <h1>邀请注册系统</h1>
      <p className="subtitle">
        注册后你会拿到一个专属邀请码，别人用它注册，你就能得到积分。
      </p>
      <div className="actions">
        <button type="button" className="primary" onClick={onRegister}>
          注册
        </button>
        <button type="button" className="secondary" onClick={onLogin}>
          登录
        </button>
      </div>
    </div>
  )
}
