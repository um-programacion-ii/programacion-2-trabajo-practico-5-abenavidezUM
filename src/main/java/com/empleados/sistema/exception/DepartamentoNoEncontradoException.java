package com.empleados.sistema.exception;

public class DepartamentoNoEncontradoException extends RuntimeException {
    
    public DepartamentoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public DepartamentoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
