import request from '@/utils/request'

// 赛事活动接口
export function getEvents(params) {
  return request.get('/member/event', { params })
}

export function getEventDetail(id) {
  return request.get(`/member/event/${id}`)
}

export function registerEvent(id) {
  return request.post(`/member/event/${id}/register`)
}

export function myEvents(params) {
  return request.get('/member/event/my', { params })
}

// 未报名会报错，探测用 silent 静默
export function getCheckinStatus(id) {
  return request.get(`/member/event/${id}/checkin-status`, { silent: true })
}

export function checkin(id) {
  return request.post(`/member/event/${id}/checkin`)
}
