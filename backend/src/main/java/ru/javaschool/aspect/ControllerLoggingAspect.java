package ru.javaschool.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Аспект, логирующий запросы к контроллерам и параметры запросов.
 */
@Component
@Aspect
@Slf4j
public class ControllerLoggingAspect {

    @Pointcut("within(ru.javaschool.documents.controller.*)")
    public void controllerLoggingPointcut() {
    }


    @Around("controllerLoggingPointcut()")
    public Object logControllerMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Controller method call: {}, parameters: {}", joinPoint.getSignature(), joinPoint.getArgs());
        Object result = joinPoint.proceed();
        log.info("Controller method result: {}", result);
        return result;
    }
}
