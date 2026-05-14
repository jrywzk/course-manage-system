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
      <el-table :data="teacherList" style="width: 100%" :header-cell-style="{ textAlign: 'center' }">
        <el-table-column prop="id" label="工号" min-width="150" />
        <el-table-column prop="name" label="姓名" min-width="150" />
        <el-table-column prop="college" label="学院" min-width="180" />
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
import { Plus } from '@element-plus/icons-vue'
import { adminNewApi } from '@/api/new-api'
import './style.scss'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 教师列表（从 /api/sections 提取唯一教师）
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

// 获取教师列表（从 /api/sections 数据中提取唯一教师）
const fetchTeachers = async () => {
  try {
    loading.value = true
    const res = await adminNewApi.getAllSections()
    if (res && (res.status === 200 || res.code === 200)) {
      const sections = res.data?.list || res.data || []
      // 提取唯一教师
      const teacherMap = {}
      sections.forEach(s => {
        if (s.teacherName && !teacherMap[s.teacherName]) {
          teacherMap[s.teacherName] = {
            id: s.teacherId || '',
            name: s.teacherName,
            college: s.building || '未知学院'
          }
        }
      })
      teacherList.value = Object.values(teacherMap)
      if (teacherList.value.length === 0) {
        ElMessage.info('暂无教师数据')
      }
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.info('教师管理接口暂未上线，请等待后端更新')
  } finally {
    loading.value = false
  }
}

// 处理添加教师
const handleAdd = () => {
  ElMessage.info('教师添加功能暂不可用，请等待后端接口上线')
}

// 处理编辑教师
const handleEdit = (teacher) => {
  ElMessage.info('教师编辑功能暂不可用，请等待后端接口上线')
}

// 处理删除教师
const handleDelete = async (teacher) => {
  ElMessage.warning('删除功能暂不可用')
}

// 处理表单提交
const handleSubmit = async () => {
  ElMessage.warning('表单提交功能暂不可用')
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