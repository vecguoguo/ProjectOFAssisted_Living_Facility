import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/bed-schematic',
    children: [
      /* ---------- 管理员与管家共用 ---------- */
      {
        path: 'bed-schematic',
        name: 'bed-schematic',
        component: () => import('@/views/BedSchematic.vue'),
        meta: { title: '床位示意图', roles: [1, 2] }
      },
      /* ---------- 管理员 ---------- */
      {
        path: 'checkin',
        name: 'checkin',
        component: () => import('@/views/admin/CheckIn.vue'),
        meta: { title: '入住登记', roles: [1] }
      },
      {
        path: 'customers',
        name: 'customers',
        component: () => import('@/views/admin/CustomerManage.vue'),
        meta: { title: '客户管理', roles: [1] }
      },
      {
        path: 'bed-swap',
        name: 'bed-swap',
        component: () => import('@/views/admin/BedSwap.vue'),
        meta: { title: '床位调换', roles: [1] }
      },
      {
        path: 'backdown-audit',
        name: 'backdown-audit',
        component: () => import('@/views/admin/BackdownAudit.vue'),
        meta: { title: '退住审核', roles: [1] }
      },
      {
        path: 'outward-audit',
        name: 'outward-audit',
        component: () => import('@/views/admin/OutwardAudit.vue'),
        meta: { title: '外出审核', roles: [1] }
      },
      {
        path: 'nurse/level',
        name: 'nurse-level',
        component: () => import('@/views/admin/nurse/NurseLevel.vue'),
        meta: { title: '护理级别', roles: [1] }
      },
      {
        path: 'nurse/content',
        name: 'nurse-content',
        component: () => import('@/views/admin/nurse/NurseContent.vue'),
        meta: { title: '护理项目', roles: [1] }
      },
      {
        path: 'nurse/setup',
        name: 'nurse-setup',
        component: () => import('@/views/admin/nurse/CustomerNurseSetup.vue'),
        meta: { title: '客户护理设置', roles: [1] }
      },
      {
        path: 'nurse/record',
        name: 'nurse-record-admin',
        component: () => import('@/views/admin/nurse/NurseRecordAdmin.vue'),
        meta: { title: '护理记录', roles: [1] }
      },
      {
        path: 'butler',
        name: 'butler',
        component: () => import('@/views/admin/ButlerManage.vue'),
        meta: { title: '健康管家', roles: [1] }
      },
      {
        path: 'service-monitor',
        name: 'service-monitor',
        component: () => import('@/views/admin/ServiceMonitor.vue'),
        meta: { title: '服务关注', roles: [1] }
      },
      {
        path: 'bed-manage',
        name: 'bed-manage',
        component: () => import('@/views/admin/BedManage.vue'),
        meta: { title: '床位管理', roles: [1] }
      },
      {
        path: 'users',
        name: 'users',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', roles: [1] }
      },
      /* ---------- 健康管家 ---------- */
      {
        path: 'daily-nurse',
        name: 'daily-nurse',
        component: () => import('@/views/butler/DailyNurse.vue'),
        meta: { title: '日常护理', roles: [2] }
      },
      {
        path: 'my-records',
        name: 'my-records',
        component: () => import('@/views/butler/MyNurseRecord.vue'),
        meta: { title: '服务对象护理记录', roles: [2] }
      },
      {
        path: 'backdown-apply',
        name: 'backdown-apply',
        component: () => import('@/views/butler/BackdownApply.vue'),
        meta: { title: '退住申请', roles: [2] }
      },
      {
        path: 'outward-apply',
        name: 'outward-apply',
        component: () => import('@/views/butler/OutwardApply.vue'),
        meta: { title: '外出申请', roles: [2] }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const store = useUserStore()

  if (to.meta.public) {
    // 已登录访问登录页 -> 回首页
    return store.isLogin ? next('/') : next()
  }
  if (!store.isLogin) {
    return next('/login')
  }
  // 角色权限校验
  const roles = to.meta.roles
  if (roles && roles.length && !roles.includes(store.roleId)) {
    return next('/bed-schematic')
  }
  next()
})

export default router
