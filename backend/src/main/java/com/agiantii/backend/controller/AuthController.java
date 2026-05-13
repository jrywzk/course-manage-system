package com.agiantii.backend.controller;

import com.agiantii.backend.common.R;
import com.agiantii.backend.mapper.AdminMapper;
import com.agiantii.backend.mapper.StudentMapper;
import com.agiantii.backend.mapper.TeacherMapper;
import com.agiantii.backend.mapper.UserMapper;
import com.agiantii.backend.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "P0-认证接口")
public class AuthController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private AdminMapper adminMapper;

    // 简易Token存储（生产环境应使用Redis）
    private static final ConcurrentHashMap<String, Map<String, Object>> tokenStore = new ConcurrentHashMap<>();

    @PostMapping("/login")
    @ApiOperation("用户登录（统一入口）")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return R.error("用户名和密码不能为空", 400);
        }

        log.info("login username: {}", username);
        User user = userMapper.selectByUsernameAndPassword(username, password);

        if (user == null) {
            return R.error("用户名或密码错误", 401);
        }

        if (user.getStatus() == 0) {
            return R.error("账号已停用", 403);
        }

        // 获取真实姓名和实体ID
        String realName = "";
        Integer entityId = null;

        switch (user.getRole()) {
            case "student":
                realName = studentMapper.selectNameByUserId(user.getId());
                entityId = studentMapper.selectIdByUserId(user.getId());
                break;
            case "teacher":
                realName = teacherMapper.selectNameByUserId(user.getId());
                entityId = teacherMapper.selectIdByUserId(user.getId());
                break;
            case "admin":
                realName = adminMapper.selectNameByUserId(user.getId());
                entityId = adminMapper.selectIdByUserId(user.getId());
                break;
        }

        // 生成Token
        String token = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", user.getId());
        tokenInfo.put("username", user.getUsername());
        tokenInfo.put("role", user.getRole());
        tokenInfo.put("realName", realName);
        tokenInfo.put("entityId", entityId);
        tokenStore.put(token, tokenInfo);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", user.getRole());
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", realName);
        data.put("entityId", entityId);
        // 同时提供角色特定字段方便前端对接
        data.put(user.getRole() + "Id", entityId);

        log.info("login success: username={}, role={}", username, user.getRole());
        return R.success(data, "登录成功");
    }

    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    @ApiImplicitParam(name = "Authorization", value = "Bearer {token}", required = true, dataType = "string", paramType = "header")
    public R<Map<String, Object>> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return R.error("未登录", 401);
        }

        String token = authHeader.substring(7);
        Map<String, Object> tokenInfo = tokenStore.get(token);
        if (tokenInfo == null) {
            return R.error("Token无效或已过期", 401);
        }

        Integer userId = (Integer) tokenInfo.get("userId");
        String role = (String) tokenInfo.get("role");
        User user = userMapper.selectUserById(userId);

        if (user == null) {
            return R.error("用户不存在", 404);
        }

        Map<String, Object> data = new HashMap<>(tokenInfo);
        data.put("status", user.getStatus());

        return R.success(data, "成功");
    }
}
