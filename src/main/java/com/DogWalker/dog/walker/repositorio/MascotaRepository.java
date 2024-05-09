package com.DogWalker.dog.walker.repositorio;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    List<Mascota> findByUsuario(Usuario usuario);
    void deleteById(int id);
    @Query("SELECT m FROM Mascota m WHERE m.id_mascota = :id_mascota")
    Mascota findByid_mascota(int id_mascota);
}