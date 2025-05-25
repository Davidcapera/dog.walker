package com.DogWalker.dog.walker.modelo.dtos;

import java.time.LocalDateTime;

public class MensajeChatDto {

    private int id;
    private int emisorId;
    private int receptorId;
    private String contenido;
    private LocalDateTime fechaHora;

    public MensajeChatDto() {}

    public MensajeChatDto(int id, int emisorId, int receptorId, String contenido, LocalDateTime fechaHora) {
        this.id = id;
        this.emisorId = emisorId;
        this.receptorId = receptorId;
        this.contenido = contenido;
        this.fechaHora = fechaHora;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmisorId() { return emisorId; }
    public void setEmisorId(int emisorId) { this.emisorId = emisorId; }
    public int getReceptorId() { return receptorId; }
    public void setReceptorId(int receptorId) { this.receptorId = receptorId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
