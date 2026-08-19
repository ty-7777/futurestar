<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useMessageStore } from '@/stores/message'

const route = useRoute()
const messageStore = useMessageStore()

onMounted(() => {
  messageStore.fetchUnread()
})
</script>

<template>
  <div class="member-layout">
    <div class="member-layout__body">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
    <!-- 仅 4 个主 Tab 页显示底部导航，子页面全屏 -->
    <van-tabbar v-if="route.meta.tab" route fixed placeholder class="member-tabbar">
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/course">课程</van-tabbar-item>
      <van-tabbar-item icon="chat-o" to="/message" :badge="messageStore.unread || undefined">
        消息
      </van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<style scoped>
.member-layout__body {
  min-height: 100vh;
}

/* 浮动圆角 TabBar */
:deep(.van-tabbar) {
  left: 12px;
  right: 12px;
  bottom: 10px;
  width: auto;
  height: 52px;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(25, 137, 250, 0.15);
}
</style>
