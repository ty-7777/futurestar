import request from '@/utils/request'

// 系统配置（管理端）
export function getConfigs() {
  return request.get('/admin/config')
}

export function getConfig(key) {
  return request.get(`/admin/config/${key}`)
}

export function updateConfig(key, configValue) {
  return request.put(`/admin/config/${key}`, { configValue })
}
