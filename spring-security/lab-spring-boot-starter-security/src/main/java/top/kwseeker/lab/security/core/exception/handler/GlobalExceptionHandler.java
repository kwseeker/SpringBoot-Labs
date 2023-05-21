package top.kwseeker.lab.security.core.exception.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.kwseeker.lab.security.core.exception.GlobalErrorCodeConstants;
import top.kwseeker.lab.security.core.exception.ServiceException;
import top.kwseeker.lab.security.core.response.CommonResult;
import top.kwseeker.lab.security.core.util.WebFrameworkUtils;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    //private final String applicationName;

    /**
     * 处理所有异常，主要是提供给 Filter 使用
     * 因为 Filter 不走 SpringMVC 的流程，但是我们又需要兜底处理异常，所以这里提供一个全量的异常处理过程，保持逻辑统一。
     *
     * @param request 请求
     * @param ex 异常
     * @return 通用返回
     */
    public CommonResult<?> allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof AccessDeniedException) {
            return accessDeniedExceptionHandler(request, (AccessDeniedException) ex);
        }
        return defaultExceptionHandler(request, ex);
    }

//    /**
//     * 处理 SpringMVC 请求参数缺失
//     *
//     * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
//     */
//    @ExceptionHandler(value = MissingServletRequestParameterException.class)
//    public CommonResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
//        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
//        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
//    }
//
//    /**
//     * 处理 SpringMVC 请求参数类型错误
//     *
//     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
//     */
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public CommonResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
//        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
//        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
//    }
//
//    /**
//     * 处理 SpringMVC 参数校验不正确
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public CommonResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
//        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
//        FieldError fieldError = ex.getBindingResult().getFieldError();
//        assert fieldError != null; // 断言，避免告警
//        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
//    }
//
//    /**
//     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
//     */
//    @ExceptionHandler(BindException.class)
//    public CommonResult<?> bindExceptionHandler(BindException ex) {
//        log.warn("[handleBindException]", ex);
//        FieldError fieldError = ex.getFieldError();
//        assert fieldError != null; // 断言，避免告警
//        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
//    }
//
//    /**
//     * 处理 Validator 校验不通过产生的异常
//     */
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
//        log.warn("[constraintViolationExceptionHandler]", ex);
//        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
//        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
//    }
//
//    /**
//     * 处理 Dubbo Consumer 本地参数校验时，抛出的 ValidationException 异常
//     */
//    @ExceptionHandler(value = ValidationException.class)
//    public CommonResult<?> validationException(ValidationException ex) {
//        log.warn("[constraintViolationExceptionHandler]", ex);
//        // 无法拼接明细的错误信息，因为 Dubbo Consumer 抛出 ValidationException 异常时，是直接的字符串信息，且人类不可读
//        return CommonResult.error(BAD_REQUEST);
//    }
//
//    /**
//     * 处理 SpringMVC 请求地址不存在
//     *
//     * 注意，它需要设置如下两个配置项：
//     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
//     * 2. spring.mvc.static-path-pattern 为 /statics/**
//     */
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public CommonResult<?> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
//        log.warn("[noHandlerFoundExceptionHandler]", ex);
//        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在:%s", ex.getRequestURL()));
//    }
//
//    /**
//     * 处理 SpringMVC 请求方法不正确
//     *
//     * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
//     */
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public CommonResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
//        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
//        return CommonResult.error(METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
//    }
//
////    /**
////     * 处理 Resilience4j 限流抛出的异常
////     */
////    @ExceptionHandler(value = RequestNotPermitted.class)
////    public CommonResult<?> requestNotPermittedExceptionHandler(HttpServletRequest req, RequestNotPermitted ex) {
////        log.warn("[requestNotPermittedExceptionHandler][url({}) 访问过于频繁]", req.getRequestURL(), ex);
////        return CommonResult.error(TOO_MANY_REQUESTS);
////    }

    /**
     * 处理 Spring Security 权限不足的异常
     *
     * 来源是，使用 @PreAuthorize 注解，AOP 进行权限拦截
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public CommonResult<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) 无法访问 url({})]", WebFrameworkUtils.getLoginUserId(req),
                req.getRequestURL(), ex);
        return CommonResult.error(GlobalErrorCodeConstants.FORBIDDEN);
    }

    /**
     * 处理业务异常 ServiceException
     *
     * 例如说，商品库存不足，用户手机号已存在。
     */
    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        log.info("[serviceExceptionHandler]", ex);
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
        log.error("[defaultExceptionHandler]", ex);
        // 插入异常日志
        //this.createExceptionLog(req, ex);
        // 返回 ERROR CommonResult
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getMsg());
    }
}