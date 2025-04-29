<template>
  <div>
    <div style="height: 60px; background-color: #fff; display: flex; align-items: center; border-bottom: 1px solid #ddd">
      <div style="flex: 1; display: flex; align-items: center">
        <div style="padding-left: 20px; display: flex; align-items: center">
          <img src="@/assets/imgs/logo1.png" alt="" style="width: 40px">
          <div style="font-weight: bold; font-size: 24px; margin-left: 5px; color:black">忆影校园影视交流平台</div>
        </div>
        <div style="margin-left: 30px; display: flex; align-items: center">
          <div class="nav-item" @click="router.push('/home')">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </div>
          <div class="nav-item" @click="router.push('/comment')">
            <el-icon><Comment /></el-icon>
            <span>评论</span>
          </div>
        </div>
      </div>
      <div style="width: fit-content; padding-right: 10px; display: flex; align-items: center;">
        <el-dropdown trigger="click" style="position: relative;">
         <span class="el-dropdown-link" style="display: flex; align-items: center;">
          <img style="width: 40px; height: 40px; border-radius: 50%" :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="">
          <span style="margin-left: 5px">{{ data.user.name }}</span>
         </span>
          <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToProfile">个人资料</el-dropdown-item>
            <el-dropdown-item @click="goToChangePassword">修改密码</el-dropdown-item>
            <el-dropdown-item @click="logout">退出系统</el-dropdown-item>
          </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div style="display: flex">
      <!-- 只有管理员才能看到左侧栏 -->
      <div v-if="data.user.role === 'ADMIN'" style="width: 200px; border-right: 1px solid #ddd; min-height: calc(100vh - 60px)">
        <el-menu
            :default-active="router.currentRoute.value.path"
            :default-openeds="['1', '2']"
            router
            style="border: none"
        >
          <el-sub-menu index="1">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>信息管理</span>
            </template>
            <el-menu-item index="/category">
              <el-icon><Grid /></el-icon>
              <span>影视分类信息</span>
            </el-menu-item>
            <el-menu-item index="/film">
              <el-icon><Film /></el-icon>
              <span>影视信息</span>
            </el-menu-item>
            <el-menu-item index="/event">
              <el-icon><Bell /></el-icon>
              <span>活动信息</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Memo /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin">
              <el-icon><User /></el-icon>
              <span>管理员信息</span>
            </el-menu-item>
            <el-menu-item index="/user">
              <el-icon><User /></el-icon>
              <span>普通用户信息</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <!-- 内容区域 -->
      <div :style="{
        'flex': 1,
        'width': data.user.role === 'ADMIN' ? '0' : '100%',
        'background-color': '#f8f8ff',
        'padding': '10px'
      }">
        <router-view @updateUser="updateUser" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, onMounted, onBeforeUnmount } from "vue";
import router from "@/router";
import {ElMessage,ElDropdown, ElDropdownMenu, ElDropdownItem} from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}')
})

onMounted(() => {
  checkLogin()
})

// 添加组件卸载前的清理
onBeforeUnmount(() => {
  // 清理可能的副作用
})

const checkLogin = () => {
  if (!data.user?.id) {
    ElMessage.error('请登录！')
    router.push('/login')
  }
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('system-user') || '{}')
  checkLogin()
}

const logout = async () => {
  try {
    ElMessage.success('退出成功')
    localStorage.removeItem('system-user')
    await router.push('/login')
  } catch (error) {
    console.error('导航错误：', error)
  }
}

const goToProfile = async () => {
  try {
    await router.push('/person')
  } catch (error) {
    console.error('导航错误：', error)
  }
}

const goToChangePassword = async () => {
  try {
    await router.push('/password')
  } catch (error) {
    console.error('导航错误：', error)
  }
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: #e0edfd !important;
}
.el-menu-item:hover {
  color: #1967e3;
}
:deep(th)  {
  color: #333;
}
.nav-item {
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
}
.nav-item:hover {
  color: #409EFF;
}
.nav-item .el-icon {
  margin-right: 5px;
}
</style>