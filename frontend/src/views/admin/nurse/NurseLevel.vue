<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-select v-model="status" placeholder="状态" clearable style="width: 140px" @change="load">
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
        <span>新增级别</span>
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="levelName" label="级别名称" min-width="160" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.levelStatus === 1 ? 'success' : 'info'" size="small">
            {{ row.levelStatus === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openConfig(row)">项目配置</el-button>
          <el-button link :type="row.levelStatus === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.levelStatus === 1 ? '停用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增护理级别" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="级别名称" prop="levelName">
          <el-input v-model="form.levelName" placeholder="如：一级护理" />
        </el-form-item>
        <el-form-item label="状态" prop="levelStatus">
          <el-select v-model="form.levelStatus" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="configVisible" :title="`级别项目配置：${currentLevel?.levelName || ''}`" size="680px">
      <el-row :gutter="16">
        <el-col :span="12">
          <h4 class="col-title">已配置项目</h4>
          <el-table :data="inLevelItems" border size="small">
            <el-table-column prop="nursingName" label="护理项目" />
            <el-table-column prop="servicePrice" label="价格" width="90" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button link type="danger" @click="removeItem(row)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
        <el-col :span="12">
          <h4 class="col-title">可添加项目</h4>
          <el-table :data="toAddItems" border size="small">
            <el-table-column prop="nursingName" label="护理项目" />
            <el-table-column prop="servicePrice" label="价格" width="90" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button link type="primary" @click="addItem(row)">添加</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { nurseLevelApi } from '@/api'

const loading = ref(false)
const list = ref([])
const status = ref(null)

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ levelName: '', levelStatus: 1 })
const rules = { levelName: [{ required: true, message: '请输入级别名称', trigger: 'blur' }] }

const configVisible = ref(false)
const currentLevel = ref(null)
const inLevelItems = ref([])
const toAddItems = ref([])

const load = async () => {
  loading.value = true
  try {
    list.value = await nurseLevelApi.list(status.value || undefined)
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  Object.assign(form, { levelName: '', levelStatus: 1 })
  dialogVisible.value = true
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  await nurseLevelApi.add(form)
  ElMessage.success('新增成功')
  dialogVisible.value = false
  load()
}

const toggleStatus = async (row) => {
  const next = row.levelStatus === 1 ? 2 : 1
  await nurseLevelApi.updateStatus(row.id, next)
  ElMessage.success(next === 1 ? '已启用' : '已停用')
  load()
}

const openConfig = async (row) => {
  currentLevel.value = row
  configVisible.value = true
  await refreshConfig()
}

const refreshConfig = async () => {
  const id = currentLevel.value.id
  inLevelItems.value = await nurseLevelApi.listItemsInLevel(id)
  toAddItems.value = await nurseLevelApi.listItemsToAdd(id)
}

const addItem = async (item) => {
  await nurseLevelApi.addItem(currentLevel.value.id, item.id)
  ElMessage.success('添加成功')
  refreshConfig()
}

const removeItem = async (item) => {
  await nurseLevelApi.removeItem(currentLevel.value.id, item.id)
  ElMessage.success('移除成功')
  refreshConfig()
}

onMounted(load)
</script>

<style scoped>
.col-title {
  margin-bottom: 10px;
  font-size: 14px;
  color: #303133;
}
</style>
