<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminMessages, sendMessage, batchSendMessage } from '@/api/message'
import { getPlayers } from '@/api/player'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })
const TYPE_LABEL = { COURSE: '课程', EVENT: '赛事', PHYSICAL: '体能', SYSTEM: '系统' }

// ---- 消息详情 ----
const detailVisible = ref(false)
const detail = ref(null)
const openDetail = (row) => {
  detail.value = row
  detailVisible.value = true
}

const load = async () => {
  loading.value = true
  try {
    const data = await getAdminMessages(query)
    list.value = data.list
    total.value = data.total
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}
onMounted(load)

// ---- 玩家选项（推送对象）----
const players = ref([])
const loadPlayers = async () => {
  const data = await getPlayers({ pageNum: 1, pageSize: 100 })
  players.value = data.list
}

// ---- 单推 ----
const singleVisible = ref(false)
const singleForm = reactive({ userId: null, title: '', content: '', sendSms: false })
const openSingle = () => {
  loadPlayers()
  singleForm.userId = null
  singleForm.title = ''
  singleForm.content = ''
  singleForm.sendSms = false
  singleVisible.value = true
}
const submitSingle = async () => {
  if (!singleForm.userId || !singleForm.title) return ElMessage.warning('请选择用户并填写标题')
  await sendMessage({ ...singleForm })
  ElMessage.success('已推送')
  singleVisible.value = false
  load()
}

// ---- 批量推 ----
const batchVisible = ref(false)
const batchForm = reactive({ userIds: [], title: '', content: '', sendSms: false })
const openBatch = () => {
  loadPlayers()
  batchForm.userIds = []
  batchForm.title = ''
  batchForm.content = ''
  batchForm.sendSms = false
  batchVisible.value = true
}
const submitBatch = async () => {
  if (!batchForm.userIds.length || !batchForm.title) return ElMessage.warning('请选择用户并填写标题')
  await batchSendMessage({ ...batchForm })
  ElMessage.success('已批量推送')
  batchVisible.value = false
  load()
}
</script>

<template>
  <div class="messages">
    <div class="page-head">
      <h2 class="page-title">消息管理</h2>
      <p class="page-sub">站内消息列表与推送</p>
    </div>
    <div class="fs-card table-card">
      <div class="toolbar">
        <el-button type="primary" @click="openSingle">推送消息</el-button>
        <el-button @click="openBatch">批量推送</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="140" />
        <el-table-column label="内容" min-width="220">
          <template #default="{ row }">
            <div class="msg-content">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">{{ TYPE_LABEL[row.type] || row.type }}</template>
        </el-table-column>
        <el-table-column label="已读" width="70">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'" size="small">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        class="pager"
        @current-change="load"
      />
    </div>

    <!-- 单推 -->
    <el-dialog v-model="singleVisible" title="推送消息" width="480px">
      <el-form label-width="80px">
        <el-form-item label="接收用户">
          <el-select v-model="singleForm.userId" filterable style="width: 100%">
            <el-option v-for="p in players" :key="p.id" :label="`${p.realName || '未命名'} (${p.phone})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="singleForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="singleForm.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="同时短信">
          <el-switch v-model="singleForm.sendSms" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="singleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSingle">推送</el-button>
      </template>
    </el-dialog>

    <!-- 批量推 -->
    <el-dialog v-model="batchVisible" title="批量推送" width="520px">
      <el-form label-width="80px">
        <el-form-item label="接收用户">
          <el-select v-model="batchForm.userIds" multiple filterable style="width: 100%" placeholder="可多选">
            <el-option v-for="p in players" :key="p.id" :label="`${p.realName || '未命名'} (${p.phone})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="batchForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="batchForm.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="同时短信">
          <el-switch v-model="batchForm.sendSms" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatch">推送</el-button>
      </template>
    </el-dialog>

    <!-- 消息详情 -->
    <el-dialog v-model="detailVisible" title="消息详情" width="520px">
      <div v-if="detail" class="msg-detail">
        <div class="msg-detail__row"><span class="msg-detail__label">标题</span>{{ detail.title }}</div>
        <div class="msg-detail__row">
          <span class="msg-detail__label">类型</span>{{ TYPE_LABEL[detail.type] || detail.type }}
        </div>
        <div class="msg-detail__row"><span class="msg-detail__label">接收用户</span>ID: {{ detail.userId }}</div>
        <div class="msg-detail__row">
          <span class="msg-detail__label">时间</span>{{ formatDate(detail.createTime) }}
        </div>
        <div class="msg-detail__content">{{ detail.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.table-card {
  padding: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.msg-content {
  max-width: 320px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.msg-detail__row {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #1d2129;
}
.msg-detail__label {
  color: #98a1b0;
  flex-shrink: 0;
}
.msg-detail__content {
  margin-top: 12px;
  padding: 14px;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
