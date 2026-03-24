package com.Auditorias.auditoria.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIOS")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private int idusuario;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "correo", nullable = false, length = 150, unique = true)
    private String correo;
    @Column(name = "rol", nullable = false, length = 20)
    private String rol;
    @Column(name = "activo", nullable = false)
    private int activo;
    @Column(name = "fecharegistro", nullable = false)
    private LocalDateTime fecharegistro;

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public LocalDateTime getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(LocalDateTime fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

}
