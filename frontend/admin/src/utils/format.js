// 后端日期时间字符串 → "YYYY-MM-DD HH:mm"
export function formatDate(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
