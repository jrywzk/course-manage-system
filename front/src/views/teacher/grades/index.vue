<template>
  <div class="grades-container">
    <!-- 教学班选择 -->
    <el-card class="section-card" v-if="!selectedSection">
      <template #header>
        <div class="card-header">
          <span>选择教学班</span>
        </div>
      </template>

      <el-table :data="sectionList" style="width: 100%" v-loading="loadingSections">
        <el-table-column prop="sectionCode" label="教学班编号" min-width="140" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.hasScore ? 'success' : 'warning'">
              {{ row.hasScore ? '已有成绩' : '待录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleSelectSection(row)">
              管理成绩
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 成绩管理 -->
    <template v-else>
      <div class="action-bar">
        <div class="section-info">
          <h3>{{ selectedSection.courseName }}</h3>
          <p>{{ selectedSection.sectionCode }} | {{ selectedSection.semester }}</p>
        </div>
        <el-button type="success" @click="handleBack">返回教学班列表</el-button>
      </div>

      <el-card class="section-card" v-loading="loading">
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
          <el-table-column prop="studentNo" label="学号" min-width="120" />
          <el-table-column prop="studentName" label="姓名" min-width="100" />
          <el-table-column prop="majorName" label="专业" min-width="140" />
          <el-table-column label="平时成绩" min-width="120" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.usualScore"
                :min="0"
                :max="100"
                :precision="0"
                size="small"
                @change="handleScoreChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="考试成绩" min-width="120" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.examScore"
                :min="0"
                :max="100"
                :precision="0"
                size="small"
                @change="handleScoreChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="总评成绩" min-width="120" align="center">
            <template #default="{ row }">
              <span>{{ computeFinalScore(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" min-width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.hasScore ? 'success' : 'info'">
                {{ row.hasScore ? '已录入' : '未录入' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                :disabled="!row.changed"
                @click="handleSaveOne(row)"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { teacherNewApi } from '@/api/new-api'
import './style.scss'

const loading = ref(false)
const loadingSections = ref(false)
const selectedSection = ref(null)
const sectionList = ref([])
const studentList = ref([])

// 安全获取 teacherId
const getTeacherId = () => {
  const tid = localStorage.getItem('teacherId')
  if (tid) return parseInt(tid, 10)
  return null
}

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

// 计算总评成绩（平时30% + 考试70%）
const computeFinalScore = (row) => {
  const usual = row.usualScore || 0
  const exam = row.examScore || 0
  if (row.hasScore && row.finalScore !== undefined && row.finalScore !== null) {
    return row.finalScore.toFixed(1)
  }
  return ((usual * 0.3 + exam * 0.7)).toFixed(1)
}

// 获取教师教学班列表
const fetchSections = async () => {
  try {
    loadingSections.value = true
    const teacherId = getTeacherId()
    const res = await teacherNewApi.getSections(teacherId)

    if (res && res.data) {
      sectionList.value = res.data.map(s => ({
        ...s,
        id: s.sectionId || s.id,
        hasScore: s.gradedCount > 0
      }))
    }
  } catch (error) {
    console.error('获取教学班列表失败:', error)
    ElMessage.error('获取教学班列表失败')
  } finally {
    loadingSections.value = false
  }
}

// 获取教学班成绩汇总
const fetchSectionScores = async (sectionId) => {
  try {
    loading.value = true
    const teacherId = getTeacherId()
    const res = await teacherNewApi.getSectionScores(sectionId, teacherId)

    if (res && res.data) {
      const data = res.data
      // 先获取学生名单
      await fetchStudents(sectionId)
    }
  } catch (error) {
    console.error('获取成绩汇总失败:', error)
    ElMessage.error('获取成绩汇总失败')
  } finally {
    loading.value = false
  }
}

// 获取教学班学生名单
const fetchStudents = async (sectionId) => {
  try {
    loading.value = true
    const teacherId = getTeacherId()
    const res = await teacherNewApi.getStudents(sectionId, teacherId)

    if (res && res.data) {
      studentList.value = res.data.map(s => ({
        ...s,
        changed: false,
        originalUsualScore: s.usualScore || 0,
        originalExamScore: s.examScore || 0
      }))
      updateStats()
    }
  } catch (error) {
    console.error('获取学生名单失败:', error)
    ElMessage.error('获取学生名单失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  const scores = studentList.value
    .map(s => {
      const finalScore = s.hasScore && s.finalScore !== undefined
        ? s.finalScore
        : ((s.usualScore || 0) * 0.3 + (s.examScore || 0) * 0.7)
      return finalScore
    })
    .filter(s => s > 0)

  if (scores.length === 0) {
    Object.assign(stats, { average: 0, passRate: 0, excellentRate: 0, highest: 0 })
    return
  }

  stats.average = scores.reduce((sum, s) => sum + s, 0) / scores.length
  stats.passRate = (scores.filter(s => s >= 60).length / scores.length) * 100
  stats.excellentRate = (scores.filter(s => s >= 90).length / scores.length) * 100
  stats.highest = Math.max(...scores)
}

// 选择教学班
const handleSelectSection = (section) => {
  selectedSection.value = section
  fetchStudents(section.id)
}

// 返回教学班列表
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
      selectedSection.value = null
      studentList.value = []
    }).catch(() => {})
  } else {
    selectedSection.value = null
    studentList.value = []
  }
}

// 处理成绩变化
const handleScoreChange = (student) => {
  student.changed =
    (student.usualScore || 0) !== student.originalUsualScore ||
    (student.examScore || 0) !== student.originalExamScore
  updateStats()
}

// 保存单个学生成绩
const handleSaveOne = async (student) => {
  try {
    const teacherId = getTeacherId()
    const res = await teacherNewApi.saveScore(
      student.enrollmentId,
      teacherId,
      student.usualScore || 0,
      student.examScore || 0
    )
    if (res && (res.status === 200 || res.code === 200)) {
      student.changed = false
      student.originalUsualScore = student.usualScore || 0
      student.originalExamScore = student.examScore || 0
      student.hasScore = true
      ElMessage.success('保存成功')
      updateStats()
    } else {
      ElMessage.warning(res?.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存失败')
  }
}

// 保存所有更改
const handleSaveAll = async () => {
  try {
    const changedStudents = studentList.value.filter(s => s.changed)
    const teacherId = getTeacherId()
    const savePromises = changedStudents.map(s =>
      teacherNewApi.saveScore(
        s.enrollmentId,
        parseInt(teacherId),
        s.usualScore || 0,
        s.examScore || 0
      )
    )

    await Promise.all(savePromises)

    studentList.value.forEach(s => {
      s.changed = false
      s.originalUsualScore = s.usualScore || 0
      s.originalExamScore = s.examScore || 0
      s.hasScore = true
    })
    ElMessage.success('全部保存成功')
    updateStats()
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('部分保存失败，请重试')
  }
}

// 初始化
onMounted(() => {
  fetchSections()
})
</script>
