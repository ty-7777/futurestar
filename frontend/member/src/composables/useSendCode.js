import { onUnmounted, ref } from 'vue'
import { showToast } from 'vant'
import { sendCode } from '@/api/auth'

// 发送验证码 + 60s 倒计时（注册/忘记密码共用）
export function useSendCode() {
  const sending = ref(false)
  const counting = ref(false)
  const seconds = ref(0)
  let timer = null

  const clearTimer = () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  const startCountdown = () => {
    counting.value = true
    seconds.value = 60
    clearTimer()
    timer = setInterval(() => {
      seconds.value--
      if (seconds.value <= 0) {
        clearTimer()
        counting.value = false
      }
    }, 1000)
  }

  onUnmounted(clearTimer)

  // 发送成功返回 true，失败返回 false（错误提示由请求层统一处理）
  const send = async (phone) => {
    if (counting.value) return false
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      showToast('请输入正确的手机号')
      return false
    }
    sending.value = true
    try {
      await sendCode({ phone })
      showToast('验证码已发送')
      startCountdown()
      return true
    } catch {
      return false
    } finally {
      sending.value = false
    }
  }

  return { sending, counting, seconds, send }
}
