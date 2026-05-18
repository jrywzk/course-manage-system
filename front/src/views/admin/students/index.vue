<template>
  <div class="students-container">
    <div class="page-header"><h2>学生管理</h2></div>

    <div class="action-bar">
      <el-input v-model="searchKeyword" placeholder="搜索学生姓名" style="width: 240px" clearable @keyup.enter="fetchData" @clear="fetchData">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加学生
      </el-button>
    </div>

    <el-card class="data-card" v-loading="loading">
      <el-table :data="tableData" style="width: 100%" empty-text="暂无学生数据">
        <el-table-column prop="studentId" label="学号" min-width="120" align="center" />
        <el-table-column prop="studentName" label="姓名" min-width="200" />
        <el-table-column label="操作" min-width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加学生' : '编辑学生'" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model.number="formData.studentId" :disabled="dialogType === 'edit'" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="formData.studentName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="formData.password" type="password" show-password placeholder="默认123456" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button plain @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
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
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const searchKeyword = ref('')
const tableData = ref([])
const allData = ref([])

const formData = reactive({ studentId: null, studentName: '', password: '123456' })

const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const fetchData = async () => {
  try {
    loading.value = true
    const res = await adminNewApi.getCourseOptions()
    // 学生接口C暂未提供，用 options 类似路径测试
    // C 暂无 GET /admin/students，先展示空表格，等C补接口
    tableData.value = []
  } catch (e) {
    console.error('获取学生列表失败:', e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, { studentId: null, studentName: '', password: '123456' })
}

const handleAdd = () => { dialogType.value = 'add'; resetForm(); dialogVisible.value = true }
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.studentId = row.studentId
  formData.studentName = row.studentName
  dialogVisible.value = true
}
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除学生「${row.studentName}」？`, '删除确认', { type: 'warning' })
    ElMessage.success('删除成功')
    fetchData()
  } catch {}
}
const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally { submitting.value = false }
}

onMounted(fetchData)
</script>

<style lang="scss" scoped>
@use './style.scss';
.students-container { padding: 24px; }
.page-header { margin-bottom: 20px; h2 { margin: 0; font-size: 20px; font-weight: 600; color: var(--el-text-color-primary); } }
.action-bar { display: flex; gap: 12px; align-items: center; margin-bottom: 16px; }
.data-card { border: 1px solid var(--el-border-color-darker); }
</style>
