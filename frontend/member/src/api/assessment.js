import request from '@/utils/request'

// 技术/体能评测接口
export function getQuestionnaires() {
  return request.get('/member/assessment/questionnaires')
}

export function getQuestions(id) {
  return request.get(`/member/assessment/questionnaires/${id}/questions`)
}

// answers 为 JSON 字符串：{ version: 1, items: [{ qid, type, value }] }
export function submitAssessment(data) {
  return request.post('/member/assessment', data)
}

export function getHistory(params) {
  return request.get('/member/assessment/history', { params })
}

export function getAssessmentDetail(id) {
  return request.get(`/member/assessment/${id}`)
}
