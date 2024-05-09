package com.DogWalker.dog.walker.modelo.entidades;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rol")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Rol {
    @Id
    @Column(name = "id_rol")
    private int id_rol;

    @Column(name= "rol")
    private String rol;

    public int getId() {
        return id_rol;
    }

    public void setId(int id) {
        this.id_rol = id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
