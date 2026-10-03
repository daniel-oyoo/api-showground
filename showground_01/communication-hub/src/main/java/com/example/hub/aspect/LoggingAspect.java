package com.example.hub.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /** Pointcut: every public method inside com.example.hub (excluding the aspect itself). */
    @Pointcut("execution(public * com.example.hub..*(..)) && !within(com.example.hub.aspect..*)")
    public void applicationMethods() {}

    @Around("applicationMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args     = joinPoint.getArgs();

        log.info("→ {}.{}() called with args: {}", className, methodName, safeArgs(args));
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("← {}.{}() returned: {} ({} ms)",
                    className, methodName, safeValue(result), elapsed);
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("✖ {}.{}() threw {}: {} ({} ms)",
                    className, methodName, ex.getClass().getSimpleName(), ex.getMessage(), elapsed);
            throw ex;
        }
    }

    /** Avoid logging huge/messy objects and mask common sensitive fields. */
    private String safeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        return Arrays.stream(args).map(this::safeValue).toList().toString();
    }

    private String safeValue(Object o) {
        if (o == null) return "null";
        String s = o.toString();
        if (s.length() > 300) s = s.substring(0, 300) + "...(truncated)";
        // Mask common secrets just in case
        return s.replaceAll("(?i)(password|token|api[-_]?key|authorization)=\\S+", "$1=***");
    }
}