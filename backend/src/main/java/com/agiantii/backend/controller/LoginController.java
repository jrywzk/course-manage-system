package com.agiantii.backend.controller;


import com.agiantii.backend.common.Constant;
import com.agiantii.backend.common.R;
import com.agiantii.backend.mapper.UserMapper;
import com.agiantii.backend.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Slf4j
@RestController
public class LoginController {

    @Resource
    private UserMapper userMapper;
    // 【已废弃】旧版登录接口，现已迁移到 AuthController，此端点已下线
    // @PostMapping("/login")
    public R<String> login(@RequestParam(value = "id",required = true) Integer id, @RequestParam(value = "password" ,required = true) String password) {
            log.info("login id:{},password:{}",id,password);
            User user = userMapper.selectUserByIdAndPassword(id,password);
            if(user != null){
                return R.success(user.getRole(), Constant.LOGIN_SUCCESS).add("user_id",user.getId());
            }
            return R.error(Constant.LOGIN_FAIL);
    }
}
