<template>
  <div class="my-courses">
    <!-- 课程统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCourses }}</div>
              <div class="stat-label">已选课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCredits }}</div>
              <div class="stat-label">总学分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.weeklyHours }}</div>
              <div class="stat-label">周课时</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程列表 -->
    <el-card class="course-list" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <el-radio-group v-model="currentTerm" size="small" @change="handleTermChange">
            <el-radio-button label="2023-1">2023秋季</el-radio-button>
            <el-radio-button label="2023-2">2024春季</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="courseList" style="width: 100%">
        <el-table-column type="expand">
          <template #default="props">
            <div class="course-detail">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="课程代码">{{ props.row.id }}</el-descriptions-item>
                <el-descriptions-item label="课程名称">{{ props.row.name }}</el-descriptions-item>
                <el-descriptions-item label="任课教师">{{ props.row.teacherName }}</el-descriptions-item>
                <el-descriptions-item label="学分">{{ props.row.credit }}</el-descriptions-item>
                <el-descriptions-item label="开课学期">{{ props.row.term }}</el-descriptions-item>
                <el-descriptions-item label="课程状态">
                  <el-tag :type="props.row.status === 'active' ? 'success' : 'info'">
                    {{ props.row.status === 'active' ? '进行中' : '未开课' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="teacherName" label="任课教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column label="成绩" width="100" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row.score)">{{ row.score || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '进行中' : '未开课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDropCourse(row)">
              退课
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Collection, Clock } from '@element-plus/icons-vue'
import { studentApi } from '@/api/student'
import './style.scss'

// 统计数据
const stats = reactive({
  totalCourses: 0,
  totalCredits: 0,
  weeklyHours: 0
})

// 课程列表
const courseList = ref([])
const loading = ref(false)
const currentTerm = ref('2023-1')

// 获取课程列表
const fetchCourses = async () => {
  try {
    loading.value = true
    const studentId = localStorage.getItem('uid')
    const res = await studentApi.getScoreByStudentId(studentId)
    
    if (res.data) {
      // 获取课程详细信息
      const coursePromises = res.data.map(score => 
        studentApi.getCourseById(score.courseId)
      )
      const courseResults = await Promise.all(coursePromises)
      
      // 合并课程信息和成绩信息
      courseList.value = courseResults.map((courseRes, index) => ({
        ...courseRes.data,
        score: res.data[index].score,
        status: 'active' // TODO: 根据实际情况设置状态
      }))

      // 更新统计信息
      stats.totalCourses = courseList.value.length
      stats.totalCredits = courseList.value.reduce((sum, course) => sum + course.credit, 0)
      stats.weeklyHours = courseList.value.length * 2 // 假设每门课每周2学时
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 处理学期切换
const handleTermChange = (term) => {
  fetchCourses()
}

// 处理退课
const handleDropCourse = async (course) => {
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

    ElMessage.success(`已退选课程：${course.name}`)
    fetchCourses() // 刷新课程列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退课失败:', error)
      ElMessage.error('退课失败，请重试')
    }
  }
}

// 获取成绩样式
const getScoreClass = (score) => {
  if (!score) return ''
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

// 初始化
onMounted(() => {
  fetchCourses()
})
</script>

<style lang="scss" scoped>
.my-courses {
  .stat-cards {
    margin-bottom: 20px;
  }
  
  .stat-card {
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    
    .stat-content {
      display: flex;
      align-items: center;
      padding: 20px;
      
      .stat-icon {
        font-size: 48px;
        margin-right: 20px;
        color: var(--el-color-primary);
        
        .el-icon {
          background: linear-gradient(45deg, var(--el-color-primary), var(--el-color-success));
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: var(--el-text-color-primary);
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }
  
  .course-list {
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .schedule-item {
      &:not(:last-child) {
        margin-bottom: 5px;
      }
    }
    
    .course-detail {
      padding: 20px;
      background: rgba(255, 255, 255, 0.02);
    }
  }
  
  :deep(.el-table) {
    background-color: transparent;
    
    th, td {
      background-color: transparent;
    }
    
    th {
      color: var(--el-text-color-regular);
    }
    
    td {
      color: var(--el-text-color-primary);
    }
  }
}

.materials-dialog {
  .reference-list {
    .reference-item {
      padding: 15px;
      border-bottom: 1px solid var(--el-border-color-lighter);
      
      &:last-child {
        border-bottom: none;
      }
      
      .ref-title {
        font-weight: bold;
        margin-bottom: 5px;
      }
      
      .ref-desc {
        color: var(--el-text-color-regular);
        margin-bottom: 10px;
      }
    }
  }
}
</style> 