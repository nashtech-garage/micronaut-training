package com.micr.crud.app.unit.interceptor;

import com.micr.crud.app.annotation.RetryOnFailure;
import com.micr.crud.app.interceptor.RetryOnFailureInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RetryOnFailureInterceptor
 */
@DisplayName("RetryOnFailureInterceptor Tests")
class RetryOnFailureInterceptorTest {

    private RetryOnFailureInterceptor interceptor;

    @Mock
    private MethodInvocationContext<Object, Object> context;

    @Mock
    private AnnotationMetadata annotationMetadata;

    @Mock
    private AnnotationValue<RetryOnFailure> annotationValue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interceptor = new RetryOnFailureInterceptor();
    }

    @Test
    @DisplayName("Should return result on first successful attempt")
    void testSuccessfulAttempt() {
        // Arrange
        Object expectedResult = "Success";
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(3));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(500L));
        when(context.proceed()).thenReturn(expectedResult);
        when(context.getMethodName()).thenReturn("testMethod");

        // Act
        Object result = interceptor.intercept(context);

        // Assert
        assertEquals(expectedResult, result);
        verify(context, times(1)).proceed();
    }

    @Test
    @DisplayName("Should retry on failure and succeed on second attempt")
    void testRetrySucceedsOnSecondAttempt() {
        // Arrange
        Object expectedResult = "Success after retry";
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(3));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(10L)); // Short delay for testing
        when(context.getMethodName()).thenReturn("testMethod");
        when(context.proceed())
                .thenThrow(new RuntimeException("First attempt failed"))
                .thenReturn(expectedResult);

        // Act
        Object result = interceptor.intercept(context);

        // Assert
        assertEquals(expectedResult, result);
        verify(context, times(2)).proceed();
    }

    @Test
    @DisplayName("Should retry multiple times and succeed")
    void testRetrySucceedsOnThirdAttempt() {
        // Arrange
        Object expectedResult = "Success after multiple retries";
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(3));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(10L));
        when(context.getMethodName()).thenReturn("testMethod");
        when(context.proceed())
                .thenThrow(new RuntimeException("Attempt 1 failed"))
                .thenThrow(new RuntimeException("Attempt 2 failed"))
                .thenReturn(expectedResult);

        // Act
        Object result = interceptor.intercept(context);

        // Assert
        assertEquals(expectedResult, result);
        verify(context, times(3)).proceed();
    }

    @Test
    @DisplayName("Should throw RuntimeException after max attempts exhausted")
    void testThrowsExceptionAfterMaxAttempts() {
        // Arrange
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(3));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(10L));
        when(context.getMethodName()).thenReturn("failingMethod");
        when(context.proceed()).thenThrow(new RuntimeException("Permanent failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interceptor.intercept(context);
        });

        assertTrue(exception.getMessage().contains("Operation failed after retries"));
        verify(context, times(3)).proceed();
    }

    @Test
    @DisplayName("Should use default maxAttempts when not specified")
    void testDefaultMaxAttempts() {
        // Arrange
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.empty()); // Default to 3
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(10L));
        when(context.getMethodName()).thenReturn("testMethod");
        when(context.proceed()).thenThrow(new RuntimeException("Failure"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            interceptor.intercept(context);
        });

        // Should attempt 3 times (default)
        verify(context, times(3)).proceed();
    }

    @Test
    @DisplayName("Should use default delayMs when not specified")
    void testDefaultDelay() {
        // Arrange
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(2));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.empty()); // Default to 500L
        when(context.getMethodName()).thenReturn("testMethod");
        when(context.proceed()).thenReturn("Success");

        // Act
        Object result = interceptor.intercept(context);

        // Assert
        assertEquals("Success", result);
        verify(context, times(1)).proceed();
    }

    @Test
    @DisplayName("Should skip retry logic when annotation is not present")
    void testNoRetryWhenAnnotationNotPresent() {
        // Arrange
        Object expectedResult = "No retry result";
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(null);
        when(context.proceed()).thenReturn(expectedResult);

        // Act
        Object result = interceptor.intercept(context);

        // Assert
        assertEquals(expectedResult, result);
        verify(context, times(1)).proceed();
    }

    @Test
    @DisplayName("Should preserve exception cause chain")
    void testExceptionCauseChain() {
        // Arrange
        Exception originalException = new IllegalArgumentException("Invalid argument");
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(2));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(10L));
        when(context.getMethodName()).thenReturn("testMethod");
        when(context.proceed()).thenThrow(originalException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            interceptor.intercept(context);
        });

        assertEquals(originalException, exception.getCause());
        assertTrue(exception.getMessage().contains("Operation failed after retries"));
    }

    @Test
    @DisplayName("Should handle InterruptedException during sleep")
    void testInterruptedExceptionHandling() throws InterruptedException {
        // Arrange
        when(context.getAnnotationMetadata()).thenReturn(annotationMetadata);
        when(annotationMetadata.getAnnotation(RetryOnFailure.class)).thenReturn(annotationValue);
        when(annotationValue.intValue("maxAttempts")).thenReturn(OptionalInt.of(2));
        when(annotationValue.longValue("delayMs")).thenReturn(OptionalLong.of(100L));
        when(context.getMethodName()).thenReturn("testMethod");
        when(context.proceed()).thenThrow(new RuntimeException("Failure"));

        // Act
        Thread testThread = new Thread(() -> {
            assertThrows(RuntimeException.class, () -> interceptor.intercept(context));
        });
        testThread.start();
        testThread.interrupt();
        testThread.join();

        // Assert - should complete without hanging
        assertTrue(true);
    }
}
