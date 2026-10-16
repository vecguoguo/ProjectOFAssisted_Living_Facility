<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-select
        v-model="customerId"
        filterable
        placeholder="选择客户查看护理项目"
        style="width: 260px"
        @change="loadItems"
      >
        <el-option v-for="c in customers" :key="c.id" :label="`${c.customerName}（房间 ${c.roomNo}）`" :value="c.id" />
      </el-select>
      <el-button @click="loadItems" :disabled="!customerId">
        <el-icon><Refresh /></el-icon>
        <span>刷新</span>
      </el-button>
      <div class="spacer" />
      <el-button type="success" :disabled="!customerId" @click="openPurchase">
        <el-icon><ShoppingCart /></el-icon>
        <span>购买护理项目</span>
      </el-button>
    </div>

    <el-divider content-position="left">已购护理项目</el-divider>
    <el-table :data="items" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="itemName" label="护理项目" min-width="140" />
      <el-table-column prop="servicePrice" label="单价" width="100" />
      <el-table-column prop="nurseNumber" label="剩余次数" width="110" />
      <el-table-column prop="buyTime" label="购买时间" width="130" />
      <el-table-column prop="maturityTime" label="到期时间" width="130" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="itemStatusTag(row)" size="small">{{ itemStatusText(row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openRenew(row)">续费</el-button>
          <el-button link type="danger" @click="onRemoveItem(row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="customerId && !loading && !items.length" description="该客户暂未购买护理项目" />
    <el-empty v-if="!customerId" description="请先选择客户" />

    <el-dialog v-model="purchaseVisible" title="购买护理项目" width="520px">
      <el-form :model="purchaseForm" label-width="100px">
        <el-form-item label="护理项目">
          <el-select v-model="purchaseForm.itemId" style="width: 100%">
            <el-option v-for="i in availableItems" :key="i.id" :label="`${i.nursingName}（${i.servicePrice}）`" :value="i.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="购买次数">
          <el-input-number v-model="purchaseForm.number" :min="1" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="到期时间">
          <el-date-picker v-model="purchaseForm.maturityTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseVisible = false">取消</el-button>
        <el-button type="primary" @click="onPurchase">确认购买</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="renewVisible" title="护理项目续费" width="460px">
      <el-form :model="renewForm" label-width="110px">
        <el-form-item label="护理项目">
          <span>{{ renewForm.itemName }}</span>
        </el-form-item>
        <el-form-item label="增加次数">
          <el-input-number v-model="renewForm.addNumber" :min="1" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="新到期时间">
          <el-date-picker v-model="renewForm.newMaturityTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" @click="onRenew">确认续费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerNurseApi } from '@/api'

const customers = ref([])
const customerId = ref(null)
const items = ref([])
const availableItems = ref([])
const loading = ref(false)

const purchaseVisible = ref(false)
const renewVisible = ref(false)
const purchaseForm = reactive({ itemId: null, number: 1, maturityTime: '' })
const renewForm = reactive({ customerNurseItemId: null, itemName: '', addNumber: 1, newMaturityTime: '' })

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

const loadCustomers = async () => {
  customers.value = await customerNurseApi.listCustomers()
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

const openPurchase = async () => {
  availableItems.value = await customerNurseApi.listAvailableItems(customerId.value)
  if (!availableItems.value.length) {
    ElMessage.warning('该客户已购买全部可用的护理项目')
    return
  }
  Object.assign(purchaseForm, { itemId: availableItems.value[0].id, number: 1, maturityTime: '' })
  purchaseVisible.value = true
}

const onPurchase = async () => {
  if (!purchaseForm.itemId || !purchaseForm.maturityTime) {
    ElMessage.warning('请选择护理项目和到期时间')
    return
  }
  await customerNurseApi.purchase({
    customerId: customerId.value,
    itemId: purchaseForm.itemId,
    number: purchaseForm.number,
    maturityTime: purchaseForm.maturityTime
  })
  ElMessage.success('购买成功')
  purchaseVisible.value = false
  loadItems()
}

const openRenew = (row) => {
  Object.assign(renewForm, {
    customerNurseItemId: row.id,
    itemName: row.itemName,
    addNumber: 1,
    newMaturityTime: row.maturityTime
  })
  renewVisible.value = true
}

const onRenew = async () => {
  await customerNurseApi.renew({
    customerNurseItemId: renewForm.customerNurseItemId,
    addNumber: renewForm.addNumber,
    newMaturityTime: renewForm.newMaturityTime
  })
  ElMessage.success('续费成功')
  renewVisible.value = false
  loadItems()
}

const onRemoveItem = async (row) => {
  try {
    await ElMessageBox.confirm(`确定移除护理项目【${row.itemName}】吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await customerNurseApi.removeItem(row.id)
  ElMessage.success('移除成功')
  loadItems()
}

onMounted(loadCustomers)
</script>
