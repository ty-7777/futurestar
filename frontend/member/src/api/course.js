import request from '@/utils/request'

// 课程预约接口
export function getPackages(params) {
  return request.get('/member/course/packages', { params })
}

export function getPackageDetail(id) {
  return request.get(`/member/course/packages/${id}`)
}

export function getSlots(packageId, date) {
  return request.get(`/member/course/packages/${packageId}/slots`, { params: { date } })
}

export function createAppointment(slotId) {
  return request.post('/member/course/appointment', { slotId })
}

export function getAppointments(params) {
  return request.get('/member/course/appointment/list', { params })
}

export function cancelAppointment(id) {
  return request.post(`/member/course/appointment/${id}/cancel`)
}
