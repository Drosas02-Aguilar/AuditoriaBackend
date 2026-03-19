package com.Auditorias.auditoria.Entity;

import java.util.List;


public class SeriveceResult<T> {
    
    public boolean correct;
    public int status;
    public String ErrorMessage;
    public T Object;
    public List<T> Objects;
    public Exception ex;
    
    
}
