<template>
  <div class="course-selection">
    <!-- 搜索和筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="课程名称">
          <el-input 
            v-model="filterForm.courseName" 
            placeholder="搜索课程" 
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="教师姓名">
          <el-input 
            v-model="filterForm.teacherName" 
            placeholder="搜索教师" 
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">搜索</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 课程列表 -->
    <el-card class="course-list" v-loading="loading">
      <template #header>
        <div class="list-header">
          <span>可选课程列表</span>
          <div class="selection-info">
            已选：{{ selectedCount }}/{{ maxSelection }} 门
            <el-tooltip content="每学期最多选择6门课程" placement="top">
              <el-icon class="info-icon"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
        </div>
      </template>

      <el-table :data="courseList" style="width: 100%">
        <el-table-column type="expand">
          <template #default="props">
            <div class="course-detail">
              <p><strong>课程代码：</strong>{{ props.row.id }}</p>
              <p><strong>课程名称：</strong>{{ props.row.name }}</p>
              <p><strong>任课教师：</strong>{{ props.row.teacherName }}</p>
              <p><strong>学分：</strong>{{ props.row.credit }}</p>
              <p><strong>课程容量：</strong>{{ props.row.studentLimit }}</p>
              <p><strong>开课学期：</strong>{{ props.row.term }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="课程代码" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="teacherName" label="任课教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="term" label="学期" width="180" />
        <el-table-column prop="studentLimit" label="剩余容量" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getCapacityTagType(row.studentLimit)">
              {{ row.studentLimit }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120" align="center">
          <template #default="{ row }">
            <el-button
              :type="row.selected ? 'danger' : 'primary'"
              :disabled="isSelectionDisabled(row)"
              link
              @click="handleSelection(row)"
            >
              {{ row.selected ? '退选' : '选课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { studentApi } from '@/api/student'
import './style.scss'

// 筛选表单
const filterForm = reactive({
  courseName: '',
  teacherName: ''
})

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 选课限制
const maxSelection = 6
const selectedCount = computed(() => courseList.value.filter(course => course.selected).length)

// 课程列表
const courseList = ref([])

// 获取课程列表
const fetchCourses = async () => {
  try {
    loading.value = true
    const res = await studentApi.getAllCourses()
    if (res.status === 200) {
      courseList.value = res.data
    } else {
      ElMessage.warning(res.msg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索课程
const handleSearch = async () => {
  try {
    loading.value = true
    let results = []
    
    if (filterForm.courseName) {
      const res = await studentApi.getCourseByName(filterForm.courseName)
      results = res.data || []
    } else if (filterForm.teacherName) {
      const res = await studentApi.getCourseByTeacherName(filterForm.teacherName)
      results = res.data || []
    } else {
      await fetchCourses()
      return
    }
    
    courseList.value = results.map(course => ({
      ...course,
      selected: false
    }))
    total.value = results.length
  } catch (error) {
    console.error('搜索课程失败:', error)
    ElMessage.error('搜索课程失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  filterForm.courseName = ''
  filterForm.teacherName = ''
  fetchCourses()
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourses()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCourses()
}

// 获取容量标签类型
const getCapacityTagType = (capacity) => {
  if (capacity > 10) return 'success'
  if (capacity > 0) return 'warning'
  return 'danger'
}

// 判断是否禁用选课按钮
const isSelectionDisabled = (course) => {
  if (course.selected) return false
  if (course.studentLimit === 0) return true
  if (selectedCount.value >= maxSelection) return true
  return false
}

// 处理选课/退课
const handleSelection = async (course) => {
  if (course.selected) {
    // 退课确认
    try {
      await ElMessageBox.confirm(
        `确定要退选课程"${course.name}"吗？`,
        '退课确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      const studentId = localStorage.getItem('uid')
      await studentApi.deleteScore(
        course.id,
        parseInt(studentId),
        course.teacherId
      )

      course.selected = false
      course.studentLimit++
      ElMessage.success(`已退选课程：${course.name}`)
    } catch (error) {
      if (error !== 'cancel') {
        console.error('退课失败:', error)
        ElMessage.error('退课失败，请重试')
      }
    }
  } else {
    if (selectedCount.value >= maxSelection) {
      ElMessage.warning(`每学期最多选择${maxSelection}门课程`)
      return
    }
    
    try {
      const studentId = localStorage.getItem('uid')
      await studentApi.addScore(
        course.id,
        parseInt(studentId),
        course.teacherId
      )

      course.selected = true
      course.studentLimit--
      ElMessage.success(`已选课程：${course.name}`)
    } catch (error) {
      console.error('选课失败:', error)
      ElMessage.error('选课失败，请重试')
    }
  }
}

// 获取已选课程
const fetchSelectedCourses = async () => {
  try {
    const studentId = localStorage.getItem('uid')
    const res = await studentApi.getScoreByStudentId(studentId)
    
    // 标记已选课程
    if (res.data) {
      const selectedCourseIds = res.data.map(score => score.courseId)
      courseList.value = courseList.value.map(course => ({
        ...course,
        selected: selectedCourseIds.includes(course.id)
      }))
    }
  } catch (error) {
    console.error('获取已选课程失败:', error)
  }
}

// 初始化
onMounted(() => {
  fetchCourses()
})
</script> 