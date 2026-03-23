package com.Auditorias.auditoria.Entity;

import java.util.List;


public class ServiceResult<T> {
    
    public boolean correct;
    public int status;
    public String ErrorMessage;
    public T Object;
    public List<T> Objects;
    public Exception ex;
    public String message;
    
    
}
