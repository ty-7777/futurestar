<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminQuestionnaires,
  createQuestionnaire,
  updateQuestionnaire,
  deleteQuestionnaire,
  updateQuestionnaireStatus,
  getAdminQuestions,
  createQuestion,
  updateQuestion,
  deleteQuestion
} from '@/api/assessment'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const load = async () => {
  loading.value = true
  try {
    const data = await getAdminQuestionnaires(query)
    list.value = data.list
    total.value = data.total
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}
onMounted(load)

// ---- 问卷 CRUD ----
const dialog = ref(false)
const form = reactive({ id: null, title: '', description: '', status: 'DRAFT' })
const openDialog = (row) => {
  Object.assign(form, {
    id: row?.id || null,
    title: row?.title || '',
    description: row?.description || '',
    status: row?.status || 'DRAFT'
  })
  dialog.value = true
}
const submit = async () => {
  if (!form.title) return ElMessage.warning('请输入问卷标题')
  const payload = { title: form.title, description: form.description, status: form.status }
  if (form.id) await updateQuestionnaire(form.id, payload)
  else await createQuestionnaire(payload)
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}
const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除问卷「${row.title}」吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteQuestionnaire(row.id)
  ElMessage.success('已删除')
  load()
}
const onToggleStatus = async (row) => {
  const next = row.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED'
  await updateQuestionnaireStatus(row.id, next)
  ElMessage.success(next === 'PUBLISHED' ? '已发布' : '已下架')
  load()
}

// ---- 题目管理 ----
const qsVisible = ref(false)
const currentQid = ref(null)
const qsList = ref([])
const loadQs = async () => {
  qsList.value = await getAdminQuestions(currentQid.value)
}
const openQs = async (row) => {
  currentQid.value = row.id
  await loadQs()
  qsVisible.value = true
}

const qDialog = ref(false)
const qForm = reactive({ id: null, content: '', type: 'SINGLE', options: '', sortOrder: 0 })
const openQDialog = (q) => {
  Object.assign(qForm, {
    id: q?.id || null,
    content: q?.content || '',
    type: q?.type || 'SINGLE',
    options: q?.options ? JSON.stringify(JSON.parse(q.options)) : '',
    sortOrder: q?.sortOrder ?? 0
  })
  qDialog.value = true
}
const submitQ = async () => {
  if (!qForm.content) return ElMessage.warning('请输入题目内容')
  let options = null
  if (qForm.type !== 'TEXT') {
    try {
      options = JSON.parse(qForm.options || '[]')
    } catch {
      return ElMessage.warning('选项需为 JSON 数组，如 ["A","B"]')
    }
  }
  const payload = { content: qForm.content, type: qForm.type, options, sortOrder: qForm.sortOrder }
  if (qForm.id) await updateQuestion(qForm.id, payload)
  else await createQuestion(currentQid.value, payload)
  ElMessage.success('保存成功')
  qDialog.value = false
  loadQs()
}
const onDeleteQ = async (q) => {
  try {
    await ElMessageBox.confirm('确定删除该题目吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteQuestion(q.id)
  ElMessage.success('已删除')
  loadQs()
}
</script>

<template>
  <div class="assessment">
    <div class="page-head">
      <h2 class="page-title">评测管理</h2>
      <p class="page-sub">问卷与题目的创建、发布与维护</p>
    </div>
    <div class="fs-card table-card">
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()">新增问卷</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="问卷标题" min-width="180" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openQs(row)">题目管理</el-button>
            <el-button
              link
              size="small"
              :type="row.status === 'PUBLISHED' ? 'warning' : 'success'"
              @click="onToggleStatus(row)"
            >
              {{ row.status === 'PUBLISHED' ? '下架' : '发布' }}
            </el-button>
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

    <!-- 问卷 新增/编辑 -->
    <el-dialog v-model="dialog" :title="form.id ? '编辑问卷' : '新增问卷'" width="500px">
      <el-form label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="DRAFT">草稿</el-radio>
            <el-radio value="PUBLISHED">发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 题目管理 -->
    <el-dialog v-model="qsVisible" title="题目管理" width="760px">
      <div class="toolbar">
        <el-button type="primary" @click="openQDialog()">新增题目</el-button>
      </div>
      <el-table :data="qsList" size="small" stripe>
        <el-table-column prop="sortOrder" label="序号" width="60" />
        <el-table-column prop="content" label="题目" min-width="200" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            {{ row.type === 'SINGLE' ? '单选' : row.type === 'MULTIPLE' ? '多选' : '文本' }}
          </template>
        </el-table-column>
        <el-table-column prop="options" label="选项" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="110">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openQDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="onDeleteQ(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 题目 新增/编辑 -->
    <el-dialog v-model="qDialog" :title="qForm.id ? '编辑题目' : '新增题目'" width="520px">
      <el-form label-width="80px">
        <el-form-item label="题目内容"><el-input v-model="qForm.content" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="qForm.type" style="width: 100%">
            <el-option label="单选" value="SINGLE" />
            <el-option label="多选" value="MULTIPLE" />
            <el-option label="文本" value="TEXT" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="qForm.type !== 'TEXT'" label="选项">
          <el-input v-model="qForm.options" type="textarea" :rows="2" placeholder='JSON 数组，如 ["A","B","C"]' />
        </el-form-item>
        <el-form-item label="排序号"><el-input-number v-model="qForm.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="qDialog = false">取消</el-button>
        <el-button type="primary" @click="submitQ">确定</el-button>
      </template>
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
</style>
