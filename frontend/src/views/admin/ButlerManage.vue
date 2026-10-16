<template>
  <div class="page-card">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 14px">
      选择健康管家查看其负责的客户；下方可把“暂无管家”的客户分配给指定管家。
    </el-alert>

    <el-divider content-position="left">健康管家负责的客户</el-divider>
    <div class="page-toolbar">
      <el-select v-model="selectedButlerId" placeholder="请选择健康管家" style="width: 240px" @change="loadOwned">
        <el-option
          v-for="b in butlers"
          :key="b.id"
          :label="`${b.nickname}（${b.username}）`"
          :value="b.id"
        />
      </el-select>
      <el-button @click="loadOwned"><el-icon><Refresh /></el-icon><span>刷新</span></el-button>
    </div>

    <el-table :data="ownedCustomers" border stripe v-loading="loadingOwned">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerName" label="客户姓名" width="130" />
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column label="性别" width="80">
        <template #default="{ row }">{{ row.customerSex === 0 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="contactTel" label="联系电话" width="140" />
      <el-table-column prop="checkinDate" label="入住日期" width="130" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button link type="danger" @click="onRemove(row)">移除管家</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-divider content-position="left">暂无健康管家的客户</el-divider>
    <el-table :data="freeCustomers" border stripe v-loading="loadingFree">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerName" label="客户姓名" width="130" />
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column prop="contactTel" label="联系电话" width="140" />
      <el-table-column prop="checkinDate" label="入住日期" width="130" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button link type="primary" :disabled="!selectedButlerId" @click="onAssign(row)">
            分配管家
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loadingFree && !freeCustomers.length" description="所有客户均已分配健康管家" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi, customerNurseApi } from '@/api'

const butlers = ref([])
const selectedButlerId = ref(null)
const ownedCustomers = ref([])
const freeCustomers = ref([])
const loadingOwned = ref(false)
const loadingFree = ref(false)

const loadButlers = async () => {
  butlers.value = await customerNurseApi.listButlers()
  if (!selectedButlerId.value && butlers.value.length) {
    selectedButlerId.value = butlers.value[0].id
    await loadOwned()
  }
}

const loadOwned = async () => {
  if (!selectedButlerId.value) {
    ownedCustomers.value = []
    return
  }
  loadingOwned.value = true
  try {
    ownedCustomers.value = await customerApi.listByButler(selectedButlerId.value)
  } finally {
    loadingOwned.value = false
  }
}

const loadFree = async () => {
  loadingFree.value = true
  try {
    freeCustomers.value = await customerApi.listWithoutButler()
  } finally {
    loadingFree.value = false
  }
}

const onAssign = async (row) => {
  if (!selectedButlerId.value) {
    ElMessage.warning('请先在上方选择健康管家')
    return
  }
  const butler = butlers.value.find((b) => b.id === selectedButlerId.value)
  try {
    await ElMessageBox.confirm(`确定把客户【${row.customerName}】分配给【${butler.nickname}】吗？`, '提示', {
      type: 'warning'
    })
  } catch (e) {
    return
  }
  await customerApi.assignButler(row.id, selectedButlerId.value)
  ElMessage.success('分配成功')
  await Promise.all([loadFree(), loadOwned()])
}

const onRemove = async (row) => {
  try {
    await ElMessageBox.confirm(`确定移除客户【${row.customerName}】的健康管家吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await customerApi.removeButler(row.id)
  ElMessage.success('移除成功')
  await Promise.all([loadFree(), loadOwned()])
}

onMounted(() => {
  loadButlers()
  loadFree()
})
</script>
