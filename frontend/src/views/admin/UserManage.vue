<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input v-model="query.nickname" placeholder="昵称模糊查询" clearable style="width: 180px" @keyup.enter="load" />
      <el-select v-model="query.roleId" placeholder="角色" clearable style="width: 140px">
        <el-option label="管理员" :value="1" />
        <el-option label="健康管家" :value="2" />
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
      <el-button type="success" @click="openAdd">
        <el-icon><CirclePlus /></el-icon>
        <span>新增用户</span>
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="username" label="账号" width="140" />
      <el-table-column label="角色" width="110">
        <template #default="{ row }">
          <el-tag :type="row.roleId === 1 ? 'danger' : 'success'" size="small">
            {{ row.roleId === 1 ? '管理员' : '健康管家' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="80">
        <template #default="{ row }">{{ row.sex === 0 ? '男' : row.sex === 1 ? '女' : '—' }}</template>
      </el-table-column>
      <el-table-column prop="phoneNumber" label="手机号" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="160" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="warning" @click="onReset(row)">重置密码</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" placeholder="不填默认 123456" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" style="width: 100%">
            <el-option label="管理员" :value="1" />
            <el-option label="健康管家" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :value="0">男</el-radio>
            <el-radio :value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phoneNumber" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
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
import { userApi } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const query = reactive({ nickname: '', roleId: null })

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({
  id: null,
  nickname: '',
  username: '',
  password: '',
  roleId: 2,
  sex: 0,
  phoneNumber: '',
  email: ''
})
const form = reactive(emptyForm())

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const load = async () => {
  loading.value = true
  try {
    list.value = await userApi.list({ nickname: query.nickname || undefined, roleId: query.roleId || undefined })
  } finally {
    loading.value = false
  }
}

/** 刷新：清空查询条件并重新拉取全部用户，确保新增/修改后的数据及时可见 */
const refresh = () => {
  query.nickname = ''
  query.roleId = null
  load()
}

const openAdd = () => {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, emptyForm(), row, { password: '' })
  dialogVisible.value = true
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) {
      await userApi.update(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await userApi.add(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

const onReset = async (row) => {
  try {
    await ElMessageBox.confirm(`确定重置【${row.nickname}】的密码吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  const pwd = await userApi.resetPassword(row.id)
  ElMessageBox.alert(`重置后的密码为：${pwd}`, '重置成功', { confirmButtonText: '知道了' })
}

const onDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除用户【${row.nickname}】吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await userApi.remove(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
