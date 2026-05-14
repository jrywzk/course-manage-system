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
      <el-table :data="studentList" style="width: 100%" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column prop="id" label="学号" min-width="150" />
        <el-table-column prop="name" label="姓名" min-width="150" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
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
          <el-button plain @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" plain @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { adminNewApi } from '@/api/new-api'
import './style.scss'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 学生列表（当前后端无独立学生管理接口，从教学班数据中模拟提取）
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

// 获取学生列表（当前从 sections/students 接口提取，仅供参考）
const fetchStudents = async () => {
  try {
    loading.value = true
    // 获取所有教学班
    const res = await adminNewApi.getAllSections()
    if (res && (res.status === 200 || res.code === 200)) {
      const sections = res.data?.list || res.data || []
      // 提取唯一的学生信息（从 teacherName 中获取 — 暂用空列表提示）
      studentList.value = []
      if (sections.length === 0) {
        ElMessage.info('暂无教学班数据，学生管理功能依赖后端新版接口')
      }
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.info('学生管理接口暂未上线，请等待后端更新')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  ElMessage.info('搜索功能暂不可用')
}

// 处理添加学生
const handleAdd = () => {
  ElMessage.info('学生添加功能暂不可用，请等待后端接口上线')
}

// 处理编辑学生
const handleEdit = (student) => {
  ElMessage.info('学生编辑功能暂不可用，请等待后端接口上线')
}

// 处理删除学生
const handleDelete = async (student) => {
  ElMessage.warning('删除功能暂不可用')
}

// 处理表单提交
const handleSubmit = async () => {
  ElMessage.warning('表单提交功能暂不可用')
}
    ElMessage.error('保存失败')
  }
}

// 初始化
onMounted(() => {
  fetchStudents()
})
</script> 