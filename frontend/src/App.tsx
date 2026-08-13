import { useCallback, useEffect, useState } from 'react'
import './App.css'
import {
  clearToken,
  fetchProfile,
  getToken,
  logout as logoutRequest,
  type Profile,
} from './api'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { ProfilePage } from './pages/ProfilePage'
import { RegisterPage } from './pages/RegisterPage'

type View = 'home' | 'register' | 'login' | 'profile'

function App() {
  const [view, setView] = useState<View>('home')
  const [profile, setProfile] = useState<Profile | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  /** 拉个人主页数据；token 失效就退回首页。 */
  const loadProfile = useCallback(async () => {
    setError('')
    try {
      setProfile(await fetchProfile())
      setView('profile')
    } catch (err) {
      clearToken()
      setProfile(null)
      setView('home')
      setError(err instanceof Error ? err.message : '加载失败')
    }
  }, [])

  // 刷新页面后，localStorage 里还有 token 就直接回到个人主页
  useEffect(() => {
    if (!getToken()) {
      setLoading(false)
      return
    }
    loadProfile()
      .catch(() => undefined)
      .finally(() => setLoading(false))
  }, [loadProfile])

  async function handleLogout() {
    await logoutRequest()
    setProfile(null)
    setView('home')
  }

  function goHome() {
    setError('')
    setView('home')
  }

  if (loading) {
    return (
      <main className="app">
        <p className="loading">加载中…</p>
      </main>
    )
  }

  return (
    <main className="app">
      {view === 'home' && (
        <>
          {error && <p className="error banner">{error}</p>}
          <HomePage
            onRegister={() => setView('register')}
            onLogin={() => setView('login')}
          />
        </>
      )}

      {view === 'register' && (
        <RegisterPage
          onRegistered={loadProfile}
          onBack={goHome}
          onGoLogin={() => setView('login')}
        />
      )}

      {view === 'login' && (
        <LoginPage
          onLoggedIn={loadProfile}
          onBack={goHome}
          onGoRegister={() => setView('register')}
        />
      )}

      {view === 'profile' && profile && (
        <ProfilePage
          profile={profile}
          onRefresh={loadProfile}
          onLogout={handleLogout}
        />
      )}
    </main>
  )
}

export default App
