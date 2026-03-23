package com.Auditorias.auditoria.RestController;

import com.Auditorias.auditoria.Entity.EventoAuditoria;
import com.Auditorias.auditoria.Entity.ServiceResult;
import com.Auditorias.auditoria.Service.AuditoriaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaRestcontroller {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<ServiceResult<EventoAuditoria>> ListarEventos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ServiceResult<EventoAuditoria> result = new ServiceResult<>();
        try {
            List<EventoAuditoria> eventos
                    = auditoriaService.ListarEventos(page, size);
            if (!eventos.isEmpty()) {
                result.Objects = eventos;
                result.correct = true;
                result.status = 200;
                result.message = "Auditorias encontradas";
            } else {
                result.status = 404;
                result.ErrorMessage = "No se encontraron evenetos de auditoria";
            }
        } catch (Exception ex) {
            result.status = 500;
            result.ErrorMessage = ex.getLocalizedMessage();

        }

        return ResponseEntity.status(result.status).body(result);

    }

}
