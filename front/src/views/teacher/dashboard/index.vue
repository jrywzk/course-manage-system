<template>
  <div class="dashboard-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="dashboard-cards">
      <el-col :span="8">
        <el-card class="data-card">
          <canvas ref="courseCanvas" class="card-canvas"></canvas>
          <div class="card-content">
            <h3>开设课程</h3>
            <div class="number">{{ stats.totalCourses }}</div>
            <div class="desc">本学期: {{ stats.currentTermCourses }} 门</div>
            <el-button type="text" @click="$router.push('/teacher/courses')">
              课程管理
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="data-card">
          <canvas ref="studentCanvas" class="card-canvas"></canvas>
          <div class="card-content">
            <h3>授课学生</h3>
            <div class="number">{{ stats.totalStudents }}</div>
            <div class="desc">平均每课: {{ stats.averageStudents }} 人</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="data-card">
          <canvas ref="gradeCanvas" class="card-canvas"></canvas>
          <div class="card-content">
            <h3>课程成绩</h3>
            <div class="number">{{ stats.averageScore.toFixed(1) }}</div>
            <div class="desc">及格率: {{ stats.passRate.toFixed(1) }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程列表 -->
    <el-card class="course-list" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>本学期课程</span>
          <el-button text @click="$router.push('/teacher/courses')">
            查看全部课程
          </el-button>
        </div>
      </template>

      <el-table :data="courseList" style="width: 100%">
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="studentLimit" label="选课人数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getCapacityTagType(row.selectedCount, row.studentLimit)">
              {{ row.selectedCount }}/{{ row.studentLimit }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成绩录入" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gradesEntered ? 'success' : 'warning'">
              {{ row.gradesEntered ? '已录入' : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link
              @click="handleGrades(row)"
            >
              成绩管理
            </el-button>
            <el-button 
              type="success" 
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { teacherNewApi } from '@/api/new-api'
import './style.scss'

const router = useRouter()
const loading = ref(false)

// 统计数据
const stats = reactive({
  totalCourses: 0,
  currentTermCourses: 0,
  totalStudents: 0,
  averageStudents: 0,
  averageScore: 0,
  passRate: 0
})

// 课程列表
const courseList = ref([])

// Canvas引用
const courseCanvas = ref(null)
const studentCanvas = ref(null)
const gradeCanvas = ref(null)

// 获取教师课程数据
const fetchTeacherData = async () => {
  try {
    loading.value = true
    const teacherId = localStorage.getItem('teacherId')
    if (!teacherId) return
    const res = await teacherNewApi.getSections(parseInt(teacherId, 10))
    
    if (res && res.data) {
      courseList.value = res.data.map(section => ({
        ...section,
        id: section.id,
        name: section.courseName || section.sectionCode,
        credit: section.credit || 0,
        selectedCount: section.totalStudents || 0,
        studentLimit: section.studentLimit || section.totalStudents || 0,
        gradesEntered: section.gradedCount > 0
      }))
      
      // 更新统计数据
      let totalStudents = 0
      let totalScore = 0
      let passedCount = 0
      let scoreCount = 0
      
      courseList.value.forEach(section => {
        totalStudents += (section.totalStudents || 0)
        const avgScore = section.averageScore || 0
        const passCount = section.passedCount || 0
        if (avgScore > 0) {
          totalScore += avgScore * (section.totalStudents || 0)
          scoreCount += (section.totalStudents || 0)
          passedCount += passCount
        }
      })
      
      stats.totalCourses = courseList.value.length
      stats.currentTermCourses = courseList.value.length
      stats.totalStudents = totalStudents
      stats.averageStudents = courseList.value.length > 0 ? Math.round(totalStudents / courseList.value.length) : 0
      stats.averageScore = scoreCount > 0 ? totalScore / scoreCount : 0
      stats.passRate = scoreCount > 0 ? (passedCount / scoreCount) * 100 : 0
    }
  } catch (error) {
    console.error('获取教师数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 绘制圆形进度
const drawCircleProgress = (canvas, percentage, color) => {
  const ctx = canvas.getContext('2d')
  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  const radius = Math.min(centerX, centerY) - 10
  
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 背景圆
  ctx.beginPath()
  ctx.arc(centerX, centerY, radius, 0, Math.PI * 2)
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)'
  ctx.lineWidth = 10
  ctx.stroke()
  
  // 进度圆
  ctx.beginPath()
  ctx.arc(centerX, centerY, radius, -Math.PI / 2, (-Math.PI / 2) + (Math.PI * 2 * percentage))
  ctx.strokeStyle = color
  ctx.lineWidth = 10
  ctx.stroke()
}

// 初始化图表
const initCharts = () => {
  const canvases = [courseCanvas.value, studentCanvas.value, gradeCanvas.value]
  canvases.forEach(canvas => {
    canvas.width = 120
    canvas.height = 120
  })
  
  // 绘制进度
  drawCircleProgress(courseCanvas.value, stats.currentTermCourses / 6, '#67C23A') // 假设最多6门课
  drawCircleProgress(studentCanvas.value, Math.min(stats.averageStudents / 50, 1), '#409EFF') // 假设平均50人为满
  drawCircleProgress(gradeCanvas.value, stats.passRate / 100, '#E6A23C')
}

// 获取容量标签类型
const getCapacityTagType = (selected, limit) => {
  const percentage = selected / limit
  if (percentage >= 0.9) return 'danger'
  if (percentage >= 0.7) return 'warning'
  return 'success'
}

// 处理成绩管理（传入 sectionId）
const handleGrades = (course) => {
  router.push(`/teacher/grades?sectionId=${course.id || course.sectionId}`)
}

// 处理课程编辑
const handleEdit = (course) => {
  router.push(`/teacher/courses/edit?sectionId=${course.id || course.sectionId}`)
}

// 监听数据变化更新图表
watch(stats, () => {
  initCharts()
})

// 初始化
onMounted(async () => {
  await fetchTeacherData()
  initCharts()
})
</script> 