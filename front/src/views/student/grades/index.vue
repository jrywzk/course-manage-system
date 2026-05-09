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

    <!-- 成绩列表 -->
    <el-card class="grade-list" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>成绩详情</span>
          <div class="header-actions">
            <el-select v-model="filterForm.term" placeholder="选择学期" clearable>
              <el-option
                v-for="item in terms"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <el-button type="primary" @click="handleSearch">查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="gradeList" style="width: 100%">
        <el-table-column prop="term" label="学期" width="180" />
        <el-table-column prop="courseId" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            <span :class="getGradeClass(row.score)">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gpa" label="绩点" width="80" align="center">
          <template #default="{ row }">
            {{ studentApi.getGradePoint(row.score).toFixed(1) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { studentApi } from '@/api/student'
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

// 获取成绩样式
const getGradeClass = (grade) => {
  if (grade >= 90) return 'grade-excellent'
  if (grade >= 80) return 'grade-good'
  if (grade >= 60) return 'grade-pass'
  return 'grade-fail'
}

// 获取成绩列表
const fetchGrades = async () => {
  try {
    loading.value = true
    const studentId = localStorage.getItem('uid')
    let res
    
    if (filterForm.term) {
      res = await studentApi.getScoreByStudentIdAndTerm(studentId, filterForm.term)
    } else {
      res = await studentApi.getScoreByStudentId(studentId)
    }
    
    if (res.data) {
      // 获取课程详细信息
      const coursePromises = res.data.map(score => 
        studentApi.getCourseById(score.courseId)
      )
      const courseResults = await Promise.all(coursePromises)
      
      // 合并课程信息和成绩信息
      gradeList.value = res.data.map((score, index) => ({
        ...score,
        courseName: courseResults[index].data.name,
        credit: courseResults[index].data.credit
      }))

      // 更新统计信息
      stats.totalCourses = gradeList.value.length
      stats.passedCourses = gradeList.value.filter(grade => grade.score >= 60).length
      stats.excellentCourses = gradeList.value.filter(grade => grade.score >= 90).length
      stats.passRate = (stats.passedCourses / stats.totalCourses) * 100
      stats.excellentRate = (stats.excellentCourses / stats.totalCourses) * 100
      stats.totalCredits = gradeList.value.reduce((sum, grade) => 
        grade.score >= 60 ? sum + grade.credit : sum, 0
      )
      stats.gpa = studentApi.calculateGPA(gradeList.value)
      // TODO: 计算GPA变化
      stats.gpaChange = 0.2
    }
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