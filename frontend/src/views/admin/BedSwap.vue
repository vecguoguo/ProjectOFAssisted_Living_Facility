<template>
  <div class="page-card">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 14px">
      选择客户后填写目标房间与空闲床位即可调换；原床位会被释放。
    </el-alert>

    <el-form :model="form" label-width="100px" style="max-width: 640px">
      <el-form-item label="选择客户">
        <el-select
          v-model="form.customerId"
          filterable
          placeholder="按姓名搜索客户"
          style="width: 100%"
          @change="onCustomerChange"
        >
          <el-option
            v-for="c in customers"
            :key="c.id"
            :label="`${c.customerName}（房间 ${c.roomNo}）`"
            :value="c.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="当前床位">
        <el-tag type="warning" v-if="currentBed">{{ currentBed.roomNo }}-{{ currentBed.bedNo }}</el-tag>
        <span v-else class="text-muted">请先选择客户</span>
      </el-form-item>

      <el-form-item label="目标房间号">
        <el-input-number v-model="form.roomNo" :min="100" :max="999" style="width: 100%" @change="loadFreeBeds" />
      </el-form-item>

      <el-form-item label="目标床位">
        <el-select v-model="form.newBedId" placeholder="请先选房间号" style="width: 100%">
          <el-option v-for="b in freeBeds" :key="b.id" :label="`${b.roomNo}-${b.bedNo}`" :value="b.id" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="onSubmit">确认调换</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { bedApi, customerApi } from '@/api'

const customers = ref([])
const freeBeds = ref([])
const currentBed = ref(null)
const submitting = ref(false)

const form = reactive({ customerId: null, roomNo: 101, newBedId: null })

const loadCustomers = async () => {
  customers.value = await customerApi.listAll()
}

const onCustomerChange = async (id) => {
  currentBed.value = null
  freeBeds.value = []
  form.newBedId = null
  const customer = customers.value.find((c) => c.id === id)
  if (customer?.bedId) {
    const bed = await bedApi.findById(customer.bedId)
    currentBed.value = bed
  }
}

const loadFreeBeds = async () => {
  form.newBedId = null
  if (!form.roomNo) return
  freeBeds.value = await bedApi.listFree(form.roomNo)
  if (!freeBeds.value.length) {
    ElMessage.warning('该房间暂无空闲床位')
  }
}

const onSubmit = async () => {
  if (!form.customerId) {
    ElMessage.warning('请先选择客户')
    return
  }
  if (!form.newBedId) {
    ElMessage.warning('请选择目标床位')
    return
  }
  submitting.value = true
  try {
    await bedApi.swap({ customerId: form.customerId, newBedId: form.newBedId })
    ElMessage.success('床位调换成功')
    await loadCustomers()
    await onCustomerChange(form.customerId)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCustomers()
  loadFreeBeds()
})
</script>
