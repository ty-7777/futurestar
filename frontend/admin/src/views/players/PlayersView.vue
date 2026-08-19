<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPlayers,
  getPlayerDetail,
  updatePlayerStatus,
  updatePlayerLevel,
  updatePlayerPoints,
  resetPlayerPassword
} from '@/api/player'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', position: '', status: '' })

const POSITIONS = [
  { label: '前锋', value: 'FORWARD' },
  { label: '中场', value: 'MIDFIELDER' },
  { label: '后卫', value: 'DEFENDER' },
  { label: '门将', value: 'GOALKEEPER' }
]
const LEVELS = ['NORMAL', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND']
const LEVEL_LABEL = { NORMAL: '普通', SILVER: '白银', GOLD: '黄金', PLATINUM: '铂金', DIAMOND: '钻石' }
const APPOINT_STATUS = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELED: '已取消' }

const positionLabel = (v) => POSITIONS.find((p) => p.value === v)?.label || v || '-'
const footLabel = (v) => ({ LEFT: '左脚', RIGHT: '右脚', BOTH: '双脚' })[v] || v || '-'

const load = async () => {
  loading.value = true
  try {
    const data = await getPlayers(query)
    list.value = data.list
    total.value = data.total
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}
onMounted(load)

const onSearch = () => {
  query.pageNum = 1
  load()
}

const changeLevel = async (row, v) => {
  await updatePlayerLevel(row.id, v)
  row.memberLevel = v
  ElMessage.success('等级已更新')
}

const onToggleStatus = async (row) => {
  const next = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
  await updatePlayerStatus(row.id, next)
  ElMessage.success(next === 'ENABLED' ? '已启用' : '已禁用')
  load()
}

const onResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定将「${row.realName || row.phone}」的密码重置为 123456 吗？`, '提示', {
      type: 'warning'
    })
  } catch {
    return
  }
  await resetPlayerPassword(row.id)
  ElMessage.success('密码已重置为 123456')
}

// ---- 详情 ----
const detailVisible = ref(false)
const detail = ref(null)
const detailTab = ref('base')
const openDetail = async (row) => {
  detail.value = await getPlayerDetail(row.id)
  detailTab.value = 'base'
  detailVisible.value = true
}

// ---- 调积分 ----
const pointsVisible = ref(false)
const pointsForm = reactive({ id: null, delta: 0, reason: '' })
const openPoints = (row) => {
  pointsForm.id = row.id
  pointsForm.delta = 0
  pointsForm.reason = ''
  pointsVisible.value = true
}
const submitPoints = async () => {
  if (!pointsForm.delta) return ElMessage.warning('请输入积分变更量')
  await updatePlayerPoints(pointsForm.id, { delta: pointsForm.delta, reason: pointsForm.reason })
  ElMessage.success('积分已调整')
  pointsVisible.value = false
  load()
}
</script>

<template>
  <div class="players">
    <div class="page-head">
      <h2 class="page-title">球员管理</h2>
      <p class="page-sub">球员账号、积分、等级与状态管理</p>
    </div>
    <div class="fs-card filter-bar">
      <el-form inline>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="姓名/手机号"
            clearable
            style="width: 180px"
            @keyup.enter="onSearch"
          />
        </el-form-item>
        <el-form-item label="位置">
          <el-select v-model="query.position" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="p in POSITIONS" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 130px">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="fs-card table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="realName" label="姓名" width="110" />
        <el-table-column label="位置" width="90">
          <template #default="{ row }">{{ positionLabel(row.position) }}</template>
        </el-table-column>
        <el-table-column label="等级" width="130">
          <template #default="{ row }">
            <el-select :model-value="row.memberLevel" size="small" @change="(v) => changeLevel(row, v)">
              <el-option v-for="l in LEVELS" :key="l" :label="LEVEL_LABEL[l]" :value="l" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="150">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="230">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="openPoints(row)">积分</el-button>
            <el-button
              link
              size="small"
              :type="row.status === 'ENABLED' ? 'warning' : 'success'"
              @click="onToggleStatus(row)"
            >
              {{ row.status === 'ENABLED' ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" size="small" @click="onResetPassword(row)">重置密码</el-button>
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

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" title="球员详情" width="720px">
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基本信息" name="base">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="手机号">{{ detail?.phone }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ detail?.realName }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ detail?.gender }}</el-descriptions-item>
            <el-descriptions-item label="出生日期">{{ detail?.birthDate }}</el-descriptions-item>
            <el-descriptions-item label="身高">{{ detail?.height }} cm</el-descriptions-item>
            <el-descriptions-item label="体重">{{ detail?.weight }} kg</el-descriptions-item>
            <el-descriptions-item label="位置">{{ positionLabel(detail?.position) }}</el-descriptions-item>
            <el-descriptions-item label="惯用脚">{{ footLabel(detail?.preferredFoot) }}</el-descriptions-item>
            <el-descriptions-item label="球龄">{{ detail?.experienceYears }} 年</el-descriptions-item>
            <el-descriptions-item label="等级">{{ LEVEL_LABEL[detail?.memberLevel] }}</el-descriptions-item>
            <el-descriptions-item label="积分">{{ detail?.points }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ detail?.status === 'ENABLED' ? '启用' : '禁用' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="体能记录" name="physical">
          <el-table :data="detail?.physicalRecords || []" size="small">
            <el-table-column label="记录时间" width="150">
              <template #default="{ row }">{{ formatDate(row.recordedAt) }}</template>
            </el-table-column>
            <el-table-column prop="height" label="身高" width="70" />
            <el-table-column prop="weight" label="体重" width="70" />
            <el-table-column prop="bmi" label="BMI" width="70" />
            <el-table-column prop="heartRate" label="心率" width="70" />
            <el-table-column prop="sprint30m" label="30米(s)" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="预约记录" name="appointments">
          <el-table :data="detail?.appointments || []" size="small">
            <el-table-column prop="packageName" label="套餐" width="150" />
            <el-table-column prop="courseDate" label="日期" width="110" />
            <el-table-column prop="timeRange" label="时段" width="120" />
            <el-table-column label="状态">
              <template #default="{ row }">{{ APPOINT_STATUS[row.status] || row.status }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 调积分 -->
    <el-dialog v-model="pointsVisible" title="调整积分" width="420px">
      <el-form label-width="80px">
        <el-form-item label="变更量">
          <el-input-number v-model="pointsForm.delta" :step="10" />
          <span class="tip">正数加、负数减</span>
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="pointsForm.reason" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPoints">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.filter-bar {
  padding: 16px 16px 0;
  margin-bottom: 16px;
}
.table-card {
  padding: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.tip {
  margin-left: 10px;
  font-size: 12px;
  color: #98a1b0;
}
</style>
