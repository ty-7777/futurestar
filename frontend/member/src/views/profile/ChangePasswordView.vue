<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { changePassword } from '@/api/profile'

const router = useRouter()

const oldPassword = ref('')
const newPassword = ref('')
const confirm = ref('')
const loading = ref(false)

const PASSWORD_RULE = /^(?=.*[A-Za-z])(?=.*\d).{8,32}$/

const onSubmit = async () => {
  if (!oldPassword.value) return showToast('请输入原密码')
  if (!PASSWORD_RULE.test(newPassword.value)) return showToast('新密码需8-32位且包含字母和数字')
  if (newPassword.value === oldPassword.value) return showToast('新密码不能与原密码相同')
  if (newPassword.value !== confirm.value) return showToast('两次输入的新密码不一致')
  loading.value = true
  try {
    await changePassword({ oldPassword: oldPassword.value, newPassword: newPassword.value })
    showToast('修改成功')
    router.back()
  } catch {
    // 错误提示已由请求层统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="change-password">
    <van-nav-bar title="修改密码" left-arrow @click-left="router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="oldPassword" label="原密码" placeholder="请输入原密码" type="password" />
        <van-field v-model="newPassword" label="新密码" placeholder="8-32位，含字母和数字" type="password" />
        <van-field v-model="confirm" label="确认密码" placeholder="请再次输入新密码" type="password" />
      </van-cell-group>
      <div class="change-password__btn">
        <van-button type="primary" block round native-type="submit" :loading="loading">
          确认修改
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.change-password__btn {
  padding: 24px 16px;
}
</style>
