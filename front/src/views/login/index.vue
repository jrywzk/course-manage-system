<template>
  <div class="login-container">
    <div class="background">
      <div v-for="n in 6" :key="n" class="circle-container">
        <div class="circle"></div>
      </div>
    </div>
    
    <el-card class="login-card">
      <template #header>
        <h2>学生选课管理系统</h2>
        <div class="sub-title">Student Course Management System</div>
      </template>
      
      <el-form 
        :model="loginForm" 
        :rules="rules" 
        ref="loginFormRef"
        v-loading="loading"
        element-loading-text="登录中..."
        element-loading-background="rgba(0, 0, 0, 0.5)"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username"
            placeholder="请输入学号/工号"
            prefix-icon="User"
            :input-style="{ color: '#fff' }"
            :disabled="loading"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            :input-style="{ color: '#fff' }"
            :disabled="loading"
          />
        </el-form-item>
        
        <el-button 
          type="primary" 
          class="w-full login-button" 
          @click="handleLogin"
          :loading="loading"
        >
          <span class="button-text">{{ loading ? '登录中...' : '登录' }}</span>
          <el-icon class="button-icon" v-if="!loading"><ArrowRight /></el-icon>
        </el-button>
      </el-form>
      
      <div class="demo-info">
        <p>管理员账号: 1</p>
        <p>管理员密码: 123</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import http from '@/api/index.js'
import './style.scss'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '1',
  password: '123'
})

const rules = {
  username: [
    { required: true, message: '请输入学号/工号', trigger: 'blur' },
    { type: 'number', message: '必须为数字', transform: (value) => Number(value) }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const checkLoginAndRedirect = () => {
  const uid = localStorage.getItem('uid')
  const role = localStorage.getItem('role')
  
  if (uid && role) {
    const routes = {
      student: '/student/dashboard',
      teacher: '/teacher/dashboard',
      admin: '/admin/dashboard'
    }
    
    if (routes[role]) {
      router.push(routes[role])
    } else {
      localStorage.removeItem('uid')
      localStorage.removeItem('role')
    }
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const response = await http.post('/api/user/login', null, {
      params: {
        id: parseInt(loginForm.username),
        password: loginForm.password
      }
    })

    if (response.status === 200) {
      localStorage.setItem('uid', loginForm.username)
      const role = response.data
      localStorage.setItem('role', role)
      
      const routes = {
        student: '/student/dashboard',
        teacher: '/teacher/dashboard',
        admin: '/admin/dashboard'
      }

      if (routes[role]) {
        router.push(routes[role])
        ElMessage.success('登录成功')
      } else {
        ElMessage.error('无效的用户角色')
        localStorage.removeItem('uid')
        localStorage.removeItem('role')
      }
    } else {
      ElMessage.error('登录失败')
    }
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  checkLoginAndRedirect()
})
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a1a2e, #0a0a1a);
  position: relative;
  overflow: hidden;
}

.background {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.circle-container {
  position: absolute;
  transform-origin: center;
  animation: rotate 20s linear infinite;
  
  @for $i from 1 through 6 {
    &:nth-child(#{$i}) {
      $size: random(300) + 100px;
      $x: random(100) + 0%;
      $y: random(100) + 0%;
      $delay: random(20) + 0s;
      $duration: 15 + random(30) + s;
      
      left: $x;
      top: $y;
      animation-delay: $delay;
      animation-duration: $duration;
      
      .circle {
        width: $size;
        height: $size;
        background: radial-gradient(
          circle at center,
          rgba(64, 158, 255, 0.1) 0%,
          rgba(103, 194, 58, 0.1) 100%
        );
        border-radius: 50%;
        filter: blur(20px);
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg) translateX(50px) rotate(0deg);
  }
  to {
    transform: rotate(360deg) translateX(50px) rotate(-360deg);
  }
}

.login-card {
  width: 400px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  
  &:hover {
    border-color: rgba(255, 255, 255, 0.2);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.3),
                0 0 30px rgba(103, 194, 58, 0.1);
  }
  
  :deep(.el-card__header) {
    text-align: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    h2 {
      margin: 0;
      color: #fff;
      font-size: 24px;
      background: linear-gradient(45deg, #409EFF, #67C23A);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      letter-spacing: 2px;
      margin-bottom: 5px;
    }
    
    .sub-title {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.6);
      letter-spacing: 1px;
    }
  }
  
  :deep(.el-input__wrapper),
  :deep(.el-select .el-input__wrapper) {
    background-color: rgba(255, 255, 255, 0.05);
    box-shadow: none;
    border: 1px solid rgba(255, 255, 255, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      border-color: rgba(255, 255, 255, 0.2);
      box-shadow: 0 0 15px rgba(103, 194, 58, 0.1);
    }
    
    &:focus-within {
      border-color: #67C23A;
      box-shadow: 0 0 15px rgba(103, 194, 58, 0.2);
    }
  }
  
  :deep(.el-button) {
    background: linear-gradient(45deg, #409EFF, #67C23A);
    border: none;
    height: 44px;
    font-size: 16px;
    letter-spacing: 2px;
    position: relative;
    overflow: hidden;
    
    &:hover {
      opacity: 0.9;
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(103, 194, 58, 0.3);
      
      .button-text {
        transform: translateX(-10px);
      }
      
      .button-icon {
        opacity: 1;
        transform: translateX(0);
      }
    }
    
    .button-text {
      transition: transform 0.3s ease;
    }
    
    .button-icon {
      position: absolute;
      right: 20px;
      opacity: 0;
      transform: translateX(-20px);
      transition: all 0.3s ease;
    }
  }
}

.demo-info {
  margin-top: 20px;
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  
  p {
    margin: 5px 0;
  }
}

.w-full {
  width: 100%;
}
</style> 