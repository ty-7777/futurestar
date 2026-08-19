<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const entries = [
  { title: '体能记录', subtitle: '记录身体数据', icon: 'fire-o', path: '/physical', color: '#2f7cff', bg: '#eaf2ff' },
  { title: 'AI 对话', subtitle: '智能教练在线', icon: 'chat-o', path: '/chat', color: '#16b8cf', bg: '#e5f9fb' },
  { title: '技术评测', subtitle: '检验训练成果', icon: 'edit', path: '/assessment', color: '#2eb35c', bg: '#e8f8ee' },
  { title: '赛事活动', subtitle: '报名比赛训练营', icon: 'flag-o', path: '/event', color: '#f08a24', bg: '#fff3e5' }
]
</script>

<template>
  <div class="home">
    <!-- 品牌头部 -->
    <div class="home__hero">
      <div class="home__deco home__deco--1" />
      <div class="home__deco home__deco--2" />
      <div class="home__deco home__deco--3" />
      <div class="home__brand">
        <span class="home__brand-icon">⚽</span>
        <span>FUTURESTAR</span>
      </div>
      <h1 class="home__title">智星足球青训</h1>
      <div class="home__slogan">
        <span class="home__slogan-cn">追求你的梦想</span>
        <span class="home__slogan-en">pursue your dream</span>
      </div>
      <div class="home__user">
        <span class="home__name">{{ userStore.user?.realName || userStore.user?.phone }}</span>
        <span class="home__points">积分 {{ userStore.user?.points ?? 0 }}</span>
      </div>
    </div>

    <!-- 功能卡片 2×2 -->
    <div class="home__blob home__blob--1" />
    <div class="home__blob home__blob--2" />
    <div class="home__grid">
      <div v-for="e in entries" :key="e.path" class="home__cell" @click="router.push(e.path)">
        <div class="home__cell-icon" :style="{ background: e.bg, color: e.color }">
          <van-icon :name="e.icon" size="24" />
        </div>
        <div class="home__cell-info">
          <div class="home__cell-title">{{ e.title }}</div>
          <div class="home__cell-sub">{{ e.subtitle }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  padding-bottom: 96px; /* 给浮动 TabBar 让位 */
}

/* ===== 品牌头部 ===== */
.home__hero {
  position: relative;
  padding: 28px 24px 26px;
  background: var(--fs-gradient);
  color: #fff;
  overflow: hidden;
}
.home__deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  pointer-events: none;
}
.home__deco--1 {
  width: 190px;
  height: 190px;
  top: -70px;
  right: -50px;
}
.home__deco--2 {
  width: 120px;
  height: 120px;
  bottom: -50px;
  left: -36px;
  border: 22px solid rgba(255, 255, 255, 0.06);
  background: transparent;
}
.home__deco--3 {
  width: 40px;
  height: 40px;
  top: 34px;
  right: 60px;
  background: rgba(255, 255, 255, 0.12);
}
.home__brand {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 999px;
  padding: 4px 12px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 3px;
}
.home__brand-icon {
  font-size: 13px;
}
.home__title {
  margin: 16px 0 0;
  font-size: 27px;
  font-weight: 800;
  letter-spacing: 7px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}
.home__slogan {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 3px;
  border-left: 2px solid rgba(255, 255, 255, 0.55);
  padding-left: 12px;
}
.home__slogan-cn {
  font-size: 15px;
  font-family: 'Songti SC', 'STSong', 'SimSun', serif;
  letter-spacing: 4px;
  opacity: 0.97;
}
.home__slogan-en {
  font-size: 13px;
  font-family: Georgia, 'Times New Roman', serif;
  font-style: italic;
  letter-spacing: 1.5px;
  opacity: 0.82;
}
.home__user {
  margin-top: 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.16);
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
  backdrop-filter: blur(4px);
}
.home__name {
  font-weight: 600;
}
.home__points {
  font-size: 13px;
  opacity: 0.9;
}

/* ===== 背景装饰 ===== */
.home__blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(52px);
  z-index: 0;
  pointer-events: none;
}
.home__blob--1 {
  width: 220px;
  height: 220px;
  top: 320px;
  right: -60px;
  background: rgba(70, 195, 255, 0.18);
}
.home__blob--2 {
  width: 180px;
  height: 180px;
  top: 480px;
  left: -60px;
  background: rgba(25, 137, 250, 0.12);
}

/* ===== 功能卡片 2×2 ===== */
.home__grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  padding: 18px 16px;
}
.home__cell {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: var(--fs-radius);
  box-shadow: var(--fs-shadow);
  padding: 18px 14px;
  min-height: 100px;
  transition: transform 0.15s;
}
.home__cell:active {
  transform: scale(0.97);
}
.home__cell-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.home__cell-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--fs-text);
}
.home__cell-sub {
  margin-top: 5px;
  font-size: 12px;
  color: var(--fs-text-3);
}
</style>
