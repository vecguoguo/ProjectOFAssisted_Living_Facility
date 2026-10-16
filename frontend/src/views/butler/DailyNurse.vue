<template>
  <div class="page-card">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 14px">
      对客户已购的护理项目登记护理，会扣减剩余次数并生成护理记录（同一事务，次数不足会回滚）。
    </el-alert>

    <div class="page-toolbar">
      <el-select v-model="customerId" filterable placeholder="选择我负责的客户" style="width: 280px" @change="loadItems">
        <el-option v-for="c in customers" :key="c.id" :label="`${c.customerName}（房间 ${c.roomNo}）`" :value="c.id" />
      </el-select>
      <el-button :disabled="!customerId" @click="loadItems">
        <el-icon><Refresh /></el-icon>
        <span>刷新</span>
      </el-button>
    </div>

    <el-table :data="items" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="itemName" label="护理项目" min-width="150" />
      <el-table-column prop="servicePrice" label="单价" width="100" />
      <el-table-column prop="nurseNumber" label="剩余次数" width="110" />
      <el-table-column prop="maturityTime" label="到期时间" width="130" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="itemStatusTag(row)" size="small">{{ itemStatusText(row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :disabled="row.nurseNumber <= 0" @click="openNurse(row)">
            登记护理
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="customerId && !loading && !items.length" description="该客户暂未购买护理项目" />
    <el-empty v-if="!customerId" description="请先选择客户" />

    <el-dialog v-model="dialogVisible" :title="`登记护理：${current?.itemName || ''}`" width="500px">
      <el-form :model="nurseForm" label-width="100px">
        <el-form-item label="护理时间">
          <el-date-picker
            v-model="nurseForm.nursingTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="护理数量">
          <el-input-number v-model="nurseForm.nursingCount" :min="1" :max="99" style="width: 100%" />
        </el-form-item>
        <el-form-item label="护理内容">
          <el-input v-model="nurseForm.nursingContent" type="textarea" :rows="3" placeholder="本次护理的具体内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">确认登记</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { customerNurseApi } from '@/api'

const customers = ref([])
const customerId = ref(null)
const items = ref([])
const loading = ref(false)
const submitting = ref(false)

const dialogVisible = ref(false)
const current = ref(null)
const nurseForm = reactive({ nursingTime: '', nursingCount: 1, nursingContent: '' })

const today = () => new Date().toISOString().slice(0, 10)
const itemStatusText = (row) => {
  if (row.nurseNumber <= 0) return '欠费'
  if (row.maturityTime && row.maturityTime < today()) return '到期'
  return '正常'
}
const itemStatusTag = (row) => {
  const s = itemStatusText(row)
  return s === '正常' ? 'success' : s === '欠费' ? 'danger' : 'warning'
}

const nowStr = () => {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

const loadCustomers = async () => {
  customers.value = await customerNurseApi.listMyCustomers()
}

const loadItems = async () => {
  if (!customerId.value) return
  loading.value = true
  try {
    items.value = await customerNurseApi.listCustomerItems(customerId.value)
  } finally {
    loading.value = false
  }
}

const openNurse = (row) => {
  current.value = row
  Object.assign(nurseForm, { nursingTime: nowStr(), nursingCount: 1, nursingContent: '' })
  dialogVisible.value = true
}

const onSubmit = async () => {
  submitting.value = true
  try {
    await customerNurseApi.doDailyNurse({
      customerNurseItemId: current.value.id,
      nursingTime: nurseForm.nursingTime,
      nursingContent: nurseForm.nursingContent,
      nursingCount: nurseForm.nursingCount
    })
    ElMessage.success('护理完成')
    dialogVisible.value = false
    loadItems()
  } finally {
    submitting.value = false
  }
}

onMounted(loadCustomers)
</script>
