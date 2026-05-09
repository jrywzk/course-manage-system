<template>
  <div class="students-container">
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加学生
      </el-button>
    </div>

    <!-- 学生列表 -->
    <el-card class="student-list" v-loading="loading">
      <el-table :data="studentList" style="width: 100%">
        <el-table-column prop="id" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
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
      :title="dialogType === 'add' ? '添加学生' : '编辑学生'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="studentForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="学号" prop="id">
          <el-input v-model="studentForm.id" :disabled="dialogType === 'edit'" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="studentForm.password" type="password" show-password />
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
import { Plus, Search } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'
import './style.scss'

const loading = ref(false)
const searchKey = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 学生列表
const studentList = ref([])

// 学生表单
const studentForm = reactive({
  id: '',
  name: '',
  password: '123456'
})

// 表单验证规则
const rules = {
  id: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 获取学生列表
const fetchStudents = async () => {
  try {
    loading.value = true
    const res = await adminApi.getAllStudents()
    if (res.status === 200) {
      studentList.value = res.data
    } else {
      ElMessage.warning(res.msg || '获取学生列表失败')
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  // TODO: 实现搜索功能
}

// 处理添加学生
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(studentForm, {
    id: '',
    name: '',
    password: ''
  })
  dialogVisible.value = true
}

// 处理编辑学生
const handleEdit = (student) => {
  dialogType.value = 'edit'
  Object.assign(studentForm, student)
  dialogVisible.value = true
}

// 处理删除学生
const handleDelete = async (student) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学生"${student.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await adminApi.deleteStudent(student.id)
    if (res.status === 200) {
      ElMessage.success('删除成功')
      fetchStudents()
    } else {
      ElMessage.warning(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除学生失败:', error)
      ElMessage.error('删除学生失败')
    }
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (dialogType.value === 'add') {
      const res = await adminApi.addStudent(studentForm, studentForm.password)
      if (res.status === 200) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchStudents()
      } else {
        ElMessage.warning(res.msg || '添加失败')
      }
    } else {
      const res = await adminApi.updateStudent(studentForm)
      if (res.status === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        fetchStudents()
      } else {
        ElMessage.warning(res.msg || '更新失败')
      }
    }
  } catch (error) {
    console.error('保存学生失败:', error)
    ElMessage.error('保存失败')
  }
}

// 初始化
onMounted(() => {
  fetchStudents()
})
</script> 