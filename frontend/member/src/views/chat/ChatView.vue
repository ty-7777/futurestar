<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { getMessages } from '@/api/chat'
import { streamSSE } from '@/utils/sse'

const route = useRoute()
const router = useRouter()

const sessionId = route.params.id
const messages = ref([])
const input = ref('')
const sending = ref(false)
const streamText = ref('')
const scrollEl = ref(null)
let abortController = null

const scrollToBottom = () => {
  nextTick(() => {
    if (scrollEl.value) scrollEl.value.scrollTop = scrollEl.value.scrollHeight
  })
}

onMounted(async () => {
  try {
    messages.value = await getMessages(sessionId)
    scrollToBottom()
  } catch {
    // 统一提示
  }
})

onBeforeUnmount(() => {
  // 离开页面时断开流，后端 doOnCancel 会保存已接收片段
  abortController && abortController.abort()
})

const send = async () => {
  const content = input.value.trim()
  if (!content || sending.value) return
  input.value = ''
  messages.value.push({ role: 'user', message: content })
  sending.value = true
  streamText.value = ''
  abortController = new AbortController()

  let completed = false
  try {
    await streamSSE(
      `/api/member/chat/session/${sessionId}/stream`,
      { content },
      {
        onMessage: (chunk) => {
          if (chunk === '[DONE]') return
          streamText.value += chunk
          scrollToBottom()
        },
        signal: abortController.signal
      }
    )
    completed = true
  } catch {
    // 网络错误 / 后端异常
  } finally {
    if (streamText.value) {
      // 完整回复或断线片段都展示出来
      messages.value.push({ role: 'assistant', message: streamText.value })
    } else if (!completed) {
      showToast('发送失败，请重试')
    }
    streamText.value = ''
    sending.value = false
    scrollToBottom()
  }
}
</script>

<template>
  <div class="chat">
    <van-nav-bar :title="route.query.name || 'AI 对话'" left-arrow @click-left="router.back()" />
    <div ref="scrollEl" class="chat__body">
      <van-empty v-if="!messages.length && !streamText" description="问点什么吧，训练、战术、营养都能聊" />
      <div
        v-for="(m, i) in messages"
        :key="i"
        class="msg"
        :class="m.role === 'user' ? 'msg--right' : 'msg--left'"
      >
        <div class="msg__bubble">{{ m.message }}</div>
      </div>
      <div v-if="streamText" class="msg msg--left">
        <div class="msg__bubble">
          {{ streamText }}<span class="msg__cursor" />
        </div>
      </div>
    </div>
    <div class="chat__input">
      <van-field v-model="input" placeholder="输入你的问题…" @keyup.enter="send" />
      <van-button type="primary" :loading="sending" @click="send">发送</van-button>
    </div>
  </div>
</template>

<style scoped>
.chat {
  height: 100vh;
  height: 100dvh;
  display: flex;
  flex-direction: column;
}
.chat__body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
}
.msg {
  display: flex;
  margin-bottom: 12px;
}
.msg--right {
  justify-content: flex-end;
}
.msg--left {
  justify-content: flex-start;
}
.msg__bubble {
  max-width: 78%;
  padding: 10px 12px;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}
.msg--right .msg__bubble {
  background: #2f7cff;
  color: #fff;
  border-top-right-radius: 2px;
}
.msg--left .msg__bubble {
  background: #fff;
  color: #323233;
  border-top-left-radius: 2px;
}
.msg__cursor {
  display: inline-block;
  width: 2px;
  height: 14px;
  background: #2f7cff;
  vertical-align: -2px;
  animation: blink 0.8s infinite;
}
@keyframes blink {
  50% {
    opacity: 0;
  }
}
.chat__input {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fff;
  border-top: 1px solid #eee;
}
.chat__input .van-field {
  flex: 1;
  padding: 0;
}
</style>
