import request from '@/utils/request'

// AI 对话接口（流式发送走 utils/sse.js）
// type: CHAT 普通对话 / ASSISTANT AI智能客服
export function createSession(data = {}) {
  return request.post('/member/chat/session', data)
}

export function sessionList(type = 'CHAT') {
  return request.get('/member/chat/session/list', { params: { type } })
}

export function getMessages(sessionId) {
  return request.get(`/member/chat/session/${sessionId}/messages`)
}

export function deleteSession(sessionId) {
  return request.delete(`/member/chat/session/${sessionId}`)
}
