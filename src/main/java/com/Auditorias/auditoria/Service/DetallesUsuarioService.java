package com.Auditorias.auditoria.Service;

import com.Auditorias.auditoria.DAO.IUsuario;
import com.Auditorias.auditoria.Entity.Usuarios;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetallesUsuarioService implements UserDetailsService {

    @Autowired
    private IUsuario iUsuario;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Usuarios usuarios = iUsuario.findByCorreo(correo).orElseThrow(()
                -> new UsernameNotFoundException("correo no encontrado" + correo));

        if (usuarios.getActivo() == 0) {
            throw new UsernameNotFoundException("Usuario inactivo" + correo);

        }

        return new User(
                usuarios.getCorreo(), "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuarios.getRol()))
        );

    }

}
