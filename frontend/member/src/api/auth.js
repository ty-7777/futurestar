import request from '@/utils/request'

// 认证接口
export function login(data) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function sendCode(data) {
  return request.post('/auth/send-code', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function resetPassword(data) {
  return request.post('/auth/reset-password', data)
}
