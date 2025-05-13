<template>
  <div class="home-container">
    <!-- 左侧影视列表区域 -->
    <div class="main-content">
      <div class="card search-bar">
        <el-input v-model="data.name" placeholder="请输入电影名称查询" style="width: 300px; margin-right: 10px"></el-input>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="info" @click="reset">重置</el-button>
      </div>

      <div class="card category-bar">
        <el-button :class="{ 'film-active' : data.categoryId === null }" @click="loadFilmByCategory(null)">全部</el-button>
        <el-button :class="{ 'film-active' : data.categoryId === item.id }"
                   v-for="item in data.categoryList"
                   :key="item.id"
                   @click="loadFilmByCategory(item.id)">
          {{ item.name }}
        </el-button>
      </div>

      <div class="card film-grid">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in data.tableData"
                  :key="item.id"
                  class="film-item"
                  @click="goDetail(item.id)">
            <div class="film-card">
              <div class="film-poster">
                <img :src="item.cover" alt="" class="film-image">
              </div>
              <div class="film-info">
                <div class="film-title line1">{{ item.name }}</div>
                <div class="film-rating">
                  <el-rate v-model="item.score" disabled allow-half
                           style="--el-rate-icon-size: 14px"
                           show-score score-template="{value}分"></el-rate>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="card">
        <el-pagination background
                       layout="total, prev, pager, next"
                       v-model:current-page="data.pageNum"
                       v-model:page-size="data.pageSize"
                       :total="data.total"
                       @current-change="load" />
      </div>
    </div>

    <!-- 右侧边栏区域 -->
    <div class="sidebar" v-if="data.eventList.length > 0">
      <!-- 最新活动 -->
      <div class="card">
        <div class="sidebar-title">最新活动</div>
        <el-timeline>
          <el-timeline-item v-for="item in data.eventList"
                            :key="item.id"
                            :timestamp="item.time"
                            placement="top">
            <div class="event-title" @click="goEventDetail(item.id)">{{ item.title }}</div>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 电影评分榜 -->
      <div class="card">
        <div class="sidebar-title">影视评分榜</div>
        <div v-for="(item, index) in data.rankingList"
             :key="item.id"
             @click="goDetail(item.id)"
             class="ranking-item">
          <div class="ranking-index">
            <div v-if="index === 0" class="rank-1">1</div>
            <div v-else-if="index === 1" class="rank-2">2</div>
            <div v-else-if="index === 2" class="rank-3">3</div>
            <div v-else class="rank-other">{{ index + 1 }}</div>
          </div>
          <img :src="item.cover" alt="" class="ranking-img">
          <div class="ranking-info">
            <div class="ranking-name line2">{{ item.name }}</div>
            <el-rate v-model="item.score" disabled allow-half show-score score-template="{value}分"
                     style="--el-rate-icon-size: 12px"></el-rate>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request";
import { useRouter } from 'vue-router';

const router = useRouter();

const data = reactive({
  name: null,
  pageNum: 1,
  pageSize: 8,
  tableData: [],
  total: 0,
  categoryList: [],
  categoryId: null,
  eventList: [],
  rankingList: []
})

// 核心方法需要先定义
const load = () => {
  request.get('/film/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
      categoryId: data.categoryId
    }
  }).then(res => {
    data.tableData = res.data.list
    data.total = res.data.total
  })
}

const loadAllData = () => {
  loadCategory()
  loadEvent()
  loadRanking()
  load()
}

const loadCategory = () => {
  request.get('/category/selectAll').then(res => {
    data.categoryList = res.data
  })
}

const loadEvent = () => {
  request.get('/event/selectAll').then(res => {
    data.eventList = res.data
  })
}

const loadRanking = () => {
  request.get('/film/selectRanking').then(res => {
    data.rankingList = res.data
  })
}

const goDetail = (id) => {
  router.push('/filmDetail?id=' + id)
}

const reset = () => {
  data.name = null
  load()
}

const loadFilmByCategory = (categoryId) => {
  data.categoryId = categoryId
  load()
}

const goEventDetail = (id) => {
  console.log('正在跳转到活动详情，ID:', id);
  router.push('eventDetail?id=' + id)
}

// 初始化加载
loadAllData()
</script>

<style scoped>
.home-container {
  display: flex;
  gap: 20px;
  padding: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.sidebar {
  width: 320px;
  flex-shrink: 0;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.film-active {
  background-color: #1967e3;
  color: white;
}

.film-grid {
  margin-bottom: 20px;
}

.film-item {
  margin-bottom: 20px;
}

.film-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.film-card:hover {
  transform: translateY(-5px);
}

.film-poster {
  position: relative;
  padding-top: 150%; /* 保持2:3的宽高比 */
  overflow: hidden;
}

.film-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.film-info {
  padding: 12px;
}

.film-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.film-rating {
  display: flex;
  align-items: center;
}

.sidebar-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.event-title {
  font-size: 14px;
  color: #1967e3;
  cursor: pointer;
  margin-bottom: 5px;
}

.event-title:hover {
  text-decoration: underline;
}

.ranking-item {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 15px;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.ranking-item:hover {
  background-color: #f5f7fa;
}

.ranking-index {
  width: 24px;
  text-align: center;
  font-weight: bold;
}

.rank-1 { color: gold; }
.rank-2 { color: silver; }
.rank-3 { color: #cd7f32; }
.rank-other { color: #666; }

.ranking-img {
  width: 60px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.ranking-info {
  flex: 1;
  min-width: 0;
}

.ranking-name {
  font-size: 14px;
  margin-bottom: 5px;
}

.line1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.line2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 响应式布局 */
@media screen and (max-width: 1200px) {
  .home-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .el-col {
    width: 33.33% !important;
  }
}

@media screen and (max-width: 900px) {
  .el-col {
    width: 50% !important;
  }
}

@media screen and (max-width: 600px) {
  .el-col {
    width: 100% !important;
  }
  
  .search-bar .el-input {
    width: 100% !important;
    margin-right: 0 !important;
  }
}
</style>
