package com.empleados.sistema.exception;

public class EmailDuplicadoException extends RuntimeException {
    
    public EmailDuplicadoException(String mensaje) {
        super(mensaje);
    }
    
    public EmailDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
