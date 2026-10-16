import { defineStore } from 'pinia'
import { authApi } from '@/api'

const TOKEN_KEY = 'yyzx_token'
const USER_KEY = 'yyzx_user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  }),

  getters: {
    isLogin: (state) => !!state.token,
    /** 1=管理员 2=健康管家 */
    roleId: (state) => state.user?.roleId,
    isAdmin: (state) => state.user?.roleId === 1,
    nickname: (state) => state.user?.nickname || '',
    roleName: (state) => (state.user?.roleId === 1 ? '管理员' : '健康管家')
  },

  actions: {
    async login(username, password) {
      const data = await authApi.login({ username, password })
      this.token = data.token
      this.user = data.user
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(USER_KEY, JSON.stringify(data.user))
      return data.user
    },

    async logout() {
      try {
        await authApi.logout()
      } catch (e) {
        // 忽略退出接口异常，本地状态照常清理
      }
      this.clear()
    },

    clear() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})
