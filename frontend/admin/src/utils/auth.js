// Token 与用户信息的本地持久化（localStorage，按端口隔离）
const TOKEN_KEY = 'fs_token'
const USER_KEY = 'fs_user'

function safeParse(json) {
  try {
    return json ? JSON.parse(json) : null
  } catch {
    return null
  }
}

export function getTokens() {
  return safeParse(localStorage.getItem(TOKEN_KEY))
}

export function setTokens(tokens) {
  localStorage.setItem(TOKEN_KEY, JSON.stringify(tokens))
}

export function clearTokens() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUser() {
  return safeParse(localStorage.getItem(USER_KEY))
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearUser() {
  localStorage.removeItem(USER_KEY)
}
