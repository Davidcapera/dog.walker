package com.DogWalker.dog.walker.repositorio;
import com.DogWalker.dog.walker.modelo.dtos.RolUsuarioDto;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {


    // Metodo para traer el usuario por el correo
    Usuario findByCorreo(String correo);
    Optional<Usuario> findById(int id);
    List<Usuario> findByRolId(int rolId);
    @Query("SELECT new com.DogWalker.dog.walker.modelo.dtos.RolUsuarioDto(r.rol, COUNT(u.correo)) FROM Usuario u LEFT JOIN u.rol r GROUP BY r.rol")
    List<RolUsuarioDto> countUsuariosPorRol();
}