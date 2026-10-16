<template>
  <div class="page-card">
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 14px">
      入住登记会自动占用所选床位并生成床位使用记录；合同到期时间必须晚于入住时间。
    </el-alert>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" style="max-width: 900px">
      <el-divider content-position="left">基本信息</el-divider>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="客户姓名" prop="customerName">
            <el-input v-model="form.customerName" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="年龄" prop="customerAge">
            <el-input-number
              v-model="form.customerAge"
              :min="0"
              :max="130"
              disabled
              placeholder="根据出生日期自动计算"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="性别" prop="customerSex">
            <el-radio-group v-model="form.customerSex">
              <el-radio :value="0">男</el-radio>
              <el-radio :value="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="身份证号" prop="idcard">
            <el-input v-model="form.idcard" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="出生日期" prop="birthday">
            <el-date-picker
              v-model="form.birthday"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              @change="onBirthdayChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话">
            <el-input v-model="form.contactTel" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">床位与合同</el-divider>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="房间号" prop="roomNoNum">
            <el-input-number
              v-model="form.roomNoNum"
              :min="100"
              :max="999"
              style="width: 100%"
              @change="loadFreeBeds"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="床位" prop="bedId">
            <el-select v-model="form.bedId" placeholder="请先选房间号" style="width: 100%">
              <el-option v-for="b in freeBeds" :key="b.id" :label="`${b.roomNo}-${b.bedNo}`" :value="b.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="入住日期" prop="checkinDate">
            <el-date-picker v-model="form.checkinDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="合同到期" prop="expirationDate">
            <el-date-picker v-model="form.expirationDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="家属姓名">
            <el-input v-model="form.familyMember" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">健康信息</el-divider>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="身高(cm)">
            <el-input v-model="form.height" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="体重(kg)">
            <el-input v-model="form.weight" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="血型">
            <el-select v-model="form.bloodType" clearable style="width: 100%">
              <el-option v-for="t in ['A', 'B', 'AB', 'O']" :key="t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="身心状态">
        <el-input v-model="form.psychosomaticState" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="注意事项">
        <el-input v-model="form.attention" type="textarea" :rows="2" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交入住登记</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { bedApi, customerApi } from '@/api'

const formRef = ref()
const submitting = ref(false)
const freeBeds = ref([])

const emptyForm = () => ({
  customerName: '',
  customerAge: null,
  customerSex: 0,
  idcard: '',
  roomNoNum: 101,
  buildingNo: '606',
  bedId: null,
  checkinDate: new Date().toISOString().slice(0, 10),
  expirationDate: '',
  birthday: '',
  contactTel: '',
  familyMember: '',
  height: '',
  weight: '',
  bloodType: '',
  psychosomaticState: '',
  attention: ''
})

const form = reactive(emptyForm())

const rules = {
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  customerAge: [{ required: true, message: '年龄由出生日期自动计算', trigger: 'change' }],
  birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  idcard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  roomNoNum: [{ required: true, message: '请输入房间号', trigger: 'change' }],
  bedId: [{ required: true, message: '请选择床位', trigger: 'change' }],
  checkinDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  expirationDate: [{ required: true, message: '请选择合同到期时间', trigger: 'change' }]
}

/** 出生日期变更时自动计算年龄（与后端 DateUtil.calcAge 一致：满周岁） */
const onBirthdayChange = (val) => {
  if (!val) {
    form.customerAge = null
    return
  }
  const [y, m, d] = String(val).split('-').map(Number)
  if (!y || !m || !d) {
    form.customerAge = null
    return
  }
  const today = new Date()
  let age = today.getFullYear() - y
  const monthDiff = today.getMonth() + 1 - m
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < d)) {
    age--
  }
  form.customerAge = age < 0 ? 0 : age
}

const loadFreeBeds = async () => {
  form.bedId = null
  if (!form.roomNoNum) {
    freeBeds.value = []
    return
  }
  freeBeds.value = await bedApi.listFree(form.roomNoNum)
  if (!freeBeds.value.length) {
    ElMessage.warning('该房间暂无空闲床位')
  }
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const payload = { ...form, roomNo: String(form.roomNoNum) }
    delete payload.roomNoNum
    const id = await customerApi.checkIn(payload)
    ElMessage.success(`入住登记成功，客户ID：${id}`)
    reset()
  } finally {
    submitting.value = false
  }
}

const reset = () => {
  Object.assign(form, emptyForm())
  freeBeds.value = []
  formRef.value?.clearValidate()
}
</script>
