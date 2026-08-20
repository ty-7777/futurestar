<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const user = userStore.user

// 会员等级映射
const LEVEL_MAP = {
  NORMAL: { label: '普通球员', color: '#4b5563' },
  SILVER: { label: '白银会员', color: '#9ca3af' },
  GOLD: { label: '黄金会员', color: '#15803d' },
  PLATINUM: { label: '铂金会员', color: '#22c55e' },
  DIAMOND: { label: '钻石会员', color: '#0b5d2e' }
}
const levelInfo = LEVEL_MAP[user?.memberLevel] || LEVEL_MAP.NORMAL

// 功能宫格（2×2 占满，绿色系深浅区分）
const entries = [
  { title: '体能记录', subtitle: '记录身体数据', icon: 'fire-o', path: '/physical', color: '#15803d', bg: 'linear-gradient(135deg,#e6f6ec,#d2efdd)' },
  { title: 'AI 对话', subtitle: '智能教练在线', icon: 'chat-o', path: '/chat', color: '#16a34a', bg: 'linear-gradient(135deg,#eafaf0,#dcf5e6)' },
  { title: '技术评测', subtitle: '检验训练成果', icon: 'edit', path: '/assessment', color: '#0b5d2e', bg: 'linear-gradient(135deg,#e9f8ef,#d9f2e2)' },
  { title: '赛事活动', subtitle: '报名比赛训练营', icon: 'flag-o', path: '/event', color: '#34c96e', bg: 'linear-gradient(135deg,#effbf3,#e4f8ec)' }
]

// ================= 搜索跳转（结果在新页面展示） =================
const COURSE = 'COURSE'
const EVENT = 'EVENT'
const activeTab = ref(COURSE)
const keyword = ref('')

const goSearch = () => {
  if (!keyword.value.trim()) return
  router.push({ path: '/search', query: { type: activeTab.value, keyword: keyword.value.trim() } })
}
</script>

<template>
  <div class="home">
    <!-- 品牌 hero：动态渐变 + 漂浮光斑 + 旋转足球 + 波浪 -->
    <div class="home__hero">
      <div class="home__halo" />
      <div class="home__dot" />
      <div class="home__orb home__orb--1" />
      <div class="home__orb home__orb--2" />
      <div class="home__ball">⚽</div>
      <svg class="home__wave" viewBox="0 0 375 44" preserveAspectRatio="none">
        <path d="M0,26 C50,40 96,10 150,18 C204,26 250,8 300,14 C332,18 356,16 375,12 L375,44 L0,44 Z" fill="rgba(255,255,255,0.10)" />
        <path d="M0,34 C60,44 120,22 190,30 C260,38 320,24 375,28 L375,44 L0,44 Z" fill="rgba(255,255,255,0.12)" />
      </svg>

      <div class="home__brand-row">
        <span class="home__brand">
          <span class="home__brand-icon">⚽</span>
          <span>FUTURESTAR</span>
        </span>
        <span class="home__level" :style="{ background: levelInfo.color }">{{ levelInfo.label }}</span>
      </div>

      <!-- 毛玻璃用户卡 -->
      <div class="home__user">
        <div class="home__avatar">
          <img v-if="user?.avatar" :src="user.avatar" alt="头像" />
          <span v-else>{{ (user?.realName || user?.phone || '球').charAt(0) }}</span>
        </div>
        <div class="home__user-info">
          <div class="home__name">{{ user?.realName || user?.phone }}</div>
          <div class="home__slogan">追求你的梦想 · pursue your dream</div>
        </div>
        <div class="home__points">
          <div class="home__points-num">{{ user?.points ?? 0 }}</div>
          <div class="home__points-label">我的积分</div>
        </div>
      </div>
    </div>

    <!-- 分类搜索栏（毛玻璃悬浮）：回车/点搜索跳转结果页 -->
    <div class="home__search">
      <div class="home__search-tabs">
        <span
          v-for="t in [{ key: COURSE, label: '课程' }, { key: EVENT, label: '赛事' }]"
          :key="t.key"
          class="home__search-tab"
          :class="{ active: activeTab === t.key }"
          @click="activeTab = t.key"
        >{{ t.label }}</span>
      </div>
      <div class="home__search-input">
        <van-icon name="search" size="16" />
        <input v-model="keyword" type="search" placeholder="搜索课程名称 / 教练，或赛事标题" @keyup.enter="goSearch" />
        <span class="home__search-btn" @click="goSearch">搜索</span>
      </div>
    </div>

    <!-- 功能宫格 2×2 占满 -->
    <div class="home__grid">
      <div v-for="e in entries" :key="e.path" class="home__cell" @click="router.push(e.path)">
        <div class="home__cell-icon" :style="{ background: e.bg, color: e.color }">
          <van-icon :name="e.icon" size="30" />
        </div>
        <div class="home__cell-title">{{ e.title }}</div>
        <div class="home__cell-sub">{{ e.subtitle }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
  padding-bottom: 96px; /* 给浮动 TabBar 让位 */
}

/* ===== 品牌 hero：球场绿动态渐变 ===== */
.home__hero {
  position: relative;
  padding: 22px 20px 56px;
  background: linear-gradient(120deg, #0b5d2e 0%, #1a9e4b 40%, #34c96e 75%, #1a9e4b 100%);
  background-size: 300% 300%;
  animation: heroFlow 14s ease infinite;
  color: #fff;
  overflow: hidden;
}
@keyframes heroFlow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 右上大光环：呼吸缩放 */
.home__halo {
  position: absolute;
  width: 240px;
  height: 240px;
  top: -96px;
  right: -70px;
  border: 34px solid rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  pointer-events: none;
  animation: haloBreath 6s ease-in-out infinite alternate;
}
@keyframes haloBreath {
  from {
    transform: scale(1);
    opacity: 0.7;
  }
  to {
    transform: scale(1.12);
    opacity: 1;
  }
}

/* 左上发光小圆点 */
.home__dot {
  position: absolute;
  width: 14px;
  height: 14px;
  top: 30px;
  left: 26px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.28);
  box-shadow: 0 0 12px rgba(255, 255, 255, 0.5);
  pointer-events: none;
  animation: dotTwinkle 3.2s ease-in-out infinite alternate;
}
@keyframes dotTwinkle {
  from {
    opacity: 0.4;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1.2);
  }
}

/* 漂浮光斑 */
.home__orb {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  pointer-events: none;
}
.home__orb--1 {
  width: 64px;
  height: 64px;
  top: 88px;
  left: -16px;
  animation: orbFloat 7s ease-in-out infinite alternate;
}
.home__orb--2 {
  width: 36px;
  height: 36px;
  top: 44px;
  left: 55%;
  animation: orbFloat 9s ease-in-out infinite alternate-reverse;
}
@keyframes orbFloat {
  from {
    transform: translateY(0);
    opacity: 0.5;
  }
  to {
    transform: translateY(-22px);
    opacity: 1;
  }
}

/* 右下旋转足球剪影 */
.home__ball {
  position: absolute;
  right: 18px;
  bottom: 30px;
  font-size: 96px;
  line-height: 1;
  opacity: 0.16;
  transform: rotate(-18deg);
  animation: homeBallSpin 22s linear infinite;
  pointer-events: none;
}
@keyframes homeBallSpin {
  from {
    transform: rotate(-18deg);
  }
  to {
    transform: rotate(342deg);
  }
}

/* 底部波浪：横向漂移 */
.home__wave {
  position: absolute;
  left: -5%;
  bottom: 0;
  width: 110%;
  height: 44px;
  pointer-events: none;
  animation: waveDrift 8s ease-in-out infinite alternate;
}
@keyframes waveDrift {
  from {
    transform: translateX(-6px);
  }
  to {
    transform: translateX(6px);
  }
}

.home__brand-row {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  backdrop-filter: blur(4px);
}
.home__brand-icon {
  font-size: 13px;
}

/* 等级徽章 */
.home__level {
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  border-radius: 999px;
  padding: 3px 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
}

/* 毛玻璃用户卡 */
.home__user {
  position: relative;
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: var(--fs-radius-lg);
  padding: 14px;
  backdrop-filter: blur(8px);
}
.home__avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0b5d2e, #34c96e);
  border: 2px solid rgba(255, 255, 255, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.home__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.home__user-info {
  flex: 1;
  min-width: 0;
}
.home__name {
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 1px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.home__slogan {
  margin-top: 3px;
  font-size: 11px;
  opacity: 0.82;
  letter-spacing: 0.5px;
}

/* 积分块 */
.home__points {
  text-align: right;
  flex-shrink: 0;
  padding-right: 4px;
}
.home__points-num {
  font-size: 20px;
  font-weight: 800;
  line-height: 1.2;
}
.home__points-label {
  font-size: 10px;
  opacity: 0.85;
  letter-spacing: 1px;
}

/* ===== 分类搜索栏（毛玻璃悬浮） ===== */
.home__search {
  position: relative;
  z-index: 2;
  margin: -34px 16px 0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: var(--fs-radius-lg);
  box-shadow: var(--fs-shadow-lg);
  padding: 10px 14px 12px;
}
.home__search-tabs {
  display: flex;
  gap: 24px;
  padding-bottom: 8px;
}
.home__search-tab {
  position: relative;
  font-size: 14px;
  font-weight: 600;
  color: var(--fs-text-2);
  cursor: pointer;
  transition: color 0.18s ease;
}
.home__search-tab.active {
  color: var(--fs-primary);
}
.home__search-tab.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: -8px;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  border-radius: 2px;
  background: var(--fs-gradient);
}
.home__search-input {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f2f8f4;
  border-radius: 999px;
  padding: 6px 6px 6px 12px;
  color: var(--fs-text-3);
}
.home__search-input input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: var(--fs-text-1);
}
.home__search-input input::placeholder {
  color: var(--fs-text-3);
}
.home__search-btn {
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: var(--fs-gradient);
  border-radius: 999px;
  padding: 6px 14px;
  cursor: pointer;
  transition: opacity 0.18s ease;
}
.home__search-btn:active {
  opacity: 0.85;
}

/* ===== 功能宫格 2×2 占满 ===== */
.home__grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  padding: 18px 16px 6px;
}
.home__cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #fff;
  border-radius: var(--fs-radius-lg);
  box-shadow: var(--fs-shadow);
  padding: 22px 12px;
  min-height: 138px;
  transition: transform 0.15s ease;
}
.home__cell:active {
  transform: scale(0.97);
}
.home__cell-icon {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.home__cell-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--fs-text-1);
}
.home__cell-sub {
  font-size: 12px;
  color: var(--fs-text-3);
}
</style>
