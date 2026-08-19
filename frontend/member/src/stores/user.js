import { defineStore } from 'pinia'
import {
  getTokens,
  setTokens,
  clearTokens,
  getUser,
  setUser,
  clearUser
} from '@/utils/auth'
import { login as loginApi, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    tokens: getTokens(),
    user: getUser()
  }),
  getters: {
    isLoggedIn: (s) => !!s.tokens?.accessToken,
    role: (s) => s.user?.role,
    points: (s) => s.user?.points
  },
  actions: {
    async login(payload) {
      const data = await loginApi(payload)
      this.tokens = { accessToken: data.accessToken, refreshToken: data.refreshToken }
      this.user = data.user
      setTokens(this.tokens)
      setUser(this.user)
      return data
    },
    async logout() {
      try {
        await logoutApi()
      } catch {
        // 后端登出失败不阻塞本地清理
      }
      this.tokens = null
      this.user = null
      clearTokens()
      clearUser()
    },
    setUser(user) {
      this.user = user
      setUser(user)
    }
  }
})
