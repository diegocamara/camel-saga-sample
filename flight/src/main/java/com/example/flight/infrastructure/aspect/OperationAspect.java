package com.example.flight.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import reactor.core.publisher.Mono;

@Aspect
// @Component
public class OperationAspect {

  @Around("@annotation(com.example.flight.infrastructure.aspect.Operation)")
  public Object operation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

    final var methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    final var returnType = methodSignature.getReturnType();

    if (Mono.class.isAssignableFrom(returnType)) {

      final var result = (Mono<?>) proceedingJoinPoint.proceed();

      return Mono.deferContextual(
          contextView -> {
            final var operationReferenceHeaderValue =
                contextView
                    .getOrEmpty(OperationReferenceWebFilter.OPERATION_REFERENCE_HEADER)
                    .map(operationReference -> (String) operationReference)
                    .orElseThrow();

            return result;
          });
    }

    return proceedingJoinPoint.proceed();

    //    return Mono.deferContextual(
    //            contextView ->
    //
    // Mono.just(contextView.get(OperationReferenceWebFilter.OPERATION_REFERENCE_HEADER)))
    //        .map(
    //            operationReferenceOptional -> {
    //              try {
    //                return proceedingJoinPoint.proceed();
    //              } catch (Throwable throwable) {
    //                throwable.printStackTrace();
    //              }
    //              return Mono.empty();
    //            });
  }
}
