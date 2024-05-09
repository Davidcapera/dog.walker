package com.DogWalker.dog.walker.repositorio;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

}

