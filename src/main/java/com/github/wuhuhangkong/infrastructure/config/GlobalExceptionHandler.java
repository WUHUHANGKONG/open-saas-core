package com.github.wuhuhangkong.infrastructure.config;

import com.github.wuhuhangkong.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 核心注解：拦截所有 Controller
public class GlobalExceptionHandler {

    /**
     * 拦截所有未知的运行时异常
     */
    @ExceptionHandler(Exception.class)
    public R<String> handleException(Exception e) {
        log.error("💥 系统异常: ", e); // 打印堆栈到控制台，方便排查
        // 返回给前端友好的提示
        return R.fail("系统繁忙，请稍后再试：" + e.getMessage());
    }

    /**
     * 拦截参数校验异常 (IllegalArgumentException)
     * 比如：Assert.notNull(xxx) 抛出的异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("⚠️ 参数错误: {}", e.getMessage());
        return R.fail(400, e.getMessage());
    }

    // 你以后还可以加自定义异常，比如 BusinessException
}