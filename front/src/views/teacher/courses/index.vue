<template>
  <div class="courses-container">
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加课程
      </el-button>
      
      <div class="search-group">
        <el-input
          v-model="searchKey"
          placeholder="搜索课程"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <!-- 课程列表 -->
    <el-card class="course-list" v-loading="loading">
      <el-table :data="courseList" style="width: 100%">
        <el-table-column prop="id" label="课程代码" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column label="开课日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.term) }}
          </template>
        </el-table-column>
        <el-table-column prop="studentLimit" label="选课人数" width="120" align="center">
          <template #default="{ row }">
            <div class="capacity-info">
              <div class="capacity-text">
                <span class="current">{{ row.selectedCount || 0 }}</span>
                <span class="separator">/</span>
                <span class="total">{{ row.studentLimit || 0 }}</span>
              </div>
              <el-progress 
                :percentage="getCapacityPercentage(row.selectedCount, row.studentLimit)"
                :status="getCapacityStatus(row.selectedCount, row.studentLimit)"
                :stroke-width="8"
                :show-text="false"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '进行中' : '未开课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right" align="center">
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
            <el-button 
              type="danger" 
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加课程' : '编辑课程'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="学分" prop="credit">
          <el-input-number 
            v-model="courseForm.credit" 
            :min="1" 
            :max="10"
            :precision="1"
            :step="0.5"
          />
        </el-form-item>
        
        <el-form-item label="课程容量" prop="studentLimit">
          <el-input-number 
            v-model="courseForm.studentLimit" 
            :min="1" 
            :max="200"
            :step="5"
          />
        </el-form-item>
        
        <el-form-item label="开课日期" prop="term">
          <el-date-picker
            v-model="courseForm.term"
            type="date"
            placeholder="选择开课日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            class="w-full"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'
import './style.scss'

const router = useRouter()
const loading = ref(false)
const searchKey = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 课程列表
const courseList = ref([])

// 课程表单
const courseForm = reactive({
  name: '',
  credit: 2,
  studentLimit: 50,
  term: '',
  teacherId: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请选择学分', trigger: 'change' }],
  studentLimit: [{ required: true, message: '请设置课程容量', trigger: 'change' }],
  term: [{ required: true, message: '请选择开课日期', trigger: 'change' }]
}

// 获取容量百分比
const getCapacityPercentage = (selected = 0, limit = 1) => {
  if (!selected || !limit) return 0
  const percentage = Math.min((selected / limit) * 100, 100)
  return Math.round(percentage * 10) / 10  // 保留一位小数
}

// 获取容量状态
const getCapacityStatus = (selected = 0, limit = 1) => {
  if (!selected || !limit) return 'success'
  const percentage = selected / limit
  if (percentage >= 0.9) return 'exception'
  if (percentage >= 0.7) return 'warning'
  return 'success'
}

// 获取课程列表
const fetchCourses = async () => {
  try {
    loading.value = true
    const teacherId = localStorage.getItem('uid')
    const res = await teacherApi.getCoursesByTeacherId(teacherId)
    
    if (res.status === 200) {
      // 获取每个课程的选课人数
      const scorePromises = res.data.map(course =>
        teacherApi.getScoresByCourseId(course.id)
      )
      
      const scoreResults = await Promise.all(scorePromises)
      
      courseList.value = res.data.map((course, index) => ({
        ...course,
        selectedCount: scoreResults[index].data?.length || 0,
        studentLimit: course.studentLimit || 0,
        status: new Date(course.term) > new Date() ? 'active' : 'inactive'
      }))
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

// 处理搜索
const handleSearch = () => {
  // TODO: 实现搜索功能
}

// 处理添加课程
const handleAdd = () => {
  dialogType.value = 'add'
  const teacherId = localStorage.getItem('uid')
  Object.assign(courseForm, {
    name: '',
    credit: 2,
    studentLimit: 50,
    term: '',
    teacherId: parseInt(teacherId)
  })
  dialogVisible.value = true
}

// 处理编辑课程
const handleEdit = (course) => {
  dialogType.value = 'edit'
  const teacherId = localStorage.getItem('uid')
  Object.assign(courseForm, {
    ...course,
    teacherId: parseInt(teacherId)
  })
  dialogVisible.value = true
}

// 处理删除课程
const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${course.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await teacherApi.deleteCourse(course.id)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除课程失败')
    }
  }
}

// 处理成绩管理
const handleGrades = (course) => {
  router.push(`/teacher/grades?courseId=${course.id}`)
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const teacherId = localStorage.getItem('uid')
    courseForm.teacherId = parseInt(teacherId)
    
    if (dialogType.value === 'add') {
      const res = await teacherApi.addCourse(courseForm)
      if (res.status === 200) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchCourses()
      } else {
        ElMessage.warning(res.msg || '添加失败')
      }
    } else {
      const res = await teacherApi.updateCourse(courseForm)
      if (res.status === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        fetchCourses()
      } else {
        ElMessage.warning(res.msg || '更新失败')
      }
    }
  } catch (error) {
    console.error('保存课程失败:', error)
    ElMessage.error('保存失败')
  }
}

// 初始化
onMounted(() => {
  fetchCourses()
})

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script> 

<style lang="scss" scoped>
.capacity-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
  
  .capacity-text {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    
    .current {
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
    
    .separator {
      color: var(--el-text-color-secondary);
    }
    
    .total {
      color: var(--el-text-color-secondary);
    }
  }
  
  :deep(.el-progress-bar__outer) {
    border-radius: 4px;
    background-color: rgba(255, 255, 255, 0.1);
  }
  
  :deep(.el-progress-bar__inner) {
    border-radius: 4px;
    transition: all 0.3s ease;
  }
}
</style> 