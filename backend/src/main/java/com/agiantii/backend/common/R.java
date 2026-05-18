package com.agiantii.backend.common;


import lombok.Data;

import java.util.HashMap;


@Data
public class R<T> {

    private Integer status;

    private String msg;


    private T data;


    private HashMap<String,Object> map = new HashMap<>();
    public static <T> R<T> success(String message) {
        R<T> r = new R<T>();
        r.msg = message;
        r.status = 200;
        return r;
    }
    public static <T> R<T> success(T object,String message) {
        R<T> r = new R<T>();
        r.data = object;
        r.status = 200;
        r.msg = message;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.status = 500;
        return r;
    }
    public static <T> R<T> error(String msg,int code) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.status = code;
        return r;
    }
    public static <T> R<T> error(T data, String msg) {
        R<T> r = new R<T>();
        r.data = data;
        r.msg = msg;
        r.status = 400;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
