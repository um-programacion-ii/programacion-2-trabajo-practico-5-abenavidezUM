package com.empleados.sistema.controller;

import com.empleados.sistema.exception.DepartamentoNoEncontradoException;
import com.empleados.sistema.exception.EmailDuplicadoException;
import com.empleados.sistema.exception.EmpleadoNoEncontradoException;
import com.empleados.sistema.exception.ProyectoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EmpleadoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleEmpleadoNoEncontrado(
            EmpleadoNoEncontradoException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Empleado no encontrado",
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(DepartamentoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleDepartamentoNoEncontrado(
            DepartamentoNoEncontradoException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Departamento no encontrado",
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(ProyectoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProyectoNoEncontrado(
            ProyectoNoEncontradoException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Proyecto no encontrado",
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(
            EmailDuplicadoException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Email duplicado",
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Datos inválidos",
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(
            IllegalStateException ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Estado inválido",
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Errores de validación");
        response.put("message", "Los datos proporcionados no son válidos");
        response.put("validationErrors", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ha ocurrido un error inesperado. Por favor contacte al administrador.",
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    private Map<String, Object> createErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status);
        response.put("error", error);
        response.put("message", message);
        response.put("path", path.replace("uri=", ""));
        return response;
    }
}
