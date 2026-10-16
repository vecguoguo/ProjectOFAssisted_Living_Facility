import request from './request'

/** 认证 */
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  me: () => request.get('/auth/me')
}

/** 用户管理 */
export const userApi = {
  list: (params) => request.get('/users', { params }),
  findById: (id) => request.get(`/users/${id}`),
  add: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  resetPassword: (id) => request.put(`/users/${id}/password`),
  remove: (id) => request.delete(`/users/${id}`)
}

/** 床位 */
export const bedApi = {
  listAll: () => request.get('/beds'),
  listFree: (roomNo) => request.get('/beds/free', { params: { roomNo } }),
  query: (params) => request.get('/beds/query', { params }),
  findById: (id) => request.get(`/beds/${id}`),
  swap: (data) => request.put('/beds/swap', data),
  add: (data) => request.post('/beds', data),
  update: (id, data) => request.put(`/beds/${id}`, data),
  remove: (id) => request.delete(`/beds/${id}`)
}

/** 客户 / 入住 */
export const customerApi = {
  listAll: () => request.get('/customers'),
  listMine: () => request.get('/customers/mine'),
  listByButler: (butlerId) => request.get('/customers/by-butler', { params: { butlerId } }),
  listWithoutButler: (nameLike) => request.get('/customers/without-butler', { params: { nameLike } }),
  findById: (id) => request.get(`/customers/${id}`),
  checkIn: (data) => request.post('/customers/checkin', data),
  update: (id, data) => request.put(`/customers/${id}`, data),
  remove: (id) => request.delete(`/customers/${id}`),
  assignButler: (id, butlerId) => request.put(`/customers/${id}/butler`, { butlerId }),
  removeButler: (id) => request.delete(`/customers/${id}/butler`)
}

/** 客户护理（级别设置 / 购买项目 / 日常护理 / 服务关注） */
export const customerNurseApi = {
  listCustomers: (nameLike) => request.get('/customer-nurse/customers', { params: { nameLike } }),
  listMyCustomers: (nameLike) => request.get('/customer-nurse/my-customers', { params: { nameLike } }),
  listButlers: (nameLike) => request.get('/customer-nurse/butlers', { params: { nameLike } }),
  setLevel: (customerId, levelId) => request.post('/customer-nurse/level', { customerId, levelId }),
  removeLevel: (customerId) => request.delete(`/customer-nurse/level/${customerId}`),
  listCustomerItems: (customerId) => request.get(`/customer-nurse/items/${customerId}`),
  listAvailableItems: (customerId, nameLike) =>
    request.get('/customer-nurse/available-items', { params: { customerId, nameLike } }),
  purchase: (data) => request.post('/customer-nurse/purchase', data),
  renew: (data) => request.put('/customer-nurse/item/renew', data),
  removeItem: (customerNurseItemId) => request.delete(`/customer-nurse/item/${customerNurseItemId}`),
  doDailyNurse: (data) => request.post('/customer-nurse/daily', data)
}

/** 护理级别 */
export const nurseLevelApi = {
  list: (status) => request.get('/nurse-levels', { params: { status } }),
  findById: (id) => request.get(`/nurse-levels/${id}`),
  add: (data) => request.post('/nurse-levels', data),
  updateStatus: (id, status) => request.put(`/nurse-levels/${id}/status`, { status }),
  listItemsInLevel: (id) => request.get(`/nurse-levels/${id}/items`),
  listItemsToAdd: (id) => request.get(`/nurse-levels/${id}/items-to-add`),
  addItem: (id, itemId) => request.post(`/nurse-levels/${id}/items`, { itemId }),
  removeItem: (id, itemId) => request.delete(`/nurse-levels/${id}/items/${itemId}`)
}

/** 护理项目 */
export const nurseContentApi = {
  list: (params) => request.get('/nurse-contents', { params }),
  add: (data) => request.post('/nurse-contents', data),
  update: (id, data) => request.put(`/nurse-contents/${id}`, data),
  remove: (id) => request.delete(`/nurse-contents/${id}`)
}

/** 护理记录 */
export const nurseRecordApi = {
  listByCustomer: (customerId) => request.get('/nurse-records', { params: { customerId } }),
  remove: (id) => request.delete(`/nurse-records/${id}`)
}

/** 外出 */
export const outwardApi = {
  list: (params) => request.get('/outwards', { params }),
  submit: (data) => request.post('/outwards', data),
  audit: (id, agree) => request.put(`/outwards/${id}/audit`, { agree }),
  registerReturn: (id, actualReturnTime) => request.put(`/outwards/${id}/return`, { actualReturnTime })
}

/** 退住 */
export const backdownApi = {
  list: (params) => request.get('/backdowns', { params }),
  submit: (data) => request.post('/backdowns', data),
  audit: (id, agree) => request.put(`/backdowns/${id}/audit`, { agree })
}
