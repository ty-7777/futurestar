<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()

const phone = ref('')
const password = ref('')
const loading = ref(false)

const onSubmit = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) return ElMessage.warning('请输入正确的手机号')
  if (!password.value) return ElMessage.warning('请输入密码')
  loading.value = true
  try {
    await userStore.login({ phone: phone.value, password: password.value })
    if (userStore.role !== 'ADMIN') {
      await userStore.logout()
      ElMessage.error('该账号不是管理员，无法登录后台')
      return
    }
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
    <el-card class="login__card">
      <div class="login__brand">⚽</div>
      <h2 class="login__title">智星足球青训 · 管理后台</h2>
      <el-form label-position="top" @submit.prevent>
        <el-form-item label="手机号">
          <el-input v-model="phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="onSubmit"
          />
        </el-form-item>
        <el-button type="primary" class="login__btn" :loading="loading" @click="onSubmit">
          登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}
.login__card {
  width: 380px;
  padding: 20px 24px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 28px rgba(47, 124, 255, 0.12);
}
.login__brand {
  width: 56px;
  height: 56px;
  margin: 0 auto 12px;
  border-radius: 18px;
  background: linear-gradient(135deg, #2f7cff, #46c3ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  box-shadow: 0 6px 18px rgba(47, 124, 255, 0.3);
}
.login__title {
  text-align: center;
  margin: 0 0 24px;
}
.login__btn {
  width: 100%;
}
</style>
