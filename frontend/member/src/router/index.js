import { createRouter, createWebHistory } from 'vue-router'
import { getTokens } from '@/utils/auth'

const routes = [
  { path: '/login', name: 'login', component: () => import('@/views/auth/LoginView.vue'), meta: { public: true } },
  { path: '/register', name: 'register', component: () => import('@/views/auth/RegisterView.vue'), meta: { public: true } },
  {
    path: '/forgot-password',
    name: 'forgot',
    component: () => import('@/views/auth/ForgotPasswordView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/views/layout/LayoutView.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'home', component: () => import('@/views/home/HomeView.vue'), meta: { title: '首页', tab: true } },
      { path: 'course', name: 'course', component: () => import('@/views/course/CourseListView.vue'), meta: { title: '课程', tab: true } },
      { path: 'course/package/:id', name: 'course-detail', component: () => import('@/views/course/PackageDetailView.vue') },
      { path: 'course/appointments', name: 'course-appointments', component: () => import('@/views/course/MyAppointmentsView.vue') },
      { path: 'message', name: 'message', component: () => import('@/views/message/MessageListView.vue'), meta: { title: '消息', tab: true } },
      { path: 'message/:id', name: 'message-detail', component: () => import('@/views/message/MessageDetailView.vue') },
      { path: 'profile', name: 'profile', component: () => import('@/views/profile/ProfileView.vue'), meta: { title: '我的', tab: true } },
      { path: 'profile/edit', name: 'profile-edit', component: () => import('@/views/profile/EditProfileView.vue') },
      {
        path: 'profile/password',
        name: 'profile-password',
        component: () => import('@/views/profile/ChangePasswordView.vue')
      },
      // 首页宫格入口
      { path: 'physical', name: 'physical', component: () => import('@/views/physical/PhysicalListView.vue') },
      { path: 'physical/record', name: 'physical-record', component: () => import('@/views/physical/RecordFormView.vue') },
      { path: 'physical/trend', name: 'physical-trend', component: () => import('@/views/physical/TrendView.vue') },
      { path: 'chat', name: 'chat', component: () => import('@/views/chat/ChatListView.vue') },
      { path: 'chat/:id', name: 'chat-detail', component: () => import('@/views/chat/ChatView.vue') },
      { path: 'assessment', name: 'assessment', component: () => import('@/views/assessment/AssessmentListView.vue') },
      { path: 'assessment/:id', name: 'assessment-fill', component: () => import('@/views/assessment/AssessmentFillView.vue') },
      { path: 'assessment/history', name: 'assessment-history', component: () => import('@/views/assessment/AssessmentHistoryView.vue') },
      {
        path: 'assessment/result/:id',
        name: 'assessment-result',
        component: () => import('@/views/assessment/AssessmentDetailView.vue')
      },
      { path: 'event', name: 'event', component: () => import('@/views/event/EventListView.vue') },
      { path: 'event/:id', name: 'event-detail', component: () => import('@/views/event/EventDetailView.vue') },
      { path: 'event/my', name: 'event-my', component: () => import('@/views/event/MyEventsView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录守卫：公开页（login/register/forgot）放行；未登录访问其他页跳登录；已登录访问公开页跳首页
router.beforeEach((to) => {
  const token = getTokens()?.accessToken
  if (!token && !to.meta.public) return { name: 'login' }
  if (token && to.meta.public) return { path: '/' }
})

export default router
