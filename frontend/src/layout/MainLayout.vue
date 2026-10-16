<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon><HomeFilled /></el-icon>
        <span>东软颐养中心</span>
      </div>
      <el-menu :default-active="activeMenu" router class="menu">
        <template v-for="m in menus" :key="m.path || m.title">
          <el-sub-menu v-if="m.children && m.children.length" :index="m.title">
            <template #title>
              <el-icon><component :is="m.icon" /></el-icon>
              <span>{{ m.title }}</span>
            </template>
            <el-menu-item v-for="c in m.children" :key="c.path" :index="c.path">
              {{ c.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="m.path">
            <el-icon><component :is="m.icon" /></el-icon>
            <span>{{ m.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">{{ route.meta.title || '' }}</div>
        <div class="header-user">
          <el-tag :type="store.isAdmin ? 'danger' : 'success'" size="small">{{ store.roleName }}</el-tag>
          <span class="nickname">{{ store.nickname }}</span>
          <el-button link type="primary" @click="onLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <keep-alive :max="10">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { menusByRole } from '@/router/menu'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const menus = computed(() => menusByRole(store.roleId))
const activeMenu = computed(() => route.path)

const onLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (e) {
    return
  }
  await store.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  height: 100%;
}

.aside {
  background: #001529;
  overflow-x: hidden;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 1px;
  background: #002140;
}

.menu {
  border-right: none;
  background: #001529;
}

.menu :deep(.el-menu-item),
.menu :deep(.el-sub-menu__title) {
  color: #c9d1d9;
}

.menu :deep(.el-menu-item:hover),
.menu :deep(.el-sub-menu__title:hover) {
  background: #17304d;
  color: #fff;
}

.menu :deep(.el-menu-item.is-active) {
  background: #1890ff;
  color: #fff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e8eb;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06);
}

.header-title {
  font-size: 17px;
  font-weight: 600;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nickname {
  font-size: 14px;
  color: #303133;
}

.main {
  background: #f0f2f5;
  padding: 16px;
}
</style>
