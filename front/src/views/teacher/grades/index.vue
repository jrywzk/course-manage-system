<template>
  <div class="grades-container">
    <!-- 课程选择 -->
    <el-card class="select-card" v-if="!selectedCourse">
      <template #header>
        <div class="card-header">
          <span>选择课程</span>
        </div>
      </template>
      
      <el-table :data="courseList" style="width: 100%">
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="term" label="学期" width="180" />
        <el-table-column prop="studentLimit" label="选课人数" width="120" align="center">
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
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleSelectCourse(row)">
              管理成绩
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 成绩管理 -->
    <template v-else>
      <div class="action-bar">
        <div class="course-info">
          <h3>{{ selectedCourse.name }}</h3>
          <p>{{ selectedCourse.term }} | 学分: {{ selectedCourse.credit }}</p>
        </div>
        <el-button @click="handleBack">返回课程列表</el-button>
      </div>

      <el-card class="grades-list" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>学生成绩列表</span>
            <div class="header-actions">
              <el-button type="success" @click="handleSaveAll" :disabled="!hasChanges">
                保存全部
              </el-button>
            </div>
          </div>
        </template>

        <el-table :data="studentList" style="width: 100%">
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" min-width="120" />
          <el-table-column label="成绩" width="200" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.score"
                :min="0"
                :max="100"
                :precision="1"
                :step="0.5"
                @change="handleScoreChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getScoreTagType(row.score)">
                {{ getScoreStatus(row.score) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                link
                :disabled="!row.changed"
                @click="handleSaveScore(row)"
              >
                保存
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 成绩统计 -->
        <div class="statistics">
          <el-descriptions :column="4" border>
            <el-descriptions-item label="平均分">
              {{ stats.average.toFixed(1) }}
            </el-descriptions-item>
            <el-descriptions-item label="及格率">
              {{ stats.passRate.toFixed(1) }}%
            </el-descriptions-item>
            <el-descriptions-item label="优秀率">
              {{ stats.excellentRate.toFixed(1) }}%
            </el-descriptions-item>
            <el-descriptions-item label="最高分">
              {{ stats.highest }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { teacherApi } from '@/api/teacher'
import './style.scss'

const route = useRoute()
const loading = ref(false)
const selectedCourse = ref(null)
const courseList = ref([])
const studentList = ref([])

// 统计数据
const stats = reactive({
  average: 0,
  passRate: 0,
  excellentRate: 0,
  highest: 0
})

// 是否有未保存的更改
const hasChanges = computed(() => 
  studentList.value.some(student => student.changed)
)

// 获取课程列表
const fetchCourses = async () => {
  try {
    loading.value = true
    const teacherId = localStorage.getItem('uid')
    const res = await teacherApi.getCoursesByTeacherId(teacherId)
    
    if (res && res.data) {
      // 获取每个课程的选课人数和成绩信息
      const scorePromises = res.data.map(course =>
        teacherApi.getScoresByCourseId(course.id)
      )
      
      const scoreResults = await Promise.all(scorePromises)
      
      courseList.value = res.data.map((course, index) => {
        const scores = scoreResults[index].data || []
        return {
          ...course,
          selectedCount: scores.length,
          gradesEntered: scores.some(score => score.score > 0)
        }
      })

      // 如果URL中有courseId，自动选择对应课程
      const courseId = route.query.courseId
      if (courseId) {
        const course = courseList.value.find(c => c.id === parseInt(courseId))
        if (course) {
          handleSelectCourse(course)
        }
      }
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 获取学生成绩列表
const fetchStudentGrades = async (courseId) => {
  try {
    loading.value = true
    const res = await teacherApi.getScoresByCourseId(courseId)
    
    if (res && res.data) {
      studentList.value = res.data.map(score => ({
        ...score,
        changed: false,
        originalScore: score.score
      }))
      
      updateStats()
    }
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  const scores = studentList.value.map(student => student.score).filter(score => score > 0)
  if (scores.length === 0) {
    Object.assign(stats, {
      average: 0,
      passRate: 0,
      excellentRate: 0,
      highest: 0
    })
    return
  }
  
  stats.average = scores.reduce((sum, score) => sum + score, 0) / scores.length
  stats.passRate = (scores.filter(score => score >= 60).length / scores.length) * 100
  stats.excellentRate = (scores.filter(score => score >= 90).length / scores.length) * 100
  stats.highest = Math.max(...scores)
}

// 获取容量标签类型
const getCapacityTagType = (selected, limit) => {
  const percentage = selected / limit
  if (percentage >= 0.9) return 'danger'
  if (percentage >= 0.7) return 'warning'
  return 'success'
}

// 获取成绩标签类型
const getScoreTagType = (score) => {
  if (!score) return 'info'
  if (score >= 90) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 获取成绩状态文本
const getScoreStatus = (score) => {
  if (!score) return '未录入'
  if (score >= 90) return '优秀'
  if (score >= 60) return '及格'
  return '不及格'
}

// 选择课程
const handleSelectCourse = (course) => {
  selectedCourse.value = course
  fetchStudentGrades(course.id)
}

// 返回课程列表
const handleBack = () => {
  if (hasChanges.value) {
    ElMessageBox.confirm(
      '有未保存的成绩，确定要返回吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      selectedCourse.value = null
      studentList.value = []
    }).catch(() => {})
  } else {
    selectedCourse.value = null
    studentList.value = []
  }
}

// 处理成绩变化
const handleScoreChange = (student) => {
  student.changed = student.score !== student.originalScore
  updateStats()
}

// 保存单个学生成绩
const handleSaveScore = async (student) => {
  try {
    const res = await teacherApi.updateScore({
      courseId: selectedCourse.value.id,
      studentId: student.studentId,
      score: student.score
    })
    if (res.status === 200) {
      student.changed = false
      student.originalScore = student.score
      ElMessage.success('保存成功')
    } else {
      ElMessage.warning(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存失败')
  }
}

// 保存所有更改
const handleSaveAll = async () => {
  try {
    const changedStudents = studentList.value.filter(student => student.changed)
    const savePromises = changedStudents.map(student => 
      teacherApi.updateScore({
        courseId: selectedCourse.value.id,
        studentId: student.studentId,
        score: student.score
      })
    )
    
    await Promise.all(savePromises)
    
    studentList.value.forEach(student => {
      student.changed = false
      student.originalScore = student.score
    })
    
    ElMessage.success('全部保存成功')
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存失败')
  }
}

// 初始化
onMounted(() => {
  fetchCourses()
})
</script> 