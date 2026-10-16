<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input
        v-model="name"
        placeholder="客户姓名（回车查询，留空查全部）"
        clearable
        style="width: 260px"
        @keyup.enter="load"
      />
      <el-button type="primary" @click="load">
        <el-icon><Search /></el-icon>
        <span>查询</span>
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
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <template v-if="row.auditstatus === 0">
            <el-button link type="success" @click="onAudit(row, true)">同意</el-button>
            <el-button link type="danger" @click="onAudit(row, false)">拒绝</el-button>
          </template>
          <span v-else class="text-muted">已处理</span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !list.length" description="暂无退住申请" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { backdownApi } from '@/api'

const loading = ref(false)
const list = ref([])
const name = ref('')

const retreatTypeText = (t) => ({ 0: '正常退住', 1: '死亡退住', 2: '保留床位' }[t] || '—')
const auditText = (s) => ({ 0: '已提交', 1: '同意', 2: '拒绝' }[s] || '—')
const auditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

const load = async () => {
  loading.value = true
  try {
    list.value = await backdownApi.list({ name: name.value || undefined })
  } finally {
    loading.value = false
  }
}

const onAudit = async (row, agree) => {
  const action = agree ? '同意' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${action}客户【${row.customerName}】的退住申请吗？`, '审核确认', {
      type: 'warning'
    })
  } catch (e) {
    return
  }
  await backdownApi.audit(row.id, agree)
  ElMessage.success(`审核完成：${action}`)
  load()
}

onMounted(load)
</script>
