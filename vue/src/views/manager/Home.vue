<template>
  <div style="display: flex; gap: 20px; align-items: flex-start">
    <!-- 左侧影视列表区域 -->
    <div style="flex: 3">
      <div class="card" style="margin-bottom: 10px">
        <el-input v-model="data.name" placeholder="请输入电影名称查询" style="width: 300px; margin-right: 10px"></el-input>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="info" @click="reset">重置</el-button>
      </div>

      <div class="card" style="margin-bottom: 10px">
        <el-button :class="{ 'film-active' : data.categoryId === null }" @click="loadFilmByCategory(null)">全部</el-button>
        <el-button :class="{ 'film-active' : data.categoryId === item.id }"
                   v-for="item in data.categoryList"
                   :key="item.id"
                   @click="loadFilmByCategory(item.id)">
          {{ item.name }}
        </el-button>
      </div>

      <div class="card" style="margin-bottom: 10px">
        <el-row :gutter="15">
          <el-col :span="6" v-for="item in data.tableData"
                  :key="item.id"
                  style="margin-bottom: 15px; cursor: pointer"
                  @click="goDetail(item.id)">
            <img :src="item.cover" alt="" style="width: 100%; height: 280px; border-radius: 5px">
            <div style="margin: 5px 0; font-size: 16px" class="line1">{{ item.name }}</div>
            <div style="margin: 5px 0; display: flex; align-items: center; gap:3px">
              <el-rate v-model="item.score" disabled allow-half
                       style="--el-rate-icon-size: 14px"
                       show-score score-template="{value}分"></el-rate>
<!--              <div style="flex: 1; text-align: right; color: #1967e3">{{ item.commentNum }}人评论</div>-->
            </div>
<!--            <div style="font-size: 13px; color: #666" class="line3">{{ item.descr }}</div>-->
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
    <div style="flex: 1">
      <!-- 最新活动 -->
      <div class="card" style="margin-bottom: 10px">
        <div style="margin-bottom: 15px; font-size: 18px; font-weight: bold">最新活动</div>
        <el-timeline>
          <el-timeline-item v-for="item in data.eventList"
                            :key="item.id"
                            :timestamp="item.time"
                            placement="top"
                            style="padding-left: 15px">
            <div style="font-size: 14px; margin-bottom: 5px; color: #1967e3; cursor: pointer" 
                 @click="goEventDetail(item.id)">{{ item.title }}</div>
            <!-- <div style="color: #666; font-size: 13px">{{ item.content }}</div> -->
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 电影评分榜 -->
      <div class="card">
        <div style="margin-bottom: 15px; font-size: 18px; font-weight: bold">影视评分榜</div>
       <div v-for="(item, index) in data.rankingList"
             :key="item.id"
             @click="goDetail(item.id)"
             style="cursor: pointer; margin-bottom: 15px">
          <div style="display: flex; gap: 10px; align-items: center">
            <div style="width: 30px; text-align: center">
              <div v-if="index === 0" style="color: gold; font-weight: bold">1</div>
              <div v-else-if="index === 1" style="color: silver; font-weight: bold">2</div>
              <div v-else-if="index === 2" style="color: chocolate; font-weight: bold">3</div>
              <div v-else style="color: #666">{{ index + 1 }}</div>
            </div>
            <img :src="item.cover" alt="" style="width: 60px; height: 80px; border-radius: 4px">
            <div style="flex: 1">
              <div style="font-size: 14px; margin-bottom: 5px" class="line1">{{ item.name }}</div>
              <el-rate v-model="item.score" disabled allow-half show-score score-template="{value}分"
                       style="--el-rate-icon-size: 12px"></el-rate>
            </div>
          </div>
        </div>
      </div>

      <div class="card" style="padding: 20px; margin-bottom: 10px">
        <div style="font-size: 20px; margin-bottom: 20px">系统公告</div>
        <div style="border-bottom: 1px solid #eee; padding: 10px 0; cursor: pointer" 
             v-for="item in data.eventList" 
             :key="item.id"
             @click="goEventDetail(item.id)">
          <div style="display: flex; align-items: center">
            <div style="flex: 1; font-size: 16px">{{ item.title }}</div>
            <div style="color: #666; font-size: 14px">{{ item.time }}</div>
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
/* 保持原有样式 */
.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.film-active {
  background-color: #1967e3;
  color: white;
}

.line1 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.line3 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>
