package com.DogWalker.dog.walker.modelo.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reserva_usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservaUsuario {
    @Id
    @Column(name = "id_reserva_usuario")
    private int id_Reserva_Usuario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private  Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;
}