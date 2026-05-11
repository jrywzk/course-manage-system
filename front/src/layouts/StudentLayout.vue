<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="sidebar-logo">
        <AppLogo small />
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="transparent"
        text-color="var(--el-text-color-primary)"
        active-text-color="var(--el-color-primary)"
        router
      >
        <el-menu-item index="/student/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student/course-selection">
          <el-icon><List /></el-icon>
          <span>选课中心</span>
        </el-menu-item>
        <el-menu-item index="/student/my-courses">
          <el-icon><Collection /></el-icon>
          <span>我的课程</span>
        </el-menu-item>
        <el-menu-item index="/student/grades">
          <el-icon><Document /></el-icon>
          <span>成绩查询</span>
        </el-menu-item>
        <el-menu-item index="/student/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>{{ currentRoute.meta.title }}</h2>
          <el-dropdown @command="handleCommand">
            <el-avatar :size="32" icon="UserFilled" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled, List, Collection, Document, User } from '@element-plus/icons-vue'
import AppLogo from '@/components/AppLogo.vue'

const route = useRoute()
const router = useRouter()

const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('uid')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/student/profile')
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  gap: 8px;
  padding: 8px 8px 0 0;
}

.sidebar-logo {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 18px 0 10px;
  border-bottom: 1px solid var(--border-color);
}

.side-menu {
  height: calc(100% - 90px);
  border-right: 1px solid var(--border-color);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
  border-bottom: 1px solid var(--border-color);
}

.user-info {
  .user-dropdown {
    color: var(--primary-text);
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 5px;
  }
}
</style> 