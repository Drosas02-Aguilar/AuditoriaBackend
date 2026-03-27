package com.Auditorias.auditoria.RestController;

import com.Auditorias.auditoria.Entity.EventoAuditoria;
import com.Auditorias.auditoria.Entity.ServiceResult;
import com.Auditorias.auditoria.Service.AuditoriaService;
import com.Auditorias.auditoria.Service.ExportService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @Autowired
    private ExportService exportService;

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

    
     @GetMapping("/export/csv")
    public ResponseEntity<byte[]> ExportarCSV() {
                ServiceResult<EventoAuditoria> result = new ServiceResult<>();

        try {
            byte[] contenido = exportService.ExportarAuditoriaCSV();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "auditoria.csv");
            return ResponseEntity.ok().headers(headers).body(contenido);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    
     @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> ExportarPDF() {
        try {
            byte[] contenido = exportService.ExportarAuditoriaPDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "auditoria.pdf");
            return ResponseEntity.ok().headers(headers).body(contenido);
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }
   
}
