<template>
  <div class="film-container">
    <div class="card search-bar">
      <el-input v-model="data.name" placeholder="请输入电影名称查询" style="width: 300px; margin-right: 10px"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
    </div>

    <div class="card category-bar">
      <el-button :class="{ 'film-active' : data.categoryId === null }" @click="loadFilmByCategory(null)">全部</el-button>
      <el-button :class="{ 'film-active' : data.categoryId === item.id }" v-for="item in data.categoryList" :key="item.id" @click="loadFilmByCategory(item.id)">{{ item.name }}</el-button>
    </div>

    <div class="card film-grid">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in data.tableData" :key="item.id" class="film-item" @click="goDetail(item.id)">
          <div class="film-card">
            <img :src="item.cover" alt="" class="film-image">
            <div class="film-info">
              <div class="film-title line1">{{ item.name }}</div>
              <div class="film-rating">
                <el-rate v-model="item.score" disabled allow-half show-score></el-rate>
                <div class="comment-count">{{ item.commentNum }}人评论</div>
              </div>
              <div class="film-desc line3">{{ item.descr }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="card">
      <el-pagination 
        background 
        layout="total, prev, pager, next" 
        v-model:current-page="data.pageNum" 
        v-model:page-size="data.pageSize"
        :total="data.total" 
        @current-change="load" />
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request";

const data = reactive({
  name: null,
  pageNum: 1,
  pageSize: 8,
  tableData: [],
  total: 0,
  categoryList: [],
  categoryId: null
})

const goDetail = (id) => {
  location.href = '/filmDetail?id=' + id
}

// 查询所有电影分类信息
const loadCategory = () => {
  request.get('/category/selectAll').then(res => {
    data.categoryList = res.data
  })
}
loadCategory()

// 根据分类查询电影列表
const loadFilmByCategory = (categoryId) => {
  data.categoryId = categoryId
  load()
}

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
load()

const reset = () => {
  data.name = null
  load()
}
</script>

<style scoped>
.film-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 15px;
}

.search-bar {
  margin-bottom: 15px;
}

.category-bar {
  margin-bottom: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.film-active {
  background-color: #1967e3;
  color: white;
}

.film-grid {
  margin-bottom: 15px;
}

.film-item {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.film-item:hover {
  transform: translateY(-5px);
}

.film-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.film-image {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.film-info {
  padding: 12px;
}

.film-title {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.film-rating {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-count {
  color: #1967e3;
  font-size: 14px;
}

.film-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.line1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.line3 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

/* 响应式布局调整 */
@media screen and (max-width: 768px) {
  .film-container {
    padding: 0 10px;
  }
  
  .search-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .search-bar .el-input {
    width: 100% !important;
    margin-right: 0 !important;
  }
}
</style>