import { defineStore } from 'pinia'
import { getUnreadCount } from '@/api/message'

export const useMessageStore = defineStore('message', {
  state: () => ({ unread: 0 }),
  actions: {
    async fetchUnread() {
      try {
        this.unread = await getUnreadCount()
      } catch {
        // 未登录或失败时保持原值
      }
    }
  }
})
