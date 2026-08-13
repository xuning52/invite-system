const API_BASE = 'http://localhost:8080'

const TOKEN_KEY = 'invite-system.token'

export type Invitee = {
  username: string
  /** ISO 时间字符串 */
  registeredAt: string
}

export type Profile = {
  username: string
  inviteCode: string
  points: number
  createdAt: string
  invitees: Invitee[]
}

export type AuthResult = {
  token: string
  username: string
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/** 后端返回的 {"message": "..."}；网络不通时给一句人话。 */
async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...init,
      headers: {
        'Content-Type': 'application/json',
        ...(init.headers ?? {}),
      },
    })
  } catch {
    throw new Error('连不上后台服务，请确认后端已在 8080 端口启动')
  }

  if (response.status === 204) {
    return undefined as T
  }

  const body = await response.json().catch(() => null)
  if (!response.ok) {
    throw new Error(body?.message ?? `请求失败（${response.status}）`)
  }
  return body as T
}

function authHeaders(): Record<string, string> {
  const token = getToken()
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export function register(input: {
  username: string
  password: string
  inviteCode: string
}): Promise<AuthResult> {
  return request<AuthResult>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({
      username: input.username,
      password: input.password,
      // 留空表示没有邀请人
      inviteCode: input.inviteCode.trim() || null,
    }),
  })
}

export function login(input: {
  username: string
  password: string
}): Promise<AuthResult> {
  return request<AuthResult>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(input),
  })
}

export function fetchProfile(): Promise<Profile> {
  return request<Profile>('/api/me', { headers: authHeaders() })
}

export async function logout(): Promise<void> {
  try {
    await request<void>('/api/auth/logout', {
      method: 'POST',
      headers: authHeaders(),
    })
  } catch {
    // 后台没响应也要让用户退出登录
  }
  clearToken()
}
