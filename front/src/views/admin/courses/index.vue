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
      <el-table :data="courseList" style="width: 100%" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column prop="id" label="课程代码" min-width="120" />
        <el-table-column prop="name" label="课程名称" min-width="160" />
        <el-table-column prop="credit" label="学分" min-width="80" align="center" />
        <el-table-column prop="term" label="开课日期" min-width="140" />
        <el-table-column prop="teacherName" label="授课教师" min-width="120" />
        <el-table-column prop="studentLimit" label="选课人数" min-width="120" align="center">
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
            <!-- TODO: 依赖旧 deleteByCourseId 接口，新版接口确认后再启用
            <el-button 
              type="danger" 
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            -->
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
          <el-button plain @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" plain @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { adminNewApi } from '@/api/new-api'
import './style.scss'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 课程列表（从 /api/sections 获取）
const courseList = ref([])
// 教师列表（从 section 数据中提取）
const teacherList = computed(() => {
  const map = {}
  courseList.value.forEach(s => {
    if (s.teacherId && s.teacherName) {
      map[s.teacherId] = { id: s.teacherId, name: s.teacherName }
    }
  })
  return Object.values(map)
})

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

// 获取课程列表（使用 /api/sections）
const fetchCourses = async () => {
  try {
    loading.value = true
    const res = await adminNewApi.getAllSections()
    if (res && (res.status === 200 || res.code === 200)) {
      const list = res.data?.list || res.data || []
      courseList.value = list.map(s => ({
        id: s.sectionId,
        name: s.courseName,
        credit: s.credit,
        term: s.semester,
        teacherId: s.teacherId || 0,
        teacherName: s.teacherName || '未分配',
        studentLimit: s.capacityLimit,
        selectedCount: s.selectedCount || 0,
        sectionCode: s.sectionCode,
        building: s.building,
        roomNo: s.roomNo
      }))
    } else {
      ElMessage.warning('获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 处理添加课程（当前后端无新增教学班接口，提示用户）
const handleAdd = () => {
  ElMessage.info('课程添加功能暂不可用，请通过后端管理教学班数据')
}

// 处理编辑课程
const handleEdit = (course) => {
  dialogType.value = 'edit'
  Object.assign(courseForm, {
    name: course.name,
    credit: course.credit,
    studentLimit: course.studentLimit,
    term: course.term || '',
    teacherId: course.teacherId
  })
  dialogVisible.value = true
}

// 处理删除课程
const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${course.name}"(${course.sectionCode})吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await adminNewApi.deleteCourse(course.id, course.teacherId)
    if (res && (res.status === 200 || res.code === 200)) {
      ElMessage.success('删除成功')
      fetchCourses()
    } else {
      ElMessage.warning(res?.msg || '删除失败')
    }
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
    ElMessage.warning('课程编辑功能暂不可用，请通过后端直接管理数据')
    dialogVisible.value = false
  } catch (error) {
    console.error('保存课程失败:', error)
  }
}

// 获取容量标签类型
const getCapacityTagType = (selected, limit) => {
  if (!limit || limit === 0) return 'info'
  const percentage = selected / limit
  if (percentage >= 0.9) return 'danger'
  if (percentage >= 0.7) return 'warning'
  return 'success'
}

// 初始化
onMounted(async () => {
  await fetchCourses()
})
</script> 