package com.Auditorias.auditoria.DAO;

import com.Auditorias.auditoria.Entity.EventoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEventoAuditoria extends JpaRepository<EventoAuditoria, Integer> {

    Page<EventoAuditoria> findAllByOrderByHorarioeventoDesc(Pageable pageable);

}
