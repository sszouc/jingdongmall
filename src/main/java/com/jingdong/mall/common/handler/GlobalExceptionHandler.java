package com.jingdong.mall.common.handler;

import com.jingdong.mall.common.exception.BusinessException;
import com.jingdong.mall.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/*
* 捕获全局的错误，我们后端依旧只处理业务错误，不处理系统错误
* */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    //为什么要把@Valid单独拎出来？这是spring的特性决定的。因为在请求传入
    //controller的时候，会先检测一次参数是否错误，所以我们必须先把参数错误单独拿出来

    /**
     * 捕获 @Valid 异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        // 提取所有验证错误
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn(errorMessage);

        // 创建错误响应
        Result<?> result = Result.badRequest(errorMessage);

        return ResponseEntity.status(400).body(result);
    }


    /**
     * 处理业务异常
     */

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());

        // 根据业务错误码选择合适的HTTP状态码
        HttpStatus httpStatus = determineHttpStatus(e.getCode());

        Result<?> result = Result.error(e.getCode(), e.getMessage());
        return ResponseEntity.status(httpStatus).body(result);
    }

    /**
     * 根据业务错误码确定HTTP状态码
     */
    private HttpStatus determineHttpStatus(Integer businessCode) {
        // 可以根据业务错误码范围分配HTTP状态码
        if (businessCode >= 1000 && businessCode < 2000) {
            // 用户相关错误：400 Bad Request
            return HttpStatus.BAD_REQUEST;
        } else if (businessCode >= 2000 && businessCode < 3000) {
            // 商品相关错误：400 Bad Request
            return HttpStatus.BAD_REQUEST;
        } else if (businessCode == 401 || businessCode == 403) {
            // 认证授权错误
            return businessCode == 401 ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        } else {
            // 默认：400 Bad Request
            return HttpStatus.BAD_REQUEST;
        }
    }

}