package com.richieoscar.artwoodcba.config.aop;

import org.aspectj.lang.JoinPoint;
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
public class LoggingAdvice {

    @Around("appPointCut()")
    public Object appLoggingAdvice(ProceedingJoinPoint joinPoint) {
        Logger logger = logger(joinPoint);
        Object[] args = joinPoint.getArgs();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Enter {}() with argument[s] = {}", joinPoint.getSignature().getName(), Arrays.toString(args));
            } else {
                logger.info("Enter {}() with argument[s] = {}", joinPoint.getSignature().getName(), Arrays.toString(args));
            }

            Object result = joinPoint.proceed();

            if (logger.isDebugEnabled()) {
                logger.debug("Exit {}() with result = {}", joinPoint.getSignature().getName(), result);
            } else {
                logger.info("Enter {}() with argument[s] = {}", joinPoint.getSignature().getName(), Arrays.toString(args));
            }
            return result;
        } catch (Throwable e) {
            logger.error("Error occurred: {} in {}()", Arrays.toString(joinPoint.getArgs()), joinPoint.getSignature().getName());
            throw new RuntimeException(e);
        }
    }

    @Pointcut(
        "within(com.richieoscar.artwoodcba.service..*)" +
        "|| within(com.richieoscar.artwoodcba.controller..*)" +
            "|| within(com.richieoscar.artwoodcba.repository..*)"
    )
    public void appPointCut() {}

    Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }
}
