<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { resetPassword } from '@/api/auth'
import { useSendCode } from '@/composables/useSendCode'

const router = useRouter()
const { sending, counting, seconds, send } = useSendCode()

const phone = ref('')
const code = ref('')
const password = ref('')
const confirm = ref('')
const loading = ref(false)

const PASSWORD_RULE = /^(?=.*[A-Za-z])(?=.*\d).{8,32}$/

const onSendCode = () => send(phone.value)

const onSubmit = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) return showToast('请输入正确的手机号')
  if (!code.value) return showToast('请输入验证码')
  if (!PASSWORD_RULE.test(password.value)) return showToast('密码需8-32位且包含字母和数字')
  if (password.value !== confirm.value) return showToast('两次输入的密码不一致')
  loading.value = true
  try {
    await resetPassword({ phone: phone.value, code: code.value, password: password.value })
    showToast('重置成功，请用新密码登录')
    router.replace({ path: '/login', query: { phone: phone.value } })
  } catch {
    // 错误提示已由请求层统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="forgot">
    <van-nav-bar title="找回密码" left-arrow @click-left="router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="phone" label="手机号" placeholder="请输入手机号" type="tel" maxlength="11">
          <template #button>
            <van-button
              size="small"
              type="primary"
              :disabled="counting"
              :loading="sending"
              @click="onSendCode"
            >
              {{ counting ? `${seconds}s后重发` : '获取验证码' }}
            </van-button>
          </template>
        </van-field>
        <van-field v-model="code" label="验证码" placeholder="请输入验证码" maxlength="6" />
        <van-field v-model="password" label="新密码" placeholder="8-32位，含字母和数字" type="password" />
        <van-field v-model="confirm" label="确认密码" placeholder="请再次输入新密码" type="password" />
      </van-cell-group>
      <div class="forgot__btn">
        <van-button type="primary" block round native-type="submit" :loading="loading">
          重置密码
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.forgot__btn {
  padding: 24px 16px;
}
</style>
