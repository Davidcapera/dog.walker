package com.DogWalker.dog.walker.repositorio;
import com.DogWalker.dog.walker.modelo.entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Rol findByRol(String rol);
}
