import request from '@/utils/request'

// 消息管理（管理端）
export function getAdminMessages(params) {
  return request.get('/admin/message', { params })
}

export function sendMessage(data) {
  return request.post('/admin/message/send', data)
}

export function batchSendMessage(data) {
  return request.post('/admin/message/batch-send', data)
}
