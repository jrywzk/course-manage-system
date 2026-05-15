<template>
  <div class="profile-container">
    <!-- 基本信息卡片 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" @click="handleEdit" v-if="!isEditing">
            编辑资料
          </el-button>
          <div v-else>
            <el-button type="success" @click="handleSave">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </div>
        </div>
      </template>
      
      <el-form 
        ref="formRef"
        :model="profileForm"
        :rules="rules"
        label-width="100px"
        :disabled="!isEditing"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="id">
              <el-input v-model="profileForm.id" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="profileForm.name" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form 
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/new-api'
import http from '@/api/index.js'
import './style.scss'

const formRef = ref(null)
const passwordFormRef = ref(null)
const isEditing = ref(false)

// 个人信息表单
const profileForm = reactive({
  id: '',
  name: ''
})

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取个人信息（使用新版 /api/auth/info）
const fetchProfile = async () => {
  try {
    const res = await authApi.getInfo()
    if (res && (res.code === 200 || res.status === 200) && res.data) {
      const info = res.data
      Object.assign(profileForm, {
        id: info.userId || info.id || '',
        name: info.realName || info.name || ''
      })
    } else {
      ElMessage.warning('未获取到个人信息')
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
    ElMessage.error(error.response?.data?.msg || '获取个人信息失败')
  }
}

// 编辑个人信息
const handleEdit = () => {
  isEditing.value = true
}

// 保存个人信息
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const res = await http.post('/api/user/update', null, {
      params: { 
        id: profileForm.id,
        password: '',
        role: 'student'
      }
    })
    if (res && res.status === 200) {
      ElMessage.success('保存成功')
      isEditing.value = false
    } else {
      ElMessage.error(res?.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.response?.data?.msg || '保存失败')
  }
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  fetchProfile() // 重新获取数据
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    const userId = localStorage.getItem('uid')
    const res = await http.post('/api/user/update', null, {
      params: { 
        id: userId,
        password: passwordForm.newPassword,
        role: 'student'
      }
    })
    if (res && res.status === 200) {
      ElMessage.success('密码修改成功')
      passwordFormRef.value.resetFields()
    } else {
      ElMessage.error(res?.msg || '修改密码失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.response?.data?.msg || '修改密码失败')
  }
}

// 初始化
onMounted(() => {
  fetchProfile()
})
</script>

<style lang="scss" scoped>
.profile-container {
  .profile-card,
  .password-card {
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  :deep(.el-input__wrapper),
  :deep(.el-textarea__wrapper) {
    background-color: var(--el-bg-color);
  }
  
  .w-full {
    width: 100%;
  }
}
</style> 