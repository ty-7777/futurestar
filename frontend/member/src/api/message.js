import request from '@/utils/request'

// 消息接口
export function getMessages(params) {
  return request.get('/member/message', { params })
}

export function getMessageDetail(id) {
  return request.get(`/member/message/${id}`)
}

export function markRead(id) {
  return request.put(`/member/message/${id}/read`)
}

export function getUnreadCount() {
  return request.get('/member/message/unread-count')
}
