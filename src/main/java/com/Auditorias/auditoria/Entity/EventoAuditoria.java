package com.Auditorias.auditoria.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTOAUDITORIA")
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento")
    private int idevento;
    @Column(name = "tipoevento", nullable = false, length = 150)
    private String tipoevento;
    @Column(name = "usuarioinvolucrado", nullable = false, length = 150)
    private String usuarioinvolucrado;
    @Column(name = "horarioevento", nullable = false)
    private LocalDateTime horarioevento;
    @Column(name = "descripcion")
    private String descripcion;

    public int getIdevento() {
        return idevento;
    }

    public void setIdevento(int idevento) {
        this.idevento = idevento;
    }

    public String getTipoevento() {
        return tipoevento;
    }

    public void setTipoevento(String tipoevento) {
        this.tipoevento = tipoevento;
    }

    public String getUsuarioinvolucrado() {
        return usuarioinvolucrado;
    }

    public void setUsuarioinvolucrado(String usuarioinvolucrado) {
        this.usuarioinvolucrado = usuarioinvolucrado;
    }

    public LocalDateTime getHorarioevento() {
        return horarioevento;
    }

    public void setHorarioevento(LocalDateTime horarioevento) {
        this.horarioevento = horarioevento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
