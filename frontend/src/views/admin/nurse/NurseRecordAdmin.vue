<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-select v-model="customerId" filterable placeholder="选择客户查看护理记录" style="width: 260px" @change="loadRecords">
        <el-option v-for="c in customers" :key="c.id" :label="`${c.customerName}（房间 ${c.roomNo}）`" :value="c.id" />
      </el-select>
      <el-button :disabled="!customerId" @click="loadRecords">
        <el-icon><Refresh /></el-icon>
        <span>刷新</span>
      </el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="itemName" label="护理项目" min-width="140" />
      <el-table-column prop="nursingTime" label="护理时间" width="180" />
      <el-table-column prop="nursingContent" label="护理内容" min-width="180" show-overflow-tooltip />
      <el-table-column prop="nursingCount" label="数量" width="90" />
      <el-table-column prop="userId" label="登记人ID" width="110" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="customerId && !loading && !records.length" description="该客户暂无护理记录" />
    <el-empty v-if="!customerId" description="请先选择客户" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerNurseApi, nurseRecordApi } from '@/api'

const customers = ref([])
const customerId = ref(null)
const records = ref([])
const loading = ref(false)

const loadCustomers = async () => {
  customers.value = await customerNurseApi.listCustomers()
}

const loadRecords = async () => {
  if (!customerId.value) return
  loading.value = true
  try {
    records.value = await nurseRecordApi.listByCustomer(customerId.value)
  } finally {
    loading.value = false
  }
}

const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该护理记录吗？', '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await nurseRecordApi.remove(row.id)
  ElMessage.success('删除成功')
  loadRecords()
}

onMounted(loadCustomers)
</script>
