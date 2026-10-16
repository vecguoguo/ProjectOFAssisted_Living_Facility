<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input-number v-model="query.roomNo" :min="100" :max="999" placeholder="房间号" style="width: 140px" />
      <el-input v-model="query.bedNo" placeholder="床位号" clearable style="width: 120px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px">
        <el-option label="空闲" :value="1" />
        <el-option label="有人" :value="2" />
        <el-option label="外出" :value="3" />
      </el-select>
      <el-button type="primary" @click="load">
        <el-icon><Search /></el-icon>
        <span>查询</span>
      </el-button>
      <el-button type="primary" plain @click="refresh">
        <el-icon><Refresh /></el-icon>
        <span>刷新</span>
      </el-button>
      <div class="spacer" />
      <el-button type="warning" :disabled="selectedId === null" @click="openEditSelected">
        <el-icon><Edit /></el-icon>
        <span>修改</span>
      </el-button>
      <el-button type="success" @click="openAdd">
        <el-icon><CirclePlus /></el-icon>
        <span>新增床位</span>
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column label="选择" width="60" align="center">
        <template #default="{ row }">
          <el-radio v-model="selectedId" :value="row.id" />
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="roomNo" label="房间号" width="110" />
      <el-table-column prop="bedNo" label="床位号" width="100" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.bedStatus)" size="small">{{ statusText(row.bedStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remarks" label="备注" min-width="200" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑床位' : '新增床位'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="房间号" prop="roomNo">
          <el-input-number v-model="form.roomNo" :min="100" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="床位号" prop="bedNo">
          <el-input v-model="form.bedNo" placeholder="如 A / B / C" />
        </el-form-item>
        <el-form-item label="状态" prop="bedStatus">
          <el-select v-model="form.bedStatus" style="width: 100%">
            <el-option label="空闲" :value="1" />
            <el-option label="有人" :value="2" />
            <el-option label="外出" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remarks" />
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
import { bedApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const query = reactive({ roomNo: null, bedNo: '', status: null })

const dialogVisible = ref(false)
const formRef = ref()
const selectedId = ref(null)
const emptyForm = () => ({ id: null, roomNo: 101, bedNo: '', bedStatus: 1, remarks: '' })
const form = reactive(emptyForm())

const rules = {
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'change' }],
  bedNo: [{ required: true, message: '请输入床位号', trigger: 'blur' }],
  bedStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const statusText = (s) => ({ 1: '空闲', 2: '有人', 3: '外出' }[s] || '未知')
const statusTag = (s) => ({ 1: 'success', 2: 'warning', 3: 'info' }[s] || '')

const load = async () => {
  loading.value = true
  try {
    list.value = await bedApi.query({
      roomNo: query.roomNo || undefined,
      bedNo: query.bedNo || undefined,
      status: query.status || undefined
    })
    selectedId.value = null
  } finally {
    loading.value = false
  }
}

/** 刷新：清空查询条件并重新拉取全部床位，确保新增/修改后的数据及时可见 */
const refresh = () => {
  query.roomNo = null
  query.bedNo = ''
  query.status = null
  load()
}

const openAdd = () => {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, emptyForm(), row)
  dialogVisible.value = true
}

/** 修改选中行：先勾选表格中的单选按钮，再点击“修改” */
const openEditSelected = () => {
  const row = list.value.find((b) => b.id === selectedId.value)
  if (!row) {
    ElMessage.warning('请先选择要修改的床位')
    return
  }
  openEdit(row)
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) {
      await bedApi.update(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await bedApi.add(form)
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
    await ElMessageBox.confirm(`确定删除床位【${row.roomNo}-${row.bedNo}】吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await bedApi.remove(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
