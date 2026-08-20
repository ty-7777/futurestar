<script setup>
import { onMounted, ref } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import { useRouter } from 'vue-router'
import { sessionList, createSession, deleteSession } from '@/api/chat'

const router = useRouter()
const ASSISTANT = 'ASSISTANT'

const sessions = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    sessions.value = await sessionList(ASSISTANT)
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}

onMounted(load)

const onNewChat = async () => {
  try {
    const { id } = await createSession({ type: ASSISTANT })
    router.push(`/assistant/${id}`)
  } catch {
    // 统一提示
  }
}

const openChat = (s) => router.push({ path: `/assistant/${s.id}`, query: { name: s.sessionName } })

const onDelete = async (s) => {
  try {
    await showConfirmDialog({ title: '提示', message: `确定删除会话「${s.sessionName}」吗？` })
  } catch {
    return
  }
  await deleteSession(s.id)
  showToast('已删除')
  load()
}
</script>

<template>
  <div class="chat-list">
    <van-nav-bar title="AI 智能客服" left-arrow @click-left="router.back()" />
    <van-loading v-if="loading" class="chat-list__loading" />
    <template v-else>
      <van-swipe-cell v-for="s in sessions" :key="s.id">
        <div class="c-item" @click="openChat(s)">
          <van-icon name="service-o" size="20" class="c-item__icon" />
          <span class="c-item__name">{{ s.sessionName }}</span>
        </div>
        <template #right>
          <van-button square type="danger" text="删除" class="c-item__delete" @click="onDelete(s)" />
        </template>
      </van-swipe-cell>
      <van-empty v-if="!sessions.length" description="还没有客服会话，点下方开始咨询" />
    </template>
    <div class="chat-list__new">
      <van-button type="primary" block round icon="plus" @click="onNewChat">咨询智能客服</van-button>
    </div>
  </div>
</template>

<style scoped>
.chat-list__loading {
  margin-top: 40vh;
}
.c-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 10px 16px;
  padding: 14px;
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
}
.c-item__icon {
  color: #16a34a;
}
.c-item__name {
  font-size: 15px;
  color: #323233;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.c-item__delete {
  height: 100%;
}
.chat-list__new {
  padding: 16px;
}
</style>
