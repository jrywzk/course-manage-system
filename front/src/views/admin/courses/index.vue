<template>
  <div class="courses-container">
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加课程
      </el-button>
    </div>

    <!-- 课程列表 -->
    <el-card class="course-list" v-loading="loading">
      <el-table :data="courseList" style="width: 100%">
        <el-table-column prop="id" label="课程代码" width="120" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="term" label="开课日期" width="180" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="studentLimit" label="选课人数" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getCapacityTagType(row.selectedCount, row.studentLimit)">
              {{ row.selectedCount }}/{{ row.studentLimit }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
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

    <!-- 添加/编辑对话框 -->
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
          <el-input v-model="courseForm.name" />
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
        
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" class="w-full">
            <el-option 
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'
import { studentApi } from '@/api/student'
import './style.scss'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 课程列表
const courseList = ref([])
// 教师列表
const teacherList = ref([])

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
  term: [{ required: true, message: '请选择开课日期', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择授课教师', trigger: 'change' }]
}

// 获取课程列表
const fetchCourses = async () => {
  try {
    loading.value = true
    const res = await adminApi.getAllCourses()
    if (res && res.data) {
      // 获取每个课程的选课人数
      const scorePromises = res.data.map(course =>
        studentApi.getScoreByCourseId(course.id)
      )
      
      const scoreResults = await Promise.all(scorePromises)
      
      courseList.value = res.data.map((course, index) => ({
        ...course,
        selectedCount: scoreResults[index].data?.length || 0
      }))
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 获取教师列表
const fetchTeachers = async () => {
  try {
    const res = await adminApi.getAllTeachers()
    if (res && res.data) {
      teacherList.value = res.data
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  }
}

// 处理添加课程
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(courseForm, {
    name: '',
    credit: 2,
    studentLimit: 50,
    term: '',
    teacherId: ''
  })
  dialogVisible.value = true
}

// 处理编辑课程
const handleEdit = (course) => {
  dialogType.value = 'edit'
  Object.assign(courseForm, course)
  dialogVisible.value = true
}

// 处理删除课程
const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${course.name}"吗？`,
      '���除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await adminApi.deleteCourse(course.id)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除课程失败')
    }
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (dialogType.value === 'add') {
      await adminApi.addCourse(courseForm)
      ElMessage.success('添加成功')
    } else {
      await adminApi.updateCourse(courseForm)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    fetchCourses()
  } catch (error) {
    console.error('保存课程失败:', error)
    ElMessage.error('保存失败')
  }
}

// 获取容量标签类型
const getCapacityTagType = (selected, limit) => {
  const percentage = selected / limit
  if (percentage >= 0.9) return 'danger'
  if (percentage >= 0.7) return 'warning'
  return 'success'
}

// 初始化
onMounted(async () => {
  await fetchTeachers()
  await fetchCourses()
})
</script> 