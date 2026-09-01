<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminEvents, createEvent, updateEvent, deleteEvent, getRegistrations } from '@/api/event'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })

const TYPE_LABEL = { MATCH: '比赛', CAMP: '训练营', SELECTION: '选拔' }
const STATUS_LABEL = { DRAFT: '草稿', REGISTRATING: '报名中', IN_PROGRESS: '进行中', ENDED: '已结束' }

const load = async () => {
  loading.value = true
  try {
    const data = await getAdminEvents(query)
    list.value = data.list
    total.value = data.total
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}
onMounted(load)

// ---- 新增/编辑 ----
const dialog = ref(false)
const form = reactive({
  id: null,
  title: '',
  type: 'MATCH',
  coverUrl: '',
  content: '',
  registrationStart: '',
  registrationEnd: '',
  activityStart: '',
  activityEnd: '',
  maxParticipants: 50,
  status: 'DRAFT'
})
const openDialog = (row) => {
  Object.assign(form, {
    id: row?.id || null,
    title: row?.title || '',
    type: row?.type || 'MATCH',
    coverUrl: row?.coverUrl || '',
    content: row?.content || '',
    registrationStart: row?.registrationStart || '',
    registrationEnd: row?.registrationEnd || '',
    activityStart: row?.activityStart || '',
    activityEnd: row?.activityEnd || '',
    maxParticipants: row?.maxParticipants ?? 50,
    status: row?.status || 'DRAFT'
  })
  dialog.value = true
}
const submit = async () => {
  if (!form.title) return ElMessage.warning('请输入活动标题')
  const payload = { ...form }
  delete payload.id
  if (form.id) await updateEvent(form.id, payload)
  else await createEvent(payload)
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}
const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除活动「${row.title}」吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteEvent(row.id)
  ElMessage.success('已删除')
  load()
}

// ---- 报名列表 ----
const regVisible = ref(false)
const regEventId = ref(null)
const regList = ref([])
const regTotal = ref(0)
const regQuery = reactive({ pageNum: 1, pageSize: 10 })

const loadRegs = async () => {
  if (!regEventId.value) return
  const data = await getRegistrations(regEventId.value, regQuery)
  regList.value = data.list
  regTotal.value = data.total
}
const openRegs = async (row) => {
  regEventId.value = row.id
  regQuery.pageNum = 1
  await loadRegs()
  regVisible.value = true
}
</script>

<template>
  <div class="events">
    <div class="page-head">
      <h2 class="page-title">赛事管理</h2>
      <p class="page-sub">活动发布、报名管理与签到查看</p>
    </div>
    <div class="fs-card table-card">
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 150px" @change="load">
          <el-option v-for="(label, v) in STATUS_LABEL" :key="v" :label="label" :value="v" />
        </el-select>
        <el-button type="primary" @click="openDialog()">新增活动</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="活动标题" min-width="180" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">{{ TYPE_LABEL[row.type] || row.type }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'REGISTRATING' ? 'success' : row.status === 'IN_PROGRESS' ? 'warning' : 'info'">
              {{ STATUS_LABEL[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="人数" width="90">
          <template #default="{ row }">{{ row.currentParticipants }}/{{ row.maxParticipants }}</template>
        </el-table-column>
        <el-table-column label="活动时间" width="210">
          <template #default="{ row }">{{ formatDate(row.activityStart) }} ~ {{ formatDate(row.activityEnd) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="190">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openRegs(row)">报名列表</el-button>
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="onDelete(row)">删除</el-button>
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

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialog" :title="form.id ? '编辑活动' : '新增活动'" width="600px">
      <el-form label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option v-for="(label, v) in TYPE_LABEL" :key="v" :label="label" :value="v" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.coverUrl" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="报名开始">
          <el-date-picker v-model="form.registrationStart" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报名截止">
          <el-date-picker v-model="form.registrationEnd" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动开始">
          <el-date-picker v-model="form.activityStart" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动结束">
          <el-date-picker v-model="form.activityEnd" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="人数上限"><el-input-number v-model="form.maxParticipants" :min="1" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="(label, v) in STATUS_LABEL" :key="v" :label="label" :value="v" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报名列表 -->
    <el-dialog v-model="regVisible" title="活动报名列表" width="680px">
      <el-table :data="regList" size="small" stripe>
        <el-table-column prop="playerName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="位置" width="90">
          <template #default="{ row }">{{ row.position || '-' }}</template>
        </el-table-column>
        <el-table-column prop="memberLevel" label="等级" width="100" />
        <el-table-column label="签到状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.checkInStatus === 'CHECKED_IN' ? 'success' : 'info'">
              {{ row.checkInStatus === 'CHECKED_IN' ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报名时间" width="150">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="regQuery.pageNum"
        v-model:page-size="regQuery.pageSize"
        :total="regTotal"
        layout="total, prev, pager, next"
        class="pager"
        @current-change="loadRegs"
      />
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}
.table-card {
  padding: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
