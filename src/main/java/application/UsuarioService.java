package application;

import application.representation.UsuarioRepresentation;
import domain.Usuario;
import infraestructure.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    public UsuarioRepresentation findByUsuario(String user) {
        Usuario u = usuarioRepository.find("usuario", user).firstResult();
        if (u == null) return null;

        UsuarioRepresentation ur = new UsuarioRepresentation();
        ur.setUsuario(u.usuario);
        ur.setPassword(u.password);
        ur.setRol(u.rol);
        return ur;
    }
}