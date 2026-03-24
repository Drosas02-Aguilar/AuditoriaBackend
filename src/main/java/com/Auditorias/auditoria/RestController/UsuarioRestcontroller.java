package com.Auditorias.auditoria.RestController;

import com.Auditorias.auditoria.Entity.ServiceResult;
import com.Auditorias.auditoria.Entity.Usuarios;
import com.Auditorias.auditoria.Service.ExportService;
import com.Auditorias.auditoria.Service.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestcontroller {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ExportService exportService;

    @GetMapping
    public ResponseEntity<ServiceResult<Usuarios>> ListarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Integer activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ServiceResult<Usuarios> result = new ServiceResult<>();

        try {

            List<Usuarios> usuarios = usuarioService.ListarUsuarios(rol, activo, page, size);
            if (!usuarios.isEmpty()) {
                result.Objects = usuarios;
                result.correct = true;
                result.status = 200;
                result.message = "Usuarios Encontrados";
            } else {
                result.status = 404;
                result.ErrorMessage = "No se encontraron usuarios";
            }

        } catch (Exception ex) {
            result.status = 500;
            result.ErrorMessage = ex.getLocalizedMessage();
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResult<Usuarios>> ObtenerPorId(
            @PathVariable int id) {
        ServiceResult<Usuarios> result = new ServiceResult<>();
        try {
            Usuarios usuarios = usuarioService.Obtenerporid(id);

            if (usuarios != null) {
                result.Object = usuarios;
                result.correct = true;
                result.status = 200;
                result.message = "Usuario encontrado";
            } else {
                result.status = 404;
                result.ErrorMessage = "Usuario no encontrado con el id:" + id;
            }
        } catch (Exception ex) {
            result.status = 500;
            result.ErrorMessage = ex.getLocalizedMessage();
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping
    public ResponseEntity<ServiceResult<Usuarios>> CrearUsuario(
            @RequestBody Usuarios usuarios) {

        ServiceResult<Usuarios> result = new ServiceResult<>();
        try {
                Usuarios creado = usuarioService.CrearUsuarios(usuarios);

            if (creado != null) {
                result.Object = creado;
                result.correct = true;
                result.status = 201;
                result.message = "Usuario creado correctamente";
            } else {
                result.status = 400;
                result.ErrorMessage = "No se pudo crear el usuario";
            }

        } catch (Exception ex) {
            result.status = 500;
            result.ErrorMessage = ex.getLocalizedMessage();
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResult<Usuarios>> ActualizarUsuario(
            @PathVariable int id,
            @RequestBody Usuarios datosNuevos) {
        ServiceResult<Usuarios> result = new ServiceResult<>();
        try {
                Usuarios actualizado = usuarioService.ActualizarUsuario(id, datosNuevos);
            if (actualizado != null) {
                result.Object = actualizado;
                result.correct = true;
                result.status = 200;
                result.message = "Usuario actualizado";
            } else {
                result.status = 404;
                result.ErrorMessage = "usuario no se puede actualizar, no se encontro: " + id;
            }
        } catch (Exception ex) {

            result.status = 500;
            result.ErrorMessage = ex.getLocalizedMessage();
        }
        return ResponseEntity.status(result.status).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceResult<Usuarios>> DesactivarUsuario(
            @PathVariable int id) {

        ServiceResult<Usuarios> result = new ServiceResult<>();
        try {
            Usuarios desctivado = usuarioService.DesactivarUsuario(id);
            if (desctivado != null) {
                result.Object = desctivado;
                result.correct = true;
                result.status = 200;
                result.message = "Usuario desactivado con id:" + id;

            } else {
                result.status = 404;
                result.ErrorMessage = "Usuario no encontrado con id:" + id;
            }

        } catch (Exception ex) {
            result.status = 500;
            result.ErrorMessage = ex.getLocalizedMessage();
        }
        return ResponseEntity.status(result.status).body(result);

    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> ExportatCSV() {
        try {

            byte[] contenido = exportService.ExportarCSV();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "usuarios.csv");
            return ResponseEntity.ok().headers(headers).body(contenido);
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }

    }
    
      @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> ExportarPDF() {
        try {
            byte[] contenido = exportService.exportarPDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "usuarios.pdf");
            return ResponseEntity.ok().headers(headers).body(contenido);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
