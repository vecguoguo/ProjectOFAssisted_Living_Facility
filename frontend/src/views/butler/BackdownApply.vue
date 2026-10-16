<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input
        v-model="name"
        placeholder="客户姓名（回车查询）"
        clearable
        style="width: 220px"
        @keyup.enter="loadList"
      />
      <el-button type="primary" @click="loadList">
        <el-icon><Search /></el-icon>
        <span>查询</span>
      </el-button>
      <div class="spacer" />
      <el-button type="success" @click="openApply">
        <el-icon><CirclePlus /></el-icon>
        <span>提交退住申请</span>
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerName" label="客户" width="120" />
      <el-table-column label="退住类型" width="120">
        <template #default="{ row }">{{ retreatTypeText(row.retreattype) }}</template>
      </el-table-column>
      <el-table-column prop="retreattime" label="退住时间" width="130" />
      <el-table-column prop="retreatreason" label="退住原因" min-width="180" />
      <el-table-column label="审批状态" width="110">
        <template #default="{ row }">
          <el-tag :type="auditTag(row.auditstatus)" size="small">{{ auditText(row.auditstatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditperson" label="审批人" width="110" />
      <el-table-column prop="audittime" label="审批时间" width="130" />
    </el-table>

    <el-empty v-if="!loading && !list.length" description="暂无退住申请" />

    <el-dialog v-model="dialogVisible" title="提交退住申请" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" filterable placeholder="选择我负责的客户" style="width: 100%">
            <el-option
              v-for="c in customers"
              :key="c.id"
              :label="`${c.customerName}（房间 ${c.roomNo}）`"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="退住类型" prop="retreattype">
          <el-radio-group v-model="form.retreattype">
            <el-radio :value="0">正常退住</el-radio>
            <el-radio :value="1">死亡退住</el-radio>
            <el-radio :value="2">保留床位</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="退住时间" prop="retreattime">
          <el-date-picker v-model="form.retreattime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退住原因" prop="retreatreason">
          <el-input v-model="form.retreatreason" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { backdownApi, customerApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const customers = ref([])
const name = ref('')

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ customerId: null, retreattype: 0, retreattime: '', retreatreason: '' })

const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  retreattime: [{ required: true, message: '请选择退住时间', trigger: 'change' }],
  retreatreason: [{ required: true, message: '请填写退住原因', trigger: 'blur' }]
}

const retreatTypeText = (t) => ({ 0: '正常退住', 1: '死亡退住', 2: '保留床位' }[t] || '—')
const auditText = (s) => ({ 0: '已提交', 1: '同意', 2: '拒绝' }[s] || '—')
const auditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

const loadList = async () => {
  loading.value = true
  try {
    list.value = await backdownApi.list({ name: name.value || undefined, mine: true })
  } finally {
    loading.value = false
  }
}

const loadCustomers = async () => {
  customers.value = await customerApi.listMine()
}

const openApply = () => {
  Object.assign(form, { customerId: null, retreattype: 0, retreattime: '', retreatreason: '' })
  dialogVisible.value = true
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await backdownApi.submit(form)
    ElMessage.success('退住申请提交成功')
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCustomers()
  loadList()
})
</script>
