<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const phone = ref(route.query.phone || '')
const password = ref('')
const loading = ref(false)

const onSubmit = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) return showToast('请输入正确的手机号')
  if (!password.value) return showToast('请输入密码')
  loading.value = true
  try {
    await userStore.login({ phone: phone.value, password: password.value })
    router.replace('/')
  } catch {
    // 错误提示已由请求层统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login">
    <div class="login__brand">⚽</div>
    <div class="login__title">智星足球青训</div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="phone" label="手机号" placeholder="请输入手机号" type="tel" maxlength="11" />
        <van-field v-model="password" label="密码" placeholder="请输入密码" type="password" />
      </van-cell-group>
      <div class="login__btn">
        <van-button type="primary" block round native-type="submit" :loading="loading">
          登录
        </van-button>
      </div>
    </van-form>
    <div class="login__links">
      <span class="link" @click="router.push('/forgot-password')">忘记密码</span>
      <span class="link" @click="router.push('/register')">注册账号</span>
    </div>
  </div>
</template>

<style scoped>
.login {
  padding-top: 14vh;
}
.login__brand {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  border-radius: 24px;
  background: var(--fs-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  box-shadow: var(--fs-shadow-lg);
}
.login__title {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 32px;
}
.login__btn {
  padding: 24px 16px;
}
.login__links {
  display: flex;
  justify-content: space-between;
  padding: 0 24px;
}
.link {
  color: #16a34a;
  font-size: 14px;
}
</style>
