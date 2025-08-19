package com.empleados.sistema.exception;

public class EmpleadoNoEncontradoException extends RuntimeException {
    
    public EmpleadoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public EmpleadoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
