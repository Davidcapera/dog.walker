package com.DogWalker.dog.walker.modelo.entidades;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reserva")
public class Reserva {

    @Id
    @Column(name = "id_reserva")
    private int idReserva; // Corrección en el nombre de la columna del ID de reserva

    @Column(name = "fecha")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "id_entrenador")
    private Usuario entrenador;

}