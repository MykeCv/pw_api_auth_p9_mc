package interfaces;

import java.time.Instant;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import application.UsuarioService;
import application.representation.UsuarioRepresentation;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UsuarioService usuarioService;

    @ConfigProperty(name = "auth.issuer", defaultValue = "concesionaria-auth")
    String issuer;

    @ConfigProperty(name = "auth.token.ttl", defaultValue = "36500")
    long ttl;

    @GET
    @Path("/token")
    public Response token(@QueryParam("user") String user,
                          @QueryParam("password") String password,
                          @QueryParam("rol") String rol) {

        if (user == null || user.isBlank() || password == null || password.isBlank()) {
            return Response.status(400).entity(new ErrorResponse("Debe enviar user y password.")).build();
        }

        UsuarioRepresentation u = usuarioService.findByUsuario(user);
        if (u == null || u.getPassword() == null || !u.getPassword().equals(password)) {
            return Response.status(401).entity(new ErrorResponse("Credenciales inválidas.")).build();
        }

        String rolFinal = (rol != null && !rol.isBlank()) ? rol : u.getRol();

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttl);

        String jwt = Jwt.issuer(issuer)
                .subject(user)
                .groups(Set.of(rolFinal))
                .issuedAt(now)
                .expiresAt(exp)
                .sign();

        return Response.ok(new TokenResponse(jwt, exp.getEpochSecond(), rolFinal)).build();
    }

    public static class TokenResponse {
        public String accessToken;
        public long expiresAt;
        public String rol;

        public TokenResponse() {}
        public TokenResponse(String accessToken, long expiresAt, String rol) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.rol = rol;
        }
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse() {}
        public ErrorResponse(String message) { this.message = message; }
    }
}