package com.Auditorias.auditoria.RestController;

import com.Auditorias.auditoria.DAO.IUsuario;
import com.Auditorias.auditoria.Entity.ServiceResult;
import com.Auditorias.auditoria.Entity.Usuarios;
import com.Auditorias.auditoria.JWT.JwtUtil;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/auth")
public class AuthRestcontroller {
    
    @Autowired
    private IUsuario iUsuario;
    
    
    @Autowired 
    private JwtUtil jtJwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<ServiceResult<Usuarios>> Login(
    @RequestBody Map<String,String> request){
        
       ServiceResult<Usuarios> result = new ServiceResult<>();
       
       try{
       
       String correo = request.get("correo");
       
       if(correo == null || correo.trim().isEmpty()){
       result.status = 400;
       result.ErrorMessage = "El correo es requerido";
       return ResponseEntity.status(result.status).body(result);
       }
       
           Optional<Usuarios> option = iUsuario.findByCorreo(correo.trim());
           if(!option.isPresent()){
           result.status = 404;
           result.ErrorMessage = "Usuario no encontrado";
           return ResponseEntity.status(result.status).body(result);
           }
           
           Usuarios usuarios = option.get();
           
           if(usuarios.getActivo() == 0){
           result.status = 403;
           result.ErrorMessage = "Usuario inactivo, contacte con el administrador";
           return ResponseEntity.status(result.status).body(result);
           }
           
           String token = jtJwtUtil.GenerarToken(usuarios.getCorreo(),usuarios.getRol());
           
           result.Object = usuarios;
           result.message = token;
           result.correct = true;
           result.status = 200;
           
           return ResponseEntity.status(result.status).body(result);
       
       }catch(Exception ex){
       
           result.status = 500;
           result.ErrorMessage = ex.getLocalizedMessage();
           return ResponseEntity.status(result.status).body(result);
       }
        
    }
     
}
