<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input
        v-model="query.name"
        placeholder="客户姓名模糊查询"
        clearable
        style="width: 200px"
        @keyup.enter="load"
      />
      <el-select v-model="query.sex" placeholder="性别" clearable style="width: 120px">
        <el-option label="男" :value="0" />
        <el-option label="女" :value="1" />
      </el-select>
      <el-button type="primary" @click="load">
        <el-icon><Search /></el-icon>
        <span>查询</span>
      </el-button>
      <el-button @click="resetQuery">
        <el-icon><Refresh /></el-icon>
        <span>重置</span>
      </el-button>
    </div>

    <el-table :data="filtered" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column label="性别" width="80">
        <template #default="{ row }">{{ sexText(row.customerSex) }}</template>
      </el-table-column>
      <el-table-column prop="customerAge" label="年龄" width="80" />
      <el-table-column prop="idcard" label="身份证号" width="180" />
      <el-table-column prop="contactTel" label="联系电话" width="130" />
      <el-table-column prop="roomNo" label="房间号" width="90" />
      <el-table-column prop="checkinDate" label="入住日期" width="120" />
      <el-table-column prop="expirationDate" label="合同到期" width="120" />
      <el-table-column label="健康管家" width="120">
        <template #default="{ row }">
          <el-tag v-if="butlerName(row.userId)" size="small">{{ butlerName(row.userId) }}</el-tag>
          <span v-else class="text-muted">未分配</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">查看</el-button>
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="openAssign(row)">分配管家</el-button>
          <el-button v-if="row.userId && row.userId > 0" link type="danger" @click="onRemoveButler(row)">
            移除
          </el-button>
          <el-button link type="danger" @click="onRetire(row)">退住</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && !filtered.length" description="暂无客户数据" />

    <el-dialog v-model="detailVisible" title="客户详情" width="720px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="客户ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ detail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ sexText(detail.customerSex) }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detail.customerAge }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detail.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detail.idcard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactTel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家属姓名">{{ detail.familyMember || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detail.roomNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="楼栋号">{{ detail.buildingNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ detail.checkinDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同到期">{{ detail.expirationDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身高(cm)">{{ detail.height || '-' }}</el-descriptions-item>
        <el-descriptions-item label="体重(kg)">{{ detail.weight || '-' }}</el-descriptions-item>
        <el-descriptions-item label="血型">{{ detail.bloodType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="健康管家">{{ butlerName(detail.userId) || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="身心状态" :span="2">
          {{ detail.psychosomaticState || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="注意事项" :span="2">{{ detail.attention || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑客户资料" width="640px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="editForm.customerName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="customerSex">
              <el-radio-group v-model="editForm.customerSex">
                <el-radio :value="0">男</el-radio>
                <el-radio :value="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="editForm.birthday"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                @change="onEditBirthdayChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input v-model="editForm.customerAge" disabled placeholder="根据出生日期自动计算" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="editForm.contactTel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家属姓名">
              <el-input v-model="editForm.familyMember" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身高(cm)">
              <el-input v-model="editForm.height" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)">
              <el-input v-model="editForm.weight" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="血型">
          <el-select v-model="editForm.bloodType" clearable style="width: 100%">
            <el-option v-for="t in ['A', 'B', 'AB', 'O']" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="身心状态">
          <el-input v-model="editForm.psychosomaticState" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="注意事项">
          <el-input v-model="editForm.attention" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignVisible" title="分配健康管家" width="420px">
      <el-form label-width="90px">
        <el-form-item label="客户">
          <span>{{ assignForm.customerName }}</span>
        </el-form-item>
        <el-form-item label="健康管家">
          <el-select v-model="assignForm.butlerId" placeholder="请选择健康管家" style="width: 100%">
            <el-option
              v-for="b in butlers"
              :key="b.id"
              :label="`${b.nickname}（${b.username}）`"
              :value="b.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="onAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi, customerNurseApi } from '@/api'

const loading = ref(false)
const customers = ref([])
const butlers = ref([])
const query = reactive({ name: '', sex: null })

const detailVisible = ref(false)
const detail = ref({})
const assignVisible = ref(false)
const assignForm = reactive({ customerId: null, customerName: '', butlerId: null })

const editVisible = ref(false)
const editFormRef = ref()
const saving = ref(false)
const editForm = reactive({})

const editRules = {
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  customerSex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }]
}

/** 按出生日期计算周岁（与后端 DateUtil.calcAge 一致） */
const calcAge = (val) => {
  if (!val) return null
  const [y, m, d] = String(val).split('-').map(Number)
  if (!y || !m || !d) return null
  const today = new Date()
  let age = today.getFullYear() - y
  const monthDiff = today.getMonth() + 1 - m
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < d)) {
    age--
  }
  return age < 0 ? 0 : age
}
const onEditBirthdayChange = (val) => {
  editForm.customerAge = calcAge(val)
}

const sexText = (s) => (s === 0 ? '男' : s === 1 ? '女' : '-')

const butlerNameMap = computed(() => {
  const map = {}
  for (const b of butlers.value) {
    map[b.id] = b.nickname
  }
  return map
})
const butlerName = (userId) =>
  userId && userId > 0 ? butlerNameMap.value[userId] || `#${userId}` : ''

const filtered = computed(() => {
  const name = query.name.trim()
  return customers.value.filter((c) => {
    if (name && !(c.customerName || '').includes(name)) return false
    if (query.sex !== null && query.sex !== undefined && query.sex !== '' && c.customerSex !== query.sex) {
      return false
    }
    return true
  })
})

const load = async () => {
  loading.value = true
  try {
    customers.value = await customerApi.listAll()
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.name = ''
  query.sex = null
  load()
}

const loadButlers = async () => {
  butlers.value = await customerNurseApi.listButlers()
}

const openDetail = (row) => {
  detail.value = { ...row }
  detailVisible.value = true
}

const openEdit = (row) => {
  Object.assign(editForm, {
    id: row.id,
    customerName: row.customerName,
    customerSex: row.customerSex,
    birthday: row.birthday || '',
    customerAge: row.customerAge,
    contactTel: row.contactTel || '',
    familyMember: row.familyMember || '',
    height: row.height || '',
    weight: row.weight || '',
    bloodType: row.bloodType || '',
    psychosomaticState: row.psychosomaticState || '',
    attention: row.attention || ''
  })
  editVisible.value = true
}

const onSave = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await customerApi.update(editForm.id, { ...editForm })
    ElMessage.success('保存成功')
    editVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const onRetire = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定为客户【${row.customerName}】办理退住吗？将释放床位【${row.roomNo}】并删除该客户记录。`,
      '提示',
      { type: 'warning' }
    )
  } catch (e) {
    return
  }
  await customerApi.remove(row.id)
  ElMessage.success('退住成功')
  load()
}

const openAssign = (row) => {
  Object.assign(assignForm, {
    customerId: row.id,
    customerName: row.customerName,
    butlerId: row.userId && row.userId > 0 ? row.userId : null
  })
  assignVisible.value = true
}

const onAssign = async () => {
  if (!assignForm.butlerId) {
    ElMessage.warning('请选择健康管家')
    return
  }
  await customerApi.assignButler(assignForm.customerId, assignForm.butlerId)
  ElMessage.success('分配成功')
  assignVisible.value = false
  load()
}

const onRemoveButler = async (row) => {
  try {
    await ElMessageBox.confirm(`确定移除客户【${row.customerName}】的健康管家吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await customerApi.removeButler(row.id)
  ElMessage.success('移除成功')
  load()
}

onMounted(() => {
  load()
  loadButlers()
})
</script>
