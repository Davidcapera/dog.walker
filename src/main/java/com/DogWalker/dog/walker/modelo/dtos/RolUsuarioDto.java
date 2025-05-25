package com.DogWalker.dog.walker.modelo.dtos;

public class RolUsuarioDto {

    private String rol;
    private Long cantidadUsuarios;

    // Constructor
    public RolUsuarioDto(String rol, Long cantidadUsuarios) {
        this.rol = rol;
        this.cantidadUsuarios = cantidadUsuarios;
    }

    // Getters y setters
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Long getCantidadUsuarios() {
        return cantidadUsuarios;
    }

    public void setCantidadUsuarios(Long cantidadUsuarios) {
        this.cantidadUsuarios = cantidadUsuarios;
    }
}
