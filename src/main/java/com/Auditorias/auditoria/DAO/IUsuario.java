
package com.Auditorias.auditoria.DAO;

import com.Auditorias.auditoria.Entity.Usuarios;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IUsuario  extends JpaRepository<Usuarios, Integer>{
    
    Page<Usuarios> findByRolAndActivo(String rol, int activo, Pageable pageable);
    Page<Usuarios> findByrol(String rol, Pageable pageable);
    Page<Usuarios> findByActivo(int activo, Pageable pageable);
    Optional<Usuarios>findByCorreo(String correo);
    List<Usuarios>findByActivo(int activo);
    
}
