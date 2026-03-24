package com.Auditorias.auditoria.Service;

import com.Auditorias.auditoria.DAO.IEventoAuditoria;
import com.Auditorias.auditoria.Entity.EventoAuditoria;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    @Autowired
    private IEventoAuditoria iEventoAuditoria;

    public void RegistrarEvento(String tipoEvento, String usuarioInvolucrado, String descripcion) {

        EventoAuditoria evento = new EventoAuditoria();

        evento.setTipoevento(tipoEvento);
        evento.setUsuarioinvolucrado(usuarioInvolucrado);
        evento.setHorarioevento(LocalDateTime.now());
        evento.setDescripcion(descripcion);

        iEventoAuditoria.save(evento);

    }

    public List<EventoAuditoria> ListarEventos(int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("horarioevento").descending());

        Page<EventoAuditoria> pagina = iEventoAuditoria.findAllByOrderByHorarioeventoDesc(pageable);
        return pagina.getContent();

    }

}
