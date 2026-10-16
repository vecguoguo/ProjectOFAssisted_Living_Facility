<template>
  <div class="page-card" v-loading="loading">
    <div class="page-toolbar">
      <el-tag type="success" effect="dark">空闲</el-tag>
      <el-tag type="warning" effect="dark">有人</el-tag>
      <el-tag type="info" effect="dark">外出</el-tag>
      <div class="spacer" />
      <span class="text-muted">共 {{ beds.length }} 张床位</span>
      <el-button type="primary" plain @click="load">
        <el-icon><Refresh /></el-icon>
        <span>刷新</span>
      </el-button>
    </div>

    <div v-for="(list, room) in rooms" :key="room" class="room">
      <div class="room-title">{{ room }} 房间</div>
      <div class="bed-list">
        <div v-for="b in list" :key="b.id" class="bed" :class="'status-' + b.bedStatus">
          <div class="bed-no">{{ b.roomNo }}-{{ b.bedNo }}</div>
          <div class="bed-status">{{ statusText(b.bedStatus) }}</div>
          <div class="bed-remark">{{ b.remarks || '—' }}</div>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && !beds.length" description="暂无床位数据" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { bedApi } from '@/api'

const loading = ref(false)
const beds = ref([])

const statusText = (s) => ({ 1: '空闲', 2: '有人', 3: '外出' }[s] || '未知')

const rooms = computed(() => {
  const map = {}
  beds.value.forEach((b) => {
    const key = b.roomNo
    if (!map[key]) map[key] = []
    map[key].push(b)
  })
  return map
})

const load = async () => {
  loading.value = true
  try {
    beds.value = await bedApi.listAll()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.room {
  margin-bottom: 18px;
}

.room-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 10px;
  padding-left: 8px;
  border-left: 4px solid #1f4e79;
}

.bed-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.bed {
  width: 150px;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  background: #fff;
  transition: transform 0.15s;
}

.bed:hover {
  transform: translateY(-2px);
}

.bed-no {
  font-size: 16px;
  font-weight: 600;
}

.bed-status {
  margin-top: 4px;
  font-size: 13px;
}

.bed-remark {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  min-height: 18px;
}

.status-1 {
  border-color: #67c23a;
  background: #f0f9eb;
}

.status-1 .bed-status {
  color: #67c23a;
}

.status-2 {
  border-color: #e6a23c;
  background: #fdf6ec;
}

.status-2 .bed-status {
  color: #e6a23c;
}

.status-3 {
  border-color: #909399;
  background: #f4f4f5;
}

.status-3 .bed-status {
  color: #909399;
}
</style>
