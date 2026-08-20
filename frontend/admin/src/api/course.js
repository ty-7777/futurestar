import request from '@/utils/request'

// 课程管理（管理端）
export function getAdminPackages(params) {
  return request.get('/admin/course/packages', { params })
}

export function createPackage(data) {
  return request.post('/admin/course/packages', data)
}

export function updatePackage(id, data) {
  return request.put(`/admin/course/packages/${id}`, data)
}

export function deletePackage(id) {
  return request.delete(`/admin/course/packages/${id}`)
}

export function batchCreateSlots(id, data) {
  return request.post(`/admin/course/packages/${id}/slots/batch`, data)
}

export function getAdminSlots(params) {
  return request.get('/admin/course/slots', { params })
}

export function updateSlot(id, data) {
  return request.put(`/admin/course/slots/${id}`, data)
}

export function getAdminAppointments(params) {
  return request.get('/admin/course/appointments', { params })
}

export function confirmAppointment(id) {
  return request.put(`/admin/course/appointments/${id}/confirm`)
}

export function rejectAppointment(id) {
  return request.put(`/admin/course/appointments/${id}/reject`)
}

export function uploadReport(id, file) {
  const form = new FormData()
  form.append('file', file)
  return request.post(`/admin/course/appointments/${id}/report`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
