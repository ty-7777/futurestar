import request from '@/utils/request'

// 个人中心接口
export function getProfile() {
  return request.get('/member/profile')
}

// 部分更新：只传要改的字段
export function updateProfile(data) {
  return request.put('/member/profile', data)
}

export function changePassword(data) {
  return request.put('/member/profile/password', data)
}

// 获取头像上传的OSS签名直传策略
export function getOssPolicy() {
  return request.get('/member/profile/oss-policy')
}
