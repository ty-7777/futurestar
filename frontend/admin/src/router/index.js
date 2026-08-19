import { createRouter, createWebHistory } from 'vue-router'
import { getTokens, getUser } from '@/utils/auth'

const routes = [
  { path: '/login', name: 'login', component: () => import('@/views/login/LoginView.vue') },
  {
    path: '/',
    component: () => import('@/views/layout/LayoutView.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
      { path: 'players', name: 'players', component: () => import('@/views/players/PlayersView.vue') },
      { path: 'course', name: 'course', component: () => import('@/views/course/CourseView.vue') },
      { path: 'assessment', name: 'assessment', component: () => import('@/views/assessment/AssessmentView.vue') },
      { path: 'event', name: 'event', component: () => import('@/views/event/EventView.vue') },
      { path: 'message', name: 'message', component: () => import('@/views/message/MessageView.vue') },
      { path: 'config', name: 'config', component: () => import('@/views/config/ConfigView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录守卫 + ADMIN 角色守卫
router.beforeEach((to) => {
  const token = getTokens()?.accessToken
  const role = getUser()?.role
  if (!token && to.name !== 'login') return { name: 'login' }
  if (token && to.name === 'login') return { path: '/' }
  // 非管理员账号拦截（后端亦会校验，前端提前处理更友好）
  if (token && role !== 'ADMIN') return { name: 'login' }
})

export default router
