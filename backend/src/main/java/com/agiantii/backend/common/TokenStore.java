package com.agiantii.backend.common;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易 Token 存储（生产环境应使用 Redis）
 * 提供 Token 存取和身份校验，供 AuthController 及需要做身份比对的接口共用。
 */
@Component
public class TokenStore {

    private final ConcurrentHashMap<String, Map<String, Object>> store = new ConcurrentHashMap<>();

    public void put(String token, Map<String, Object> info) {
        store.put(token, new HashMap<>(info));
    }

    public Map<String, Object> get(String token) {
        return store.get(token);
    }

    /**
     * 解析 Authorization header 获取 token 信息。
     * @param authHeader Authorization header 值，格式 Bearer {token}
     * @return token 信息 Map，或 null（未登录/无效）
     */
    public Map<String, Object> resolve(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return store.get(authHeader.substring(7));
    }

    /**
     * 校验当前登录用户的 entityId 是否与目标 resourceId 一致。
     * @param authHeader Authorization header
     * @param resourceId 请求中包含的实体 ID（如 teacherId）
     * @return 校验通过返回 token 信息，不通过返回 null
     */
    public Map<String, Object> validateOwner(String authHeader, Integer resourceId) {
        Map<String, Object> info = resolve(authHeader);
        if (info == null) {
            return null;
        }
        Integer entityId = (Integer) info.get("entityId");
        if (entityId == null || !entityId.equals(resourceId)) {
            return null;
        }
        return info;
    }
}
