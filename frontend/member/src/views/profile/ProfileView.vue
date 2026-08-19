<script setup>
import { onMounted } from 'vue'
import { showConfirmDialog } from 'vant'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getProfile } from '@/api/profile'

const userStore = useUserStore()
const router = useRouter()

const LEVEL_LABEL = {
  NORMAL: '普通会员',
  SILVER: '白银会员',
  GOLD: '黄金会员',
  PLATINUM: '铂金会员',
  DIAMOND: '钻石会员'
}

onMounted(async () => {
  try {
    const profile = await getProfile()
    userStore.setUser(profile)
  } catch {
    // 忽略（未登录等情况）
  }
})

const onLogout = async () => {
  try {
    await showConfirmDialog({ title: '提示', message: '确定退出登录吗？' })
  } catch {
    return
  }
  await userStore.logout()
  router.replace('/login')
}
</script>

<template>
  <div class="profile">
    <van-nav-bar title="个人中心" />
    <div class="profile__card">
      <van-image
        v-if="userStore.user?.avatar"
        round
        width="56"
        height="56"
        :src="userStore.user.avatar"
      />
      <div v-else class="profile__avatar">
        <van-icon name="user-o" size="28" />
      </div>
      <div class="profile__info">
        <div class="profile__name">{{ userStore.user?.realName || userStore.user?.phone }}</div>
        <div class="profile__meta">
          <span>{{ LEVEL_LABEL[userStore.user?.memberLevel] || '普通会员' }}</span>
          <span>积分 {{ userStore.user?.points ?? 0 }}</span>
        </div>
      </div>
    </div>
    <van-cell-group inset class="profile__menu">
      <van-cell title="编辑资料" icon="edit" is-link @click="router.push('/profile/edit')" />
      <van-cell title="修改密码" icon="lock" is-link @click="router.push('/profile/password')" />
    </van-cell-group>
    <div class="profile__logout">
      <van-button block round @click="onLogout">退出登录</van-button>
    </div>
  </div>
</template>

<style scoped>
.profile__card {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 16px;
  padding: 20px 16px;
  background: var(--fs-gradient);
  border-radius: 12px;
  color: #fff;
}
.profile__avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}
.profile__name {
  font-size: 18px;
  font-weight: 600;
}
.profile__meta {
  margin-top: 6px;
  display: flex;
  gap: 12px;
  font-size: 13px;
  opacity: 0.9;
}
.profile__logout {
  margin: 24px 16px;
}
</style>
