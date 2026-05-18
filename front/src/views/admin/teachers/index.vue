<template>
  <div class="teachers-container">
    <div class="page-header"><h2>教师管理</h2></div>
    <div class="action-bar">
      <el-input v-model="searchKeyword" placeholder="搜索教师姓名" style="width: 240px" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>添加教师
      </el-button>
    </div>
    <el-card class="data-card" v-loading="loading">
      <el-table :data="[]" style="width: 100%" empty-text="教师管理接口待C补充（admin/teachers CRUD），当前仅展示占位结构">
        <el-table-column prop="teacherId" label="工号" min-width="120" align="center" />
        <el-table-column prop="teacherName" label="姓名" min-width="200" />
        <el-table-column label="操作" min-width="160" fixed="right" align="center">
          <template #default><el-button type="primary" link>编辑</el-button><el-button type="danger" link>停用</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import './style.scss'
const searchKeyword = ref('')
const loading = ref(false)
</script>

<style lang="scss" scoped>
@use './style.scss';
.teachers-container { padding: 24px; }
.page-header { margin-bottom: 20px; h2 { margin: 0; font-size: 20px; font-weight: 600; color: var(--el-text-color-primary); } }
.action-bar { display: flex; gap: 12px; align-items: center; margin-bottom: 16px; }
.data-card { border: 1px solid var(--el-border-color-darker); }
</style>
