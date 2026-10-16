/**
 * 侧边栏菜单定义（roles: 1=管理员 2=健康管家），
 * 与后端 Controller / 路由一一对应。
 */
export const menuTree = [
  { path: '/bed-schematic', title: '床位示意图', icon: 'Grid', roles: [1, 2] },

  { path: '/checkin', title: '入住登记', icon: 'CirclePlus', roles: [1] },
  { path: '/customers', title: '客户管理', icon: 'Postcard', roles: [1] },
  { path: '/bed-swap', title: '床位调换', icon: 'Sort', roles: [1] },
  { path: '/backdown-audit', title: '退住审核', icon: 'DocumentChecked', roles: [1] },
  { path: '/outward-audit', title: '外出审核', icon: 'DocumentChecked', roles: [1] },
  {
    title: '护理管理',
    icon: 'FirstAidKit',
    roles: [1],
    children: [
      { path: '/nurse/level', title: '护理级别', roles: [1] },
      { path: '/nurse/content', title: '护理项目', roles: [1] },
      { path: '/nurse/setup', title: '客户护理设置', roles: [1] },
      { path: '/nurse/record', title: '护理记录', roles: [1] }
    ]
  },
  { path: '/butler', title: '健康管家', icon: 'UserFilled', roles: [1] },
  { path: '/service-monitor', title: '服务关注', icon: 'View', roles: [1] },
  { path: '/bed-manage', title: '床位管理', icon: 'OfficeBuilding', roles: [1] },
  { path: '/users', title: '用户管理', icon: 'User', roles: [1] },

  { path: '/daily-nurse', title: '日常护理', icon: 'FirstAidKit', roles: [2] },
  { path: '/my-records', title: '服务对象护理记录', icon: 'Tickets', roles: [2] },
  { path: '/backdown-apply', title: '退住申请', icon: 'Document', roles: [2] },
  { path: '/outward-apply', title: '外出申请', icon: 'Van', roles: [2] }
]

/** 按角色过滤菜单 */
export function menusByRole(roleId) {
  return menuTree
    .filter((m) => m.roles.includes(roleId))
    .map((m) => {
      if (!m.children) return m
      return { ...m, children: m.children.filter((c) => c.roles.includes(roleId)) }
    })
}
