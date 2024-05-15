package com.DogWalker.dog.walker.repositorio;
import com.DogWalker.dog.walker.modelo.entidades.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    @Query("SELECT r FROM Reserva r WHERE r.usuario.id_usuario = :idUsuario")
    List<Reserva> findByUsuario_IdUsuario(int idUsuario);
    Reserva findByIdReserva(int idReserva);
    @Query("SELECT r FROM Reserva r WHERE r.entrenador.id_usuario = :idEntrenador")
    List<Reserva> findByEntrenador_IdEntrenador(int idEntrenador);
}