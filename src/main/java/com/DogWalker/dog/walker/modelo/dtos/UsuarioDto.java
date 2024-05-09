package com.DogWalker.dog.walker.modelo.dtos;

public class UsuarioDto {

    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;
    private String rol;
    private int id;


    // Constructor
    public UsuarioDto() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
