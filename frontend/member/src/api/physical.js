import request from '@/utils/request'

// 体能记录接口
export function addRecord(data) {
  return request.post('/member/physical', data)
}

export function getRecords(params) {
  return request.get('/member/physical', { params })
}

export function getTrend(months = 6) {
  return request.get('/member/physical/trend', { params: { months } })
}
