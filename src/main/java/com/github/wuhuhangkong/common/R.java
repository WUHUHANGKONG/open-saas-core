package com.github.wuhuhangkong.common;

import lombok.Data;

/**
 * 通用返回结果封装类
 * @param <T> 数据类型
 */
@Data
public class R<T> {

    private Integer code; // 状态码：200成功，500失败
    private String msg;   // 提示信息
    private T data;       // 返回的数据

    // 私有构造，强制通过静态方法创建
    private R() {}

    // ✅ 成功 - 带数据
    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    // ✅ 成功 - 不带数据
    public static <T> R<T> ok() {
        return ok(null);
    }

    // ❌ 失败 - 带错误信息
    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    // ❌ 失败 - 带错误码和信息
    public static <T> R<T> fail(Integer code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}