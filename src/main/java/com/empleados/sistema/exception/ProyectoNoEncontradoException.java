package com.empleados.sistema.exception;

public class ProyectoNoEncontradoException extends RuntimeException {
    
    public ProyectoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public ProyectoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
