import request from '@/utils/request'

// 评测管理（管理端）
export function getAdminQuestionnaires(params) {
  return request.get('/admin/assessment/questionnaires', { params })
}

export function createQuestionnaire(data) {
  return request.post('/admin/assessment/questionnaires', data)
}

export function updateQuestionnaire(id, data) {
  return request.put(`/admin/assessment/questionnaires/${id}`, data)
}

export function deleteQuestionnaire(id) {
  return request.delete(`/admin/assessment/questionnaires/${id}`)
}

export function updateQuestionnaireStatus(id, status) {
  return request.put(`/admin/assessment/questionnaires/${id}/status`, { status })
}

export function getAdminQuestions(id) {
  return request.get(`/admin/assessment/questionnaires/${id}/questions`)
}

export function createQuestion(id, data) {
  return request.post(`/admin/assessment/questionnaires/${id}/questions`, data)
}

export function updateQuestion(qid, data) {
  return request.put(`/admin/assessment/questions/${qid}`, data)
}

export function deleteQuestion(qid) {
  return request.delete(`/admin/assessment/questions/${qid}`)
}
