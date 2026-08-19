import request from '@/utils/request'

export function getPlayers(params) {
  return request.get('/admin/players', { params })
}

export function getPlayerDetail(id) {
  return request.get(`/admin/players/${id}`)
}

export function updatePlayerStatus(id, status) {
  return request.put(`/admin/players/${id}/status`, { status })
}

export function updatePlayerLevel(id, memberLevel) {
  return request.put(`/admin/players/${id}/level`, { memberLevel })
}

export function updatePlayerPoints(id, data) {
  return request.put(`/admin/players/${id}/points`, data)
}

export function resetPlayerPassword(id) {
  return request.put(`/admin/players/${id}/reset-password`)
}
