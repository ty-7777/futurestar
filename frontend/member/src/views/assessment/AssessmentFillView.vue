<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { getQuestions, submitAssessment } from '@/api/assessment'

const route = useRoute()
const router = useRouter()
const questionnaireId = route.params.id

const questions = ref([])
const answers = reactive({})
const submitting = ref(false)
const showResult = ref(false)
const result = ref(null)

const parseOptions = (options) => {
  if (!options) return []
  try {
    return JSON.parse(options)
  } catch {
    return []
  }
}

const QUESTION_TYPE = { SINGLE: '单选题', MULTIPLE: '多选题', TEXT: '文本题' }

onMounted(async () => {
  try {
    questions.value = await getQuestions(questionnaireId)
    for (const q of questions.value) {
      if (q.type === 'MULTIPLE') answers[q.id] = []
    }
  } catch {
    // 统一提示
  }
})

const onSubmit = async () => {
  for (const q of questions.value) {
    const v = answers[q.id]
    const empty = v === undefined || v === null || v === '' || (Array.isArray(v) && !v.length)
    if (empty) return showToast(`请完成第 ${q.sortOrder || q.id} 题`)
  }
  const items = questions.value.map((q) => ({ qid: q.id, type: q.type, value: answers[q.id] }))
  submitting.value = true
  try {
    result.value = await submitAssessment({
      questionnaireId,
      answers: JSON.stringify({ version: 1, items })
    })
    showToast('评测完成，+20 积分')
    showResult.value = true
  } catch {
    // 统一提示
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="fill">
    <van-nav-bar title="开始评测" left-arrow @click-left="router.back()" />
    <div class="fill__body">
      <div v-for="(q, idx) in questions" :key="q.id" class="fq">
        <div class="fq__head">
          <span class="fq__no">{{ idx + 1 }}.</span>
          <span class="fq__content">{{ q.content }}</span>
          <van-tag plain type="primary">{{ QUESTION_TYPE[q.type] }}</van-tag>
        </div>

        <template v-if="q.type === 'SINGLE'">
          <van-radio-group v-model="answers[q.id]" class="fq__options">
            <van-radio v-for="(opt, i) in parseOptions(q.options)" :key="i" :name="i">
              {{ opt }}
            </van-radio>
          </van-radio-group>
        </template>

        <template v-else-if="q.type === 'MULTIPLE'">
          <van-checkbox-group v-model="answers[q.id]" class="fq__options">
            <van-checkbox v-for="(opt, i) in parseOptions(q.options)" :key="i" :name="i">
              {{ opt }}
            </van-checkbox>
          </van-checkbox-group>
        </template>

        <van-field
          v-else
          v-model="answers[q.id]"
          type="textarea"
          rows="3"
          placeholder="请输入你的回答"
          class="fq__text"
        />
      </div>
    </div>
    <div class="fill__submit">
      <van-button type="primary" block round :loading="submitting" @click="onSubmit">
        提交评测
      </van-button>
    </div>

    <van-popup v-model:show="showResult" round>
      <div class="fill__result">
        <div class="fill__score">AI 评分：{{ result?.aiScore }} 分</div>
        <div class="fill__suggestion">{{ result?.aiSuggestion }}</div>
        <van-button type="primary" block round @click="router.back()">完成</van-button>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
.fill__body {
  padding: 8px 16px 80px;
}
.fq {
  background: #fff;
  border-radius: var(--fs-radius); box-shadow: var(--fs-shadow);
  padding: 14px;
  margin-bottom: 12px;
}
.fq__head {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}
.fq__no {
  font-weight: 600;
  color: #16a34a;
}
.fq__content {
  font-size: 14px;
  font-weight: 600;
  flex: 1;
}
.fq__options {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.fq__text {
  margin-top: 8px;
  padding: 0;
  background: #f7f8fa;
  border-radius: 8px;
}
.fill__submit {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 16px;
  background: #fff;
}
.fill__result {
  width: 280px;
  padding: 24px;
  text-align: center;
}
.fill__score {
  font-size: 18px;
  font-weight: 600;
  color: #15803d;
}
.fill__suggestion {
  margin: 14px 0 20px;
  font-size: 14px;
  color: #323233;
  text-align: left;
  line-height: 1.7;
  max-height: 40vh;
  overflow-y: auto;
  white-space: pre-wrap;
}
</style>
