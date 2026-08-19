import axios from 'axios'
import { showToast } from 'vant'
import { getTokens, setTokens, clearTokens } from './auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截：自动带 Access Token
request.interceptors.request.use((config) => {
  const { accessToken } = getTokens() || {}
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`
  return config
})

// ---- 双 Token 刷新（单飞，防止并发重复刷新）----
let isRefreshing = false
let queue = []

async function handle401(config) {
  // 已有刷新在进行，排队等待后重试
  if (isRefreshing) {
    await new Promise((resolve, reject) => queue.push({ resolve, reject }))
    return request(config)
  }

  isRefreshing = true
  try {
    const refreshToken = getTokens()?.refreshToken
    if (!refreshToken) throw new Error('缺少 refreshToken')
    const { data } = await axios.post('/api/auth/refresh', { refreshToken })
    setTokens(data.data)
    queue.forEach(({ resolve }) => resolve())
    queue = []
    return request(config)
  } catch (e) {
    queue.forEach(({ reject }) => reject(e))
    queue = []
    clearTokens()
    showToast('登录已过期，请重新登录')
    window.location.href = '/login'
    throw e
  } finally {
    isRefreshing = false
  }
}

// 响应拦截：解包统一响应 + 401 刷新重试 + 统一错误提示
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      showToast(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res.data
  },
  async (error) => {
    const { response, config } = error
    // 有 refreshToken 才尝试刷新（避免登录等无凭证请求误入刷新流程）
    if (response?.status === 401 && config && !config._retry && getTokens()?.refreshToken) {
      config._retry = true
      return handle401(config)
    }
    const status = response?.status
    const message = response?.data?.message
    // silent: 调用方主动忽略错误提示（如探测类接口）
    if (!config?.silent) {
      if (status === 403) showToast(message || '无权限操作')
      else if (status === 404) showToast(message || '资源不存在')
      else if (status === 409) showToast(message || '操作冲突')
      else if (status === 429) showToast(message || '操作过于频繁，请稍后再试')
      else if (status === 500) showToast(message || '服务器内部错误')
      else if (!response) showToast('网络异常，请检查网络')
      else if (message) showToast(message)
    }
    return Promise.reject(error)
  }
)

export default request
