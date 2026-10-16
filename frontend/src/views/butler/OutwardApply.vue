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
        <span>提交外出申请</span>
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerName" label="客户" width="110" />
      <el-table-column prop="outgoingreason" label="外出事由" min-width="140" />
      <el-table-column prop="outgoingtime" label="外出时间" width="120" />
      <el-table-column prop="expectedreturntime" label="预计回院" width="120" />
      <el-table-column prop="actualreturntime" label="实际回院" width="120">
        <template #default="{ row }">
          <span v-if="row.actualreturntime">{{ row.actualreturntime }}</span>
          <span v-else class="text-muted">未回院</span>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" width="100">
        <template #default="{ row }">
          <el-tag :type="auditTag(row.auditstatus)" size="small">{{ auditText(row.auditstatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.auditstatus === 1 && !row.actualreturntime"
            link
            type="primary"
            @click="openReturn(row)"
          >
            登记回院
          </el-button>
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !list.length" description="暂无外出申请" />

    <el-dialog v-model="dialogVisible" title="提交外出申请" width="560px">
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
        <el-form-item label="外出事由" prop="outgoingreason">
          <el-input v-model="form.outgoingreason" placeholder="如：家属接出散心" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="外出时间" prop="outgoingtime">
              <el-date-picker v-model="form.outgoingtime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计回院" prop="expectedreturntime">
              <el-date-picker
                v-model="form.expectedreturntime"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="陪同人">
              <el-input v-model="form.escorted" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="与客户关系">
              <el-input v-model="form.relation" placeholder="如：儿子" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="陪同人电话">
          <el-input v-model="form.escortedtel" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="returnVisible" title="登记回院时间" width="420px">
      <el-form label-width="110px">
        <el-form-item label="实际回院时间">
          <el-date-picker v-model="returnDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onReturn">确认登记</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { customerApi, outwardApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const customers = ref([])
const name = ref('')

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  customerId: null,
  outgoingreason: '',
  outgoingtime: '',
  expectedreturntime: '',
  escorted: '',
  relation: '',
  escortedtel: ''
})

const returnVisible = ref(false)
const returnDate = ref('')
const currentRow = ref(null)

const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  outgoingreason: [{ required: true, message: '请填写外出事由', trigger: 'blur' }],
  outgoingtime: [{ required: true, message: '请选择外出时间', trigger: 'change' }],
  expectedreturntime: [{ required: true, message: '请选择预计回院时间', trigger: 'change' }]
}

const auditText = (s) => ({ 0: '已提交', 1: '同意', 2: '拒绝' }[s] || '—')
const auditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

const loadList = async () => {
  loading.value = true
  try {
    list.value = await outwardApi.list({ name: name.value || undefined, mine: true })
  } finally {
    loading.value = false
  }
}

const loadCustomers = async () => {
  customers.value = await customerApi.listMine()
}

const openApply = () => {
  Object.assign(form, {
    customerId: null,
    outgoingreason: '',
    outgoingtime: '',
    expectedreturntime: '',
    escorted: '',
    relation: '',
    escortedtel: ''
  })
  dialogVisible.value = true
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await outwardApi.submit(form)
    ElMessage.success('外出申请提交成功')
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

const openReturn = (row) => {
  currentRow.value = row
  returnDate.value = new Date().toISOString().slice(0, 10)
  returnVisible.value = true
}

const onReturn = async () => {
  if (!returnDate.value) {
    ElMessage.warning('请选择实际回院时间')
    return
  }
  submitting.value = true
  try {
    await outwardApi.registerReturn(currentRow.value.id, returnDate.value)
    ElMessage.success('回院时间登记成功')
    returnVisible.value = false
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
