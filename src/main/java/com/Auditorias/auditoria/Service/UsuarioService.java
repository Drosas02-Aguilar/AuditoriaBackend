package com.Auditorias.auditoria.Service;

import com.Auditorias.auditoria.DAO.IUsuario;
import com.Auditorias.auditoria.Entity.Usuarios;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private IUsuario iUsuario;
    @Autowired
    private AuditoriaService auditoriaService;

    public List<Usuarios> ListarUsuarios(String rol, Integer activo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("fecharegistro").descending());
        Page<Usuarios> pagina;

        if (rol != null && activo != null) {
            pagina = iUsuario.findByRolAndActivo(rol, activo, pageable);
        } else if (rol != null) {
            pagina = iUsuario.findByrol(rol, pageable);
        } else if (activo != null) {
            pagina = iUsuario.findByActivo(activo, pageable);
        } else {
            pagina = iUsuario.findAll(pageable);
        }

        return pagina.getContent();
    }

    public Usuarios Obtenerporid(int id) {
        Optional<Usuarios> opt = iUsuario.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Transactional
    public Usuarios CrearUsuarios(Usuarios usuarios) {
        usuarios.setActivo(1);
        usuarios.setFecharegistro(LocalDateTime.now());
        iUsuario.save(usuarios);

        auditoriaService.RegistrarEvento(
                "CREAR_USUARIO",
                usuarios.getCorreo(),
                "Se creo el usuario:" + usuarios.getNombre()
                + " | Rol: " + usuarios.getRol()
        );
        return usuarios;
    }

    @Transactional
    public Usuarios ActualizarUsuario(int id, Usuarios datosNuevos) {
        Optional<Usuarios> opt = iUsuario.findById(id);

        if (opt.isPresent()) {
            Usuarios usuarios = opt.get();
            usuarios.setNombre(datosNuevos.getNombre());
            usuarios.setCorreo(datosNuevos.getCorreo());
            usuarios.setRol(datosNuevos.getRol());
            iUsuario.save(usuarios);

            auditoriaService.RegistrarEvento(
                    "ACTUALIZAR_USUARIO", usuarios.getCorreo(), "Se actualizo el usuario: "
                    + usuarios.getNombre() + " | Nuevo rol: " + usuarios.getRol()
            );

            return usuarios;
        }

        return null;
    }

    public Usuarios DesactivarUsuario(int id) {
        Optional<Usuarios> opt = iUsuario.findById(id);

        if (opt.isPresent()) {
            Usuarios usuarios = opt.get();
            usuarios.setActivo(0);
            iUsuario.save(usuarios);

            auditoriaService.RegistrarEvento("DESACTIVAR_USUARIO", usuarios.getCorreo(), "Se desactivo el usuario: " + usuarios.getNombre());

            return usuarios;
        }
        return null;
    }

    public List<Usuarios> ObtenerTodosActivos() {
        return iUsuario.findByActivo(1);
    }

}
