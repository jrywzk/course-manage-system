<template>
  <div class="teachers-container">
    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加教师
      </el-button>
    </div>

    <!-- 教师列表 -->
    <el-card class="teacher-list" v-loading="loading">
      <el-table :data="teacherList" style="width: 100%">
        <el-table-column prop="id" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="college" label="学院" min-width="180" />
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
      :title="dialogType === 'add' ? '添加教师' : '编辑教师'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="teacherForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="工号" prop="id">
          <el-input v-model="teacherForm.id" :disabled="dialogType === 'edit'" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="teacherForm.name" />
        </el-form-item>
        
        <el-form-item label="学院" prop="college">
          <el-input v-model="teacherForm.college" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="teacherForm.password" type="password" show-password />
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
import './style.scss'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 教师列表
const teacherList = ref([])

// 教师表单
const teacherForm = reactive({
  id: '',
  name: '',
  college: '',
  password: ''
})

// 表单验证规则
const rules = {
  id: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 获取教师列表
const fetchTeachers = async () => {
  try {
    loading.value = true
    const res = await adminApi.getAllTeachers()
    if (res.status === 200) {
      teacherList.value = res.data
    } else {
      ElMessage.warning(res.msg || '获取教师列表失败')
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

// 处理添加教师
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(teacherForm, {
    id: '',
    name: '',
    college: '',
    password: ''
  })
  dialogVisible.value = true
}

// 处理编辑教师
const handleEdit = (teacher) => {
  dialogType.value = 'edit'
  Object.assign(teacherForm, teacher)
  dialogVisible.value = true
}

// 处理删除教师
const handleDelete = async (teacher) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除教师"${teacher.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await adminApi.deleteTeacher(teacher.id)
    if (res.status === 200) {
      ElMessage.success('删除成功')
      fetchTeachers()
    } else {
      ElMessage.warning(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除教师失败:', error)
      ElMessage.error('删除教师失败')
    }
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (dialogType.value === 'add') {
      const res = await adminApi.addTeacher(teacherForm, teacherForm.password)
      if (res.status === 200) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchTeachers()
      } else {
        ElMessage.warning(res.msg || '添加失败')
      }
    } else {
      const res = await adminApi.updateTeacher(teacherForm)
      if (res.status === 200) {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        fetchTeachers()
      } else {
        ElMessage.warning(res.msg || '更新失败')
      }
    }
  } catch (error) {
    console.error('保存教师失败:', error)
    ElMessage.error('保存失败')
  }
}

// 初始化
onMounted(() => {
  fetchTeachers()
})
</script> 