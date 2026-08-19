<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigs, updateConfig } from '@/api/config'
import { formatDate } from '@/utils/format'

const list = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    list.value = await getConfigs()
  } catch {
    // 统一提示
  } finally {
    loading.value = false
  }
}
onMounted(load)

const editVisible = ref(false)
const editForm = reactive({ key: '', value: '', description: '' })
const openEdit = (row) => {
  Object.assign(editForm, { key: row.configKey, value: row.configValue, description: row.description })
  editVisible.value = true
}
const submit = async () => {
  await updateConfig(editForm.key, editForm.value)
  ElMessage.success('已保存')
  editVisible.value = false
  load()
}
</script>

<template>
  <div class="config">
    <div class="page-head">
      <h2 class="page-title">系统配置</h2>
      <p class="page-sub">AI 提示词、积分规则等系统参数</p>
    </div>
    <div class="fs-card table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="configKey" label="配置键" width="240" />
        <el-table-column prop="configValue" label="配置值" min-width="280" show-overflow-tooltip />
        <el-table-column prop="description" label="说明" min-width="180" show-overflow-tooltip />
        <el-table-column label="更新时间" width="150">
          <template #default="{ row }">{{ formatDate(row.updateTime || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="editVisible" title="编辑配置" width="520px">
      <el-form label-width="90px">
        <el-form-item label="配置键"><el-input :model-value="editForm.key" disabled /></el-form-item>
        <el-form-item label="配置值"><el-input v-model="editForm.value" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="说明"><el-input :model-value="editForm.description" disabled /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.table-card {
  padding: 16px;
}
</style>
