/**
 * 新版业务链 API 封装
 *
 * 业务链: course → course_section → enrollment → score
 * 所有方法优先调用新版接口，新版不可用时自动降级到旧版。
 * 联调时只需后端上线新版接口即可，前端零改动。
 */

import http from '@/api/index.js'

/**
 * 通用降级包装：先调新版 fn，失败则调旧版 fallback
 */
async function withFallback(fn, fallback) {
  try {
    const res = await fn()
    if (res && (res.code === 200 || res.status === 200)) return res
    // code != 200 也试 fallback
    return await fallback()
  } catch (_e) {
    return await fallback()
  }
}

// ======================== 1. 认证 ========================

export const authApi = {
  /**
   * 登录（统一入口）
   * POST /api/auth/login  → 降级 POST /api/user/login
   */
  async login(username, password) {
    return withFallback(
      () => http.post('/api/auth/login', { username, password }),
      () => http.post('/api/user/login', null, {
        params: { id: parseInt(username) || username, password }
      })
    )
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
    return withFallback(
      () => http.get('/api/sections', params),
      // 降级：查所有课程
      () => http.get('/api/course/selectAll')
    )
  },

  /**
   * 教学班详情
   * GET /api/sections/{id}
   */
  async getSectionDetail(id) {
    return withFallback(
      () => http.get(`/api/sections/${id}`),
      () => http.get(`/api/course/selectByCourseId?courseId=${id}`)
    )
  }
}

// ======================== 3. 选课 / 退课 ========================

export const enrollmentApi = {
  /**
   * 选课
   * POST /api/enrollments  → 降级 POST /api/score/insert
   */
  async enroll(studentId, sectionId) {
    return withFallback(
      () => http.post('/api/enrollments', { studentId, sectionId, source: '自主选课' }),
      () => http.post('/api/score/insert', {
        courseId: sectionId,
        studentId,
        teacherId: 0,
        score: 0
      })
    )
  },

  /**
   * 退课
   * DELETE /api/enrollments/{enrollmentId}  → 降级 /api/score/deleteByCourseIdAndStudentIdAndTeacherId
   */
  async drop(enrollmentId, sectionId, studentId, teacherId) {
    return withFallback(
      () => http.delete(`/api/enrollments/${enrollmentId}`),
      () => http.get(`/api/score/deleteByCourseIdAndStudentIdAndTeacherId`, {
        params: { courseId: sectionId, studentId, teacherId }
      })
    )
  },

  /**
   * 学生已选教学班列表（我的课程）
   * GET /api/students/{studentId}/enrollments?semester=&status=
   */
  async getMyEnrollments(studentId, params = {}) {
    return withFallback(
      () => http.get(`/api/students/${studentId}/enrollments`, params),
      () => http.get(`/api/score/selectByStudentId?studentId=${studentId}`)
    )
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
   * 课程删除 - 当前后端没有直接删除 sections 的接口
   * 降级使用旧版（可能不可用）
   */
  async deleteCourse(courseId, teacherId) {
    return withFallback(
      () => http.get(`/api/course/deleteByCourseId`, { params: { courseId } }),
      () => http.get(`/api/course/deleteByCourseIdAndTeacherId`, {
        params: { courseId, teacherId: teacherId || 0 }
      })
    )
  }
}

export default {
  auth: authApi,
  section: sectionApi,
  enrollment: enrollmentApi,
  teacher: teacherNewApi,
  admin: adminNewApi
}
