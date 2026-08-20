<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminPackages,
  createPackage,
  updatePackage,
  deletePackage,
  batchCreateSlots,
  getAdminSlots,
  updateSlot,
  getAdminAppointments,
  confirmAppointment,
  rejectAppointment,
  uploadReport
} from '@/api/course'
import { formatDate } from '@/utils/format'

const activeTab = ref('packages')

// ================= 套餐管理 =================
const pkgLoading = ref(false)
const pkgList = ref([])
const pkgTotal = ref(0)
const pkgQuery = reactive({ pageNum: 1, pageSize: 10 })

const loadPackages = async () => {
  pkgLoading.value = true
  try {
    const data = await getAdminPackages(pkgQuery)
    pkgList.value = data.list
    pkgTotal.value = data.total
  } catch {
    // 统一提示
  } finally {
    pkgLoading.value = false
  }
}
onMounted(loadPackages)

const pkgDialog = ref(false)
const pkgForm = reactive({
  id: null,
  name: '',
  coverUrl: '',
  description: '',
  price: 0,
  coachName: '',
  suitableLevel: '',
  items: '',
  status: 'ENABLED'
})
const openPkgDialog = (row) => {
  Object.assign(pkgForm, {
    id: row?.id || null,
    name: row?.name || '',
    coverUrl: row?.coverUrl || '',
    description: row?.description || '',
    price: row?.price ?? 0,
    coachName: row?.coachName || '',
    suitableLevel: row?.suitableLevel || '',
    items: row?.items || '',
    status: row?.status || 'ENABLED'
  })
  pkgDialog.value = true
}
const submitPkg = async () => {
  if (!pkgForm.name) return ElMessage.warning('请输入课程名称')
  const payload = { ...pkgForm }
  delete payload.id
  if (pkgForm.id) await updatePackage(pkgForm.id, payload)
  else await createPackage(payload)
  ElMessage.success('保存成功')
  pkgDialog.value = false
  loadPackages()
}
const onDeletePkg = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除套餐「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await deletePackage(row.id)
  ElMessage.success('已删除')
  loadPackages()
}

// ================= 时段管理 =================
const slotPkgId = ref(null)
const slotPkgName = ref('')
const slotDate = ref('')
const slotList = ref([])
const slotLoading = ref(false)

const loadSlots = async () => {
  if (!slotPkgId.value) {
    slotList.value = []
    return
  }
  slotLoading.value = true
  try {
    slotList.value = await getAdminSlots({ packageId: slotPkgId.value, date: slotDate.value || undefined })
  } catch {
    // 统一提示
  } finally {
    slotLoading.value = false
  }
}
const onSlotPkgChange = (row) => {
  slotPkgName.value = row.name
  loadSlots()
}

const batchDialog = ref(false)
const batchForm = reactive({ startDate: '', endDate: '', timeRange: '', maxCount: 20 })
const openBatch = () => {
  batchForm.startDate = ''
  batchForm.endDate = ''
  batchForm.timeRange = ''
  batchForm.maxCount = 20
  batchDialog.value = true
}
const submitBatch = async () => {
  if (!batchForm.startDate || !batchForm.endDate || !batchForm.timeRange) {
    return ElMessage.warning('请填写完整的开始/结束日期与时间段')
  }
  await batchCreateSlots(slotPkgId.value, { ...batchForm })
  ElMessage.success('时段已批量生成')
  batchDialog.value = false
  loadSlots()
}

const slotEditDialog = ref(false)
const slotForm = reactive({ id: null, maxCount: 20, status: 'AVAILABLE' })
const openSlotEdit = (row) => {
  Object.assign(slotForm, { id: row.id, maxCount: row.maxCount, status: row.status })
  slotEditDialog.value = true
}
const submitSlot = async () => {
  await updateSlot(slotForm.id, { maxCount: slotForm.maxCount, status: slotForm.status })
  ElMessage.success('已更新')
  slotEditDialog.value = false
  loadSlots()
}

// ================= 预约管理 =================
const apptLoading = ref(false)
const apptList = ref([])
const apptTotal = ref(0)
const apptQuery = reactive({ pageNum: 1, pageSize: 10 })

const loadAppointments = async () => {
  apptLoading.value = true
  try {
    const data = await getAdminAppointments(apptQuery)
    apptList.value = data.list
    apptTotal.value = data.total
  } catch {
    // 统一提示
  } finally {
    apptLoading.value = false
  }
}
const APPOINT_STATUS = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELED: '已取消' }

// 切换到对应 Tab 时才加载（预约管理列表默认不预加载）
const onTabChange = (name) => {
  if (name === 'appointments') loadAppointments()
}

const onUpload = async ({ file }, row) => {
  await uploadReport(row.id, file)
  ElMessage.success('报告已上传')
  loadAppointments()
}

//确认预约（仅待确认状态显示按钮）
const onConfirm = async (row) => {
  try {
    await ElMessageBox.confirm(`确定确认「${row.packageName} ${row.courseDate} ${row.timeRange}」的预约吗？`, '确认预约', { type: 'warning' })
  } catch {
    return
  }
  await confirmAppointment(row.id)
  ElMessage.success('已确认')
  loadAppointments()
}

//拒绝预约（退还学员积分并释放名额）
const onReject = async (row) => {
  try {
    await ElMessageBox.confirm(`拒绝后将退还学员积分并释放名额，确定拒绝吗？`, '拒绝预约', { type: 'warning' })
  } catch {
    return
  }
  await rejectAppointment(row.id)
  ElMessage.success('已拒绝')
  loadAppointments()
}
</script>

<template>
  <div class="course">
    <div class="page-head">
      <h2 class="page-title">课程管理</h2>
      <p class="page-sub">维护课程套餐、预约时段与训练报告</p>
    </div>
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 套餐管理 -->
      <el-tab-pane label="套餐管理" name="packages">
        <div class="fs-card table-card">
          <div class="toolbar">
            <el-button type="primary" @click="openPkgDialog()">新增套餐</el-button>
          </div>
          <el-table :data="pkgList" v-loading="pkgLoading" stripe>
            <el-table-column prop="name" label="课程名称" width="160" />
            <el-table-column prop="coachName" label="教练" width="100" />
            <el-table-column prop="price" label="价格(积分)" width="100" />
            <el-table-column prop="suitableLevel" label="适合水平" width="100" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ENABLED' ? 'success' : 'info'">
                  {{ row.status === 'ENABLED' ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button size="small" @click="openPkgDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" plain @click="onDeletePkg(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="pkgQuery.pageNum"
            v-model:page-size="pkgQuery.pageSize"
            :total="pkgTotal"
            layout="total, prev, pager, next"
            class="pager"
            @current-change="loadPackages"
          />
        </div>
      </el-tab-pane>

      <!-- 时段管理 -->
      <el-tab-pane label="时段管理" name="slots">
        <div class="fs-card table-card">
          <div class="toolbar">
            <el-select
              v-model="slotPkgId"
              placeholder="选择套餐"
              filterable
              style="width: 200px"
              @change="onSlotPkgChange(pkgList.find((p) => p.id === slotPkgId))"
            >
              <el-option v-for="p in pkgList" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
            <el-date-picker v-model="slotDate" type="date" placeholder="按日期筛选" style="width: 150px" @change="loadSlots" />
            <el-button type="primary" :disabled="!slotPkgId" @click="openBatch">批量生成时段</el-button>
          </div>
          <el-table :data="slotList" v-loading="slotLoading" stripe>
            <el-table-column prop="courseDate" label="日期" width="130" />
            <el-table-column prop="timeRange" label="时间段" width="140" />
            <el-table-column prop="maxCount" label="最大人数" width="90" />
            <el-table-column prop="currentCount" label="已约" width="90" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'AVAILABLE' ? 'success' : row.status === 'FULL' ? 'danger' : 'info'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" @click="openSlotEdit(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 预约管理 -->
      <el-tab-pane label="预约管理" name="appointments">
        <div class="fs-card table-card">
          <el-table :data="apptList" v-loading="apptLoading" stripe>
            <el-table-column prop="packageName" label="套餐" width="150" />
            <el-table-column prop="courseDate" label="日期" width="110" />
            <el-table-column prop="timeRange" label="时段" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">{{ APPOINT_STATUS[row.status] || row.status }}</template>
            </el-table-column>
            <el-table-column label="创建时间" width="150">
              <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="报告" width="200">
              <template #default="{ row }">
                <el-upload
                  :show-file-list="false"
                  accept="application/pdf"
                  :http-request="(opt) => onUpload(opt, row)"
                >
                  <el-button size="small" type="primary" plain>{{ row.reportUrl ? '重新上传' : '上传报告' }}</el-button>
                </el-upload>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <template v-if="row.status === 'PENDING'">
                  <el-button size="small" type="success" @click="onConfirm(row)">确认</el-button>
                  <el-button size="small" type="danger" plain @click="onReject(row)">拒绝</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="apptQuery.pageNum"
            v-model:page-size="apptQuery.pageSize"
            :total="apptTotal"
            layout="total, prev, pager, next"
            class="pager"
            @current-change="loadAppointments"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 套餐 新增/编辑 -->
    <el-dialog v-model="pkgDialog" :title="pkgForm.id ? '编辑套餐' : '新增套餐'" width="560px">
      <el-form label-width="90px">
        <el-form-item label="课程名称"><el-input v-model="pkgForm.name" /></el-form-item>
        <el-form-item label="教练"><el-input v-model="pkgForm.coachName" /></el-form-item>
        <el-form-item label="价格(积分)"><el-input-number v-model="pkgForm.price" :min="0" /></el-form-item>
        <el-form-item label="适合水平"><el-input v-model="pkgForm.suitableLevel" placeholder="如：初级" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="pkgForm.coverUrl" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="pkgForm.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="训练项目">
          <el-input v-model="pkgForm.items" type="textarea" :rows="2" placeholder='JSON 数组，如 ["传接球","射门"]' />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="pkgForm.status">
            <el-radio value="ENABLED">启用</el-radio>
            <el-radio value="DISABLED">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pkgDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPkg">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量生成时段 -->
    <el-dialog v-model="batchDialog" title="批量生成时段" width="460px">
      <el-form label-width="90px">
        <el-form-item label="开始日期"><el-date-picker v-model="batchForm.startDate" type="date" style="width: 100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="batchForm.endDate" type="date" style="width: 100%" /></el-form-item>
        <el-form-item label="时间段"><el-input v-model="batchForm.timeRange" placeholder="如 18:00-19:30" /></el-form-item>
        <el-form-item label="最大人数"><el-input-number v-model="batchForm.maxCount" :min="1" :max="100" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialog = false">取消</el-button>
        <el-button type="primary" @click="submitBatch">生成</el-button>
      </template>
    </el-dialog>

    <!-- 编辑时段 -->
    <el-dialog v-model="slotEditDialog" title="编辑时段" width="420px">
      <el-form label-width="90px">
        <el-form-item label="最大人数"><el-input-number v-model="slotForm.maxCount" :min="1" :max="100" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="slotForm.status" style="width: 100%">
            <el-option label="可预约" value="AVAILABLE" />
            <el-option label="已满" value="FULL" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="slotEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitSlot">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.table-card {
  padding: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
