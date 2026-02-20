package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Usuario")
@SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_secuencia", allocationSize = 1)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    public Long id;

    public String usuario;
    public String password;
    public String rol;
}