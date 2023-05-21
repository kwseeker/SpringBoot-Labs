package top.kwseeker.lab.security.core.authentication.check;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import top.kwseeker.lab.security.core.exception.GlobalErrorCodeConstants;
import top.kwseeker.lab.security.core.exception.ServiceExceptionUtil;
import top.kwseeker.lab.security.core.util.SecurityFrameworkUtils;

@Aspect
@Slf4j
public class PreAuthenticatedAspect {

    /**
     * 拦截请求，从安全上下文中获取认证信息
     */
    @Around("@annotation(preAuthenticated)")
    public Object around(ProceedingJoinPoint joinPoint, PreAuthenticated preAuthenticated) throws Throwable {
        log.info("PreAuthenticated aspect called");
        if (SecurityFrameworkUtils.getLoginUser() == null) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.UNAUTHORIZED);
        }
        return joinPoint.proceed();
    }

}
