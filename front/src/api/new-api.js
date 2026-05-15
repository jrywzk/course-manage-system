/**
 * 新版业务链 API 封装
 *
 * 业务链: course → course_section → enrollment → score
 * 所有方法直接调用新版接口，旧链已清理。
 */

import http from '@/api/index.js'

// ======================== 1. 认证 ========================

export const authApi = {
  /**
   * 登录（统一入口）
   * POST /api/auth/login
   */
  async login(username, password) {
    return http.post('/api/auth/login', { username, password })
  },

  /**
   * 获取当前用户信息
   * GET /api/auth/info
   */
  async getInfo() {
    return http.get('/api/auth/info')
  }
}

// ======================== 2. 教学班 ========================

export const sectionApi = {
  /**
   * 教学班列表（学生选课列表页）
   * GET /api/sections?page=&pageSize=&status=
   */
  async getSections(params = {}) {
    return http.get('/api/sections', params)
  },

  /**
   * 教学班详情
   * GET /api/sections/{id}
   */
  async getSectionDetail(id) {
    return http.get(`/api/sections/${id}`)
  }
}

// ======================== 3. 选课 / 退课 ========================

export const enrollmentApi = {
  /**
   * 选课
   * POST /api/enrollments
   */
  async enroll(studentId, sectionId) {
    return http.post('/api/enrollments', { studentId, sectionId, source: '自主选课' })
  },

  /**
   * 退课
   * DELETE /api/enrollments/{enrollmentId}
   */
  async drop(enrollmentId) {
    return http.delete(`/api/enrollments/${enrollmentId}`)
  },

  /**
   * 学生已选教学班列表（我的课程）
   * GET /api/students/{studentId}/enrollments?semester=&status=
   */
  async getMyEnrollments(studentId, params = {}) {
    return http.get(`/api/students/${studentId}/enrollments`, params)
  }
}

// ======================== 4. 教师端 ========================

/**
 * 安全获取 teacherId：优先用传入值，否则从 localStorage 读取
 */
function getTeacherId(tid) {
  if (tid !== null && tid !== undefined && tid !== '' && tid !== 'undefined' && tid !== 'null') {
    return parseInt(tid, 10)
  }
  const stored = localStorage.getItem('teacherId')
  if (stored) return parseInt(stored, 10)
  return null
}

export const teacherNewApi = {
  /**
   * 教师授课教学班列表
   * GET /api/teachers/{teacherId}/sections  (Day2: 需要 Token)
   */
  async getSections(teacherId) {
    const tid = getTeacherId(teacherId)
    return http.get(`/api/teachers/${tid}/sections`)
  },

  /**
   * 教学班学生名单
   * GET /api/sections/{sectionId}/students?teacherId=
   * Day2: teacherId 必填 Query + Token 校验
   */
  async getStudents(sectionId, teacherId) {
    const tid = getTeacherId(teacherId)
    return http.get(`/api/sections/${sectionId}/students`, { teacherId: tid })
  },

  /**
   * 录入/修改成绩
   * POST /api/scores   (Day2: 需要 Token + teacherId 必填 Body)
   */
  async saveScore(enrollmentId, teacherId, usualScore, examScore) {
    const tid = getTeacherId(teacherId)
    return http.post('/api/scores', { enrollmentId, teacherId: tid, usualScore, examScore })
  },

  /**
   * 教学班成绩汇总
   * GET /api/sections/{sectionId}/scores?teacherId=
   * Day2: teacherId 必填 Query + Token 校验
   */
  async getSectionScores(sectionId, teacherId) {
    const tid = getTeacherId(teacherId)
    return http.get(`/api/sections/${sectionId}/scores`, { teacherId: tid })
  }
}

// ======================== 5. 管理员端 ========================

export const adminNewApi = {
  /**
   * 获取所有教学班列表（管理员课程管理页主数据源）
   * GET /api/sections?page=1&pageSize=100&status=
   */
  async getAllSections(params = {}) {
    return http.get('/api/sections', { page: 1, pageSize: 100, status: '', ...params })
  },

  /**
   * 获取教学班详情（含选课学生列表）
   * GET /api/sections/{id}
   */
  async getSectionDetail(id) {
    return http.get(`/api/sections/${id}`)
  },

  /**
   * 课程删除
   * GET /api/course/deleteByCourseId
   */
  async deleteCourse(courseId) {
    return http.get(`/api/course/deleteByCourseId`, { params: { courseId } })
  }
}

export default {
  auth: authApi,
  section: sectionApi,
  enrollment: enrollmentApi,
  teacher: teacherNewApi,
  admin: adminNewApi
}
