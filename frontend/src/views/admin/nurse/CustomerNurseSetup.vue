<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input
        v-model="nameLike"
        placeholder="客户姓名（回车查询）"
        clearable
        style="width: 220px"
        @keyup.enter="loadCustomers"
      />
      <el-button type="primary" @click="loadCustomers">
        <el-icon><Search /></el-icon>
        <span>查询</span>
      </el-button>
    </div>

    <el-table :data="customers" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerName" label="客户姓名" width="130" />
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column label="当前护理级别" min-width="160">
        <template #default="{ row }">
          <el-tag v-if="row.levelId" type="success" size="small">{{ levelName(row.levelId) }}</el-tag>
          <span v-else class="text-muted">未设置</span>
        </template>
      </el-table-column>
      <el-table-column prop="psychosomaticState" label="身心状态" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openSet(row)">设置级别</el-button>
          <el-button link type="danger" :disabled="!row.levelId" @click="onRemove(row)">移除级别</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="`设置护理级别：${current?.customerName || ''}`" width="440px">
      <el-form label-width="90px">
        <el-form-item label="护理级别">
          <el-select v-model="selectedLevelId" placeholder="请选择级别" style="width: 100%">
            <el-option v-for="l in levels" :key="l.id" :label="l.levelName" :value="l.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSet">确认设置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerNurseApi, nurseLevelApi } from '@/api'

const loading = ref(false)
const customers = ref([])
const levels = ref([])
const nameLike = ref('')

const dialogVisible = ref(false)
const current = ref(null)
const selectedLevelId = ref(null)

const levelName = (id) => levels.value.find((l) => l.id === id)?.levelName || `级别${id}`

const loadCustomers = async () => {
  loading.value = true
  try {
    customers.value = await customerNurseApi.listCustomers(nameLike.value || undefined)
  } finally {
    loading.value = false
  }
}

const loadLevels = async () => {
  levels.value = await nurseLevelApi.list(1)
}

const openSet = (row) => {
  current.value = row
  selectedLevelId.value = row.levelId || (levels.value[0]?.id ?? null)
  dialogVisible.value = true
}

const onSet = async () => {
  if (!selectedLevelId.value) {
    ElMessage.warning('请选择护理级别')
    return
  }
  await customerNurseApi.setLevel(current.value.id, selectedLevelId.value)
  ElMessage.success('设置成功')
  dialogVisible.value = false
  loadCustomers()
}

const onRemove = async (row) => {
  try {
    await ElMessageBox.confirm(`确定移除客户【${row.customerName}】的护理级别吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await customerNurseApi.removeLevel(row.id)
  ElMessage.success('移除成功')
  loadCustomers()
}

onMounted(() => {
  loadLevels()
  loadCustomers()
})
</script>
