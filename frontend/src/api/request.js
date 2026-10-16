import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

/** 统一请求封装：自动带 token、统一处理业务码、401 自动跳登录 */
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const store = useUserStore()
  if (store.token) {
    config.headers.token = store.token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回 { code, message, data }
    if (res && res.code === 200) {
      return res.data
    }
    const message = (res && res.message) || '操作失败'
    if (res && res.code === 401) {
      const store = useUserStore()
      store.clear()
      router.push('/login')
    } else {
      ElMessage.error(message)
    }
    return Promise.reject(new Error(message))
  },
  (error) => {
    const message = error?.response ? `请求失败(${error.response.status})` : '网络异常，请确认后端服务已启动'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
