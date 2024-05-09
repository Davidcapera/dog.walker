package com.DogWalker.dog.walker.modelo.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reserva {

    @Id
    @Column(name = "id_Reserva")
    private int  id_Reserva;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "id_entrenador")
    private Usuario entrenador;

    @Column(name = "fecha")
    private LocalDateTime fecha;

}