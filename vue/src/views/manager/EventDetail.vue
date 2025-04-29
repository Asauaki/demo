<template>
  <div>
    <div style="display: flex; align-items: flex-start; grid-gap: 10px">
      <div style="flex: 1; width: 0">
        <div class="card" style="padding: 20px; margin-bottom: 10px">
          <div class="image-container">
            <img src="@/assets/imgs/gyk.jpg" alt="活动图片" class="event-image">
          </div>
          
          <div style="margin-bottom: 20px">
            <div style="font-weight: bold; font-size: 24px; margin-bottom: 8px">{{ data.event.title }}</div>
            <div style="color: #666; font-size: 14px; text-align: right">
              <i class="el-icon-time"></i> {{ data.event.time }}
            </div>
          </div>

          <div style="color: #666; line-height: 28px; font-size: 16px; text-align: justify; white-space: pre-wrap">
            {{ data.event.content }}
          </div>

          <div class="navigation-container">
            <div v-if="data.prevEvent" class="nav-item prev" @click="goToEvent(data.prevEvent.id)">
              <el-icon><ArrowLeft /></el-icon>
              <div class="nav-content">
                <div class="nav-label">上一个活动</div>
                <div class="nav-title">{{ data.prevEvent.title }}</div>
              </div>
            </div>
            <div v-if="data.nextEvent" class="nav-item next" @click="goToEvent(data.nextEvent.id)">
              <div class="nav-content">
                <div class="nav-label">下一个活动</div>
                <div class="nav-title">{{ data.nextEvent.title }}</div>
              </div>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <div style="width: 300px; padding: 20px" class="card">
        <div style="font-size: 20px; margin-bottom: 20px">推荐影视</div>
        <div style="margin-bottom: 20px; cursor: pointer" v-for="item in data.recommendList" :key="item.id" @click="goDetail(item.id)">
          <img :src="item.cover" alt="" style="width: 100%; margin-bottom: 5px">
          <div style="font-size: 18px">{{ item.name }}</div>
          <div>
            <el-rate v-model="item.score" disabled allow-half show-score></el-rate>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from "vue";
import router from "@/router";
import request from "@/utils/request";
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const data = reactive({
  id: router.currentRoute.value.query.id,
  event: {},
  recommendList: [],
  prevEvent: null,
  nextEvent: null
})

// 加载所有数据的方法
const loadAllData = () => {
  // 加载活动详情
  request.get('/event/selectById/' + data.id).then(res => {
    data.event = res.data
  })

  // 加载推荐电影
  request.get('/film/selectRecommend/0').then(res => {
    data.recommendList = res.data
  })

  // 加载上一个活动
  request.get('/event/selectPrevious/' + data.id).then(res => {
    data.prevEvent = res.data
  })

  // 加载下一个活动
  request.get('/event/selectNext/' + data.id).then(res => {
    data.nextEvent = res.data
  })
}

// 监听路由参数变化
watch(
  () => router.currentRoute.value.query.id,
  (newId) => {
    if (newId) {
      data.id = newId
      loadAllData()
    }
  }
)

const goDetail = (id) => {
  router.push('filmDetail?id=' + id)
}

const goToEvent = (id) => {
  router.push('eventDetail?id=' + id)
}

// 初始加载数据
loadAllData()
</script>

<style scoped>
.card {
  background: white;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.image-container {
  width: 100%;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.event-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.event-image:hover {
  transform: scale(1.02);
}

.navigation-container {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
  max-width: 45%;
}

.nav-item:hover {
  background-color: #f5f7fa;
}

.nav-content {
  display: flex;
  flex-direction: column;
}

.nav-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.nav-title {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.prev {
  margin-right: auto;
}

.next {
  margin-left: auto;
  text-align: right;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .event-image {
    max-height: 200px;
  }
}

@media screen and (max-width: 480px) {
  .event-image {
    max-height: 150px;
  }
}
</style> 