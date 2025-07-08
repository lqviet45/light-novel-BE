package com.lqviet.userservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@annotation(Loggable)")
    public void loggableMethods() {}

    @Pointcut("execution(* com.lqviet.userservice.services.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.lqviet.userservice.controllers.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.lqviet.userservice.repositories.*.*(..))")
    public void repositoryMethods() {}

    @Around("loggableMethods() || serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("Starting execution of {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            log.info("Successfully completed {}.{} in {} ms",
                    className, methodName, (endTime - startTime));

            return result;
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();

            log.error("Exception in {}.{} after {} ms: {}",
                    className, methodName, (endTime - startTime), ex.getMessage());

            throw ex;
        }
    }

    @Before("controllerMethods()")
    public void logControllerEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Controller method called: {} with args: {}", methodName, args);
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logControllerExit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Controller method completed: {} with result type: {}",
                methodName, result != null ? result.getClass().getSimpleName() : "null");
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logControllerExceptions(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("Exception in controller method {}: {}", methodName, ex.getMessage());
    }

    @Before("repositoryMethods()")
    public void logRepositoryAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("Repository method called: {}", methodName);
    }

    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "ex")
    public void logRepositoryExceptions(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("Repository exception in {}: {}", methodName, ex.getMessage());
    }
}