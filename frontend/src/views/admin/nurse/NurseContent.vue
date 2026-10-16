<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input v-model="query.nameLike" placeholder="项目名称" clearable style="width: 180px" @keyup.enter="load" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px">
        <el-option label="启用" :value="1" />
        <el-option label="停用" :value="2" />
      </el-select>
      <el-button type="primary" @click="load">
        <el-icon><Search /></el-icon>
        <span>查询</span>
      </el-button>
      <div class="spacer" />
      <el-button type="success" @click="openAdd">
        <el-icon><CirclePlus /></el-icon>
        <span>新增项目</span>
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="serialNumber" label="编号" width="110" />
      <el-table-column prop="nursingName" label="护理项目" min-width="140" />
      <el-table-column prop="servicePrice" label="价格" width="100" />
      <el-table-column prop="executionCycle" label="执行周期" width="120" />
      <el-table-column prop="executionTimes" label="执行次数" width="120" />
      <el-table-column prop="message" label="项目说明" min-width="160" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑护理项目' : '新增护理项目'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="项目编号" prop="serialNumber">
          <el-input v-model="form.serialNumber" placeholder="如 HL-007" />
        </el-form-item>
        <el-form-item label="项目名称" prop="nursingName">
          <el-input v-model="form.nursingName" />
        </el-form-item>
        <el-form-item label="价格" prop="servicePrice">
          <el-input v-model="form.servicePrice" placeholder="如 50 元/次" />
        </el-form-item>
        <el-form-item label="执行周期">
          <el-input v-model="form.executionCycle" placeholder="如 每日一次" />
        </el-form-item>
        <el-form-item label="执行次数">
          <el-input v-model="form.executionTimes" placeholder="如 1 次/天" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目说明">
          <el-input v-model="form.message" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nurseContentApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const query = reactive({ nameLike: '', status: null })

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({
  id: null,
  serialNumber: '',
  nursingName: '',
  servicePrice: '',
  executionCycle: '',
  executionTimes: '',
  status: 1,
  message: ''
})
const form = reactive(emptyForm())

const rules = {
  serialNumber: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
  nursingName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  servicePrice: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const load = async () => {
  loading.value = true
  try {
    list.value = await nurseContentApi.list({
      nameLike: query.nameLike || undefined,
      status: query.status || undefined
    })
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, emptyForm(), row)
  dialogVisible.value = true
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) {
      await nurseContentApi.update(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await nurseContentApi.add(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除护理项目【${row.nursingName}】吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await nurseContentApi.remove(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
