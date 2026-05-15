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
        </div>
      </template>

      <el-table :data="courseList" style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="sectionCode" label="教学班编号" min-width="120" />
        <el-table-column prop="teacherName" label="任课教师" width="110" />
        <el-table-column prop="credit" label="学分" width="70" align="center" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="selectTime" label="选课时间" min-width="150" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row._status === 1 ? 'success' : 'info'" size="small">
              {{ row._status === 1 ? '在读' : '已退课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row._status === 1"
              type="danger"
              link
              @click="handleDropCourse(row)"
            >退课</el-button>
            <span v-else class="text-placeholder">—</span>
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
import { enrollmentApi } from '@/api/new-api'
import './style.scss'

// 统计数据
const stats = reactive({
  totalCourses: 0,
  totalCredits: 0,
  weeklyHours: 0
})

const courseList = ref([])
const loading = ref(false)

// 获取我的课程（直接使用新版 enrollment 接口）
const fetchCourses = async () => {
  try {
    loading.value = true
    const studentId = localStorage.getItem('studentId')
    if (!studentId) { ElMessage.error('未获取到学生信息，请重新登录'); return }
    const res = await enrollmentApi.getMyEnrollments(studentId)
    if (res && (res.code === 200 || res.status === 200)) {
      const data = res.data?.list || res.data || []
      courseList.value = data.map(e => ({
        enrollmentId: e.enrollmentId,
        sectionId: e.sectionId,
        courseName: e.courseName,
        sectionCode: e.sectionCode,
        semester: e.semester,
        credit: e.credit,
        teacherName: e.teacherName,
        selectTime: e.selectTime,
        _status: e.status
      }))
      stats.totalCourses = data.length
      stats.totalCredits = data.reduce((sum, e) => sum + (e.credit || 0), 0)
      stats.weeklyHours = data.length * 2
    } else {
      ElMessage.warning(res?.msg || res?.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 退课
const handleDropCourse = async (course) => {
  if (!course.enrollmentId) { ElMessage.error('缺少选课记录ID'); return }
  try {
    await ElMessageBox.confirm(`确定要退选"${course.courseName}（${course.sectionCode}）"吗？`, '退课确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    const res = await enrollmentApi.drop(course.enrollmentId)
    if (res && (res.code === 200 || res.status === 200)) {
      ElMessage.success(`已退选：${course.courseName}`)
      fetchCourses()
    } else {
      ElMessage.error(res?.msg || res?.message || '退课失败')
    }
  } catch (error) {
    if (error !== 'cancel') { console.error('退课失败:', error); ElMessage.error('退课失败，请重试') }
  }
}

onMounted(() => { fetchCourses() })
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