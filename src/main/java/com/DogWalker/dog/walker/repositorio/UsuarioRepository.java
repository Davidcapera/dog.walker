package com.DogWalker.dog.walker.repositorio;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {


    // Metodo para traer el usuario por el correo
    Usuario findByCorreo(String correo);
    Optional<Usuario> findById(int id);
    List<Usuario> findByRolId(int rolId);
}