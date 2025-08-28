package com.classes.config;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommonPointcuts {
    @Pointcut("execution(* com.classes.services.*.*(..))")
    public void greetingLoggerServices(){};

    @Pointcut("execution(* com.classes.controllers.*.*(..))")
    public void greetingLoggerControllers(){};
}
