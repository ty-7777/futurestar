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
    <!-- 主 Tab 页显示底部导航，子页面全屏；中间为凸起的 AI 智能客服大按钮 -->
    <van-tabbar v-if="route.meta.tab" route fixed placeholder class="member-tabbar">
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/course">课程</van-tabbar-item>
      <van-tabbar-item class="tabbar-assistant" to="/assistant" :icon="null">
        <template #icon>
          <span class="tabbar-assistant__btn">
            <van-icon name="service-o" size="22" />
            <span class="tabbar-assistant__ai">AI</span>
          </span>
        </template>
        智能客服
      </van-tabbar-item>
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
  box-shadow: 0 4px 20px rgba(26, 158, 75, 0.18);
  overflow: visible; /* 允许中间大按钮向上凸出 */
}

/* 中间 AI 智能客服：凸起圆形大按钮（视觉焦点，亮绿色） */
:deep(.tabbar-assistant) {
  position: relative;
}
:deep(.tabbar-assistant .van-tabbar-item__icon) {
  margin-top: -38px;
  margin-bottom: 2px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #34c96e 0%, #16a34a 100%);
  box-shadow:
    0 6px 18px rgba(22, 163, 74, 0.5),
    0 0 0 4px rgba(255, 255, 255, 0.9),
    0 0 24px rgba(34, 197, 94, 0.4);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease;
}
/* AI 徽标：白底深绿字，叠在耳机右上角（大号醒目） */
:deep(.tabbar-assistant__ai) {
  position: absolute;
  top: -5px;
  right: -8px;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.5px;
  color: #0b5d2e;
  background: #fff;
  border-radius: 9px;
  padding: 2px 6px;
  line-height: 1.3;
  box-shadow: 0 2px 6px rgba(11, 93, 46, 0.35);
}
:deep(.tabbar-assistant .van-tabbar-item__text) {
  font-size: 10px;
  color: var(--fs-text-2);
}
:deep(.tabbar-assistant--active .van-tabbar-item__icon) {
  transform: scale(1.06);
  box-shadow:
    0 8px 22px rgba(22, 163, 74, 0.6),
    0 0 0 4px rgba(255, 255, 255, 0.9),
    0 0 32px rgba(34, 197, 94, 0.55);
}
:deep(.tabbar-assistant--active .van-tabbar-item__text) {
  color: #16a34a;
  font-weight: 600;
}
</style>
