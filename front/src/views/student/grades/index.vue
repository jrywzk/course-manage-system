<template>
  <div class="grades-container">
    <!-- 成绩统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.gpa.toFixed(2) }}</div>
            <div class="stat-label">平均绩点</div>
            <div class="stat-trend" :class="{ 'up': stats.gpaChange > 0 }">
              {{ stats.gpaChange > 0 ? '+' : '' }}{{ stats.gpaChange.toFixed(2) }}
              <el-icon><ArrowUp v-if="stats.gpaChange > 0" /><ArrowDown v-else /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalCredits }}</div>
            <div class="stat-label">已修学分</div>
            <div class="stat-progress">
              <el-progress 
                :percentage="(stats.totalCredits / stats.requiredCredits) * 100"
                :format="format"
                :stroke-width="8"
              />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.passRate.toFixed(1) }}%</div>
            <div class="stat-label">通过率</div>
            <div class="stat-desc">已通过 {{ stats.passedCourses }}/{{ stats.totalCourses }} 门</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stats.excellentRate.toFixed(1) }}%</div>
            <div class="stat-label">优秀率</div>
            <div class="stat-desc">优秀 {{ stats.excellentCourses }} 门</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩列表 — 成绩详情表格暂隐藏，等新版接口就绪后恢复 -->
    <el-card class="grade-list">
      <template #header>
        <div class="card-header">
          <span>成绩详情</span>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { enrollmentApi } from '@/api/new-api'
import './style.scss'

// 统计数据
const stats = reactive({
  gpa: 0,
  gpaChange: 0,
  totalCredits: 0,
  requiredCredits: 120,
  passRate: 0,
  passedCourses: 0,
  totalCourses: 0,
  excellentRate: 0,
  excellentCourses: 0
})

// 学期选项
const terms = [
  { label: '2023-2024学年第一学期', value: '2023-1' },
  { label: '2023-2024学年第二学期', value: '2023-2' },
  { label: '2022-2023学年第一学期', value: '2022-1' },
  { label: '2022-2023学年第二学期', value: '2022-2' }
]

// 筛选表单
const filterForm = reactive({
  term: ''
})

// 成绩列表
const gradeList = ref([])
const loading = ref(false)

// 格式化进度
const format = (percentage) => `${stats.totalCredits}/${stats.requiredCredits}`

// 安全获取 studentId
const getStudentId = () => {
  const sid = localStorage.getItem('studentId')
  if (sid) return parseInt(sid, 10)
  return null
}

// 获取成绩样式
const getGradeClass = (grade) => {
  if (grade >= 90) return 'grade-excellent'
  if (grade >= 80) return 'grade-good'
  if (grade >= 60) return 'grade-pass'
  return 'grade-fail'
}

// 获取成绩列表（使用新版 enrollments 接口）
const fetchGrades = async () => {
  try {
    loading.value = true
    const studentId = getStudentId()
    if (!studentId) {
      ElMessage.error('未获取到学生信息，请重新登录')
      return
    }

    const res = await enrollmentApi.getMyEnrollments(studentId, { status: '' })
    const enrollments = res?.data || []
    const enrolledList = Array.isArray(enrollments) ? enrollments : []

    // 按学期筛选
    let filtered = enrolledList
    if (filterForm.term) {
      filtered = enrolledList.filter(e => e.semester === filterForm.term)
    }

    gradeList.value = filtered.map(e => ({
      id: e.enrollmentId,
      term: e.semester || '',
      courseId: e.courseCode || '',
      courseName: e.courseName || '',
      sectionCode: e.sectionCode || '',
      credit: e.credit || 0,
      usualScore: e.usualScore ?? null,
      examScore: e.examScore ?? null,
      score: e.finalScore ?? null,
      gpa: e.gpaPoint ?? null,
      isPassed: e.isPassed
    }))

    // 更新统计信息
    const hasScore = gradeList.value.filter(g => g.score != null)
    stats.totalCourses = gradeList.value.length
    stats.passedCourses = hasScore.filter(g => g.score >= 60).length
    stats.excellentCourses = hasScore.filter(g => g.score >= 90).length
    stats.passRate = gradeList.value.length > 0 ? (stats.passedCourses / gradeList.value.length) * 100 : 0
    stats.excellentRate = gradeList.value.length > 0 ? (stats.excellentCourses / gradeList.value.length) * 100 : 0
    stats.totalCredits = hasScore.filter(g => g.score >= 60).reduce((sum, g) => sum + (g.credit || 0), 0)

    // GPA：后端直接返回 gpaPoint
    const gpaList = hasScore.filter(g => g.gpa != null)
    stats.gpa = gpaList.length > 0 ? gpaList.reduce((sum, g) => sum + (g.gpa || 0), 0) / gpaList.length : 0
    stats.gpaChange = 0
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  fetchGrades()
}

// 初始化
onMounted(() => {
  fetchGrades()
})
</script> 