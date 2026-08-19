import request from '@/utils/request'

// 赛事管理（管理端）
export function getAdminEvents(params) {
  return request.get('/admin/event', { params })
}

export function createEvent(data) {
  return request.post('/admin/event', data)
}

export function updateEvent(id, data) {
  return request.put(`/admin/event/${id}`, data)
}

export function deleteEvent(id) {
  return request.delete(`/admin/event/${id}`)
}

export function getRegistrations(id, params) {
  return request.get(`/admin/event/${id}/registrations`, { params })
}
