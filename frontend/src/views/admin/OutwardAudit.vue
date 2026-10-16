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
      <el-table-column prop="escorted" label="陪同人" width="100" />
      <el-table-column label="审批状态" width="100">
        <template #default="{ row }">
          <el-tag :type="auditTag(row.auditstatus)" size="small">{{ auditText(row.auditstatus) }}</el-tag>
        </template>
      </el-table-column>
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

    <el-empty v-if="!loading && !list.length" description="暂无外出申请" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { outwardApi } from '@/api'

const loading = ref(false)
const list = ref([])
const name = ref('')

const auditText = (s) => ({ 0: '已提交', 1: '同意', 2: '拒绝' }[s] || '—')
const auditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

const load = async () => {
  loading.value = true
  try {
    list.value = await outwardApi.list({ name: name.value || undefined })
  } finally {
    loading.value = false
  }
}

const onAudit = async (row, agree) => {
  const action = agree ? '同意' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${action}客户【${row.customerName}】的外出申请吗？`, '审核确认', {
      type: 'warning'
    })
  } catch (e) {
    return
  }
  await outwardApi.audit(row.id, agree)
  ElMessage.success(`审核完成：${action}`)
  load()
}

onMounted(load)
</script>
