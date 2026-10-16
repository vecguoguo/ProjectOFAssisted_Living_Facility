<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="login-title">
        <el-icon :size="28"><HomeFilled /></el-icon>
        <h2>东软颐养中心管理系统</h2>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onSubmit">
        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" placeholder="请输入账号" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="onSubmit">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="center">测试账号</el-divider>
      <div class="tips">
        <p>管理员：<b>admin</b> / <b>admin123</b></p>
        <p>健康管家：<b>guanjia01</b> / <b>345678</b></p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const store = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const user = await store.login(form.username.trim(), form.password)
    ElMessage.success(`登录成功，欢迎您 ${user.nickname}`)
    router.push('/')
  } catch (e) {
    // 错误提示已在 axios 拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f4e79 0%, #2f7fbf 60%, #4aa3df 100%);
}

.login-card {
  width: 420px;
  padding: 8px 12px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.login-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin: 8px 0 20px;
  color: #1f4e79;
}

.login-title h2 {
  font-size: 20px;
  font-weight: 600;
}

.tips {
  font-size: 13px;
  color: #606266;
  line-height: 1.9;
  text-align: center;
}
</style>
