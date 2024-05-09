package com.DogWalker.dog.walker.modelo.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servicio")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Servicio {
    @Id
    @Column (name = "id_servicio")
    private int id_servicio;

    @Column(name = "servicio")
    private String servicio;

    @Column(name="descripcion")
    private String descripcion;

}