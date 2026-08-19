<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const onLogout = async () => {
  try {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await userStore.logout()
  router.replace('/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="224px" class="admin-layout__aside">
      <div class="admin-layout__logo">
        <span class="admin-layout__logo-icon">⚽</span>
        <span class="admin-layout__logo-text">智星足球青训</span>
      </div>
      <el-menu router :default-active="route.path" class="admin-layout__menu">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>仪表盘
        </el-menu-item>
        <el-menu-item index="/players">
          <el-icon><UserFilled /></el-icon>球员管理
        </el-menu-item>
        <el-menu-item index="/course">
          <el-icon><Notebook /></el-icon>课程管理
        </el-menu-item>
        <el-menu-item index="/assessment">
          <el-icon><EditPen /></el-icon>评测管理
        </el-menu-item>
        <el-menu-item index="/event">
          <el-icon><Trophy /></el-icon>赛事管理
        </el-menu-item>
        <el-menu-item index="/message">
          <el-icon><ChatDotRound /></el-icon>消息管理
        </el-menu-item>
        <el-menu-item index="/config">
          <el-icon><Setting /></el-icon>系统配置
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-layout__header">
        <span />
        <el-dropdown>
          <span class="admin-layout__user">
            <span class="admin-layout__avatar">
              <el-icon><User /></el-icon>
            </span>
            {{ userStore.user?.realName || userStore.user?.phone }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="onLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="admin-layout__main">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-layout__aside {
  background: #001529;
}
.admin-layout__logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
}
.admin-layout__logo-icon {
  font-size: 20px;
}
.admin-layout__logo-text {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
}
.admin-layout__menu {
  border-right: none;
  background: transparent;
  padding: 0 8px;
  --el-menu-text-color: #a6adb4;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-active-color: #fff;
}
.admin-layout__menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  border-radius: 8px;
  margin-bottom: 4px;
  color: #a6adb4;
}
.admin-layout__menu .el-menu-item:hover {
  color: #fff;
}
.admin-layout__menu .el-menu-item.is-active {
  background: #2f7cff;
  color: #fff;
  box-shadow: 0 4px 14px rgba(47, 124, 255, 0.3);
}
.admin-layout__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  z-index: 1;
}
.admin-layout__user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--el-text-color-regular);
}
.admin-layout__avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}
.admin-layout__main {
  background: #f5f7fb;
  padding: 20px;
}
</style>
