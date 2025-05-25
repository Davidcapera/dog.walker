package com.DogWalker.dog.walker.servicio;
import com.DogWalker.dog.walker.modelo.dtos.UsuarioDto;
import com.DogWalker.dog.walker.modelo.entidades.Rol;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.repositorio.RolRepository;
import com.DogWalker.dog.walker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void registrarUsuario(UsuarioDto usuarioDTO, String rolNombre) throws Exception {
        try {

            // Verificar si el usuario ya existe en la base de datos
            Usuario usuarioExistente = usuarioRepository.findByCorreo(usuarioDTO.getCorreo());
            if (usuarioExistente != null) {
                throw new IllegalArgumentException("Ya existe un usuario registrado con este correo: " + usuarioDTO.getCorreo());
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setCorreo(usuarioDTO.getCorreo());
            usuario.setTelefono(usuarioDTO.getTelefono());
            usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));

            // Buscar el rol en la base de datos por nombre
            Rol rol = rolRepository.findByRol(rolNombre);
            if (rol == null) {
                throw new IllegalArgumentException("Rol no encontrado en la base de datos: " + rolNombre);
            }

            // Establecer el rol encontrado en el usuario
            usuario.setRol(rol);

            // Guardar el usuario en la base de datos
            usuarioRepository.save(usuario);
        } catch (DataAccessException e) {
            throw new Exception("Error al registrar el usuario: " + e.getMessage());
        }
    }


    public Usuario autenticarUsuario(String correo, String contrasena) throws Exception {
        try {
            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                return usuario;
            } else {
                throw new IllegalArgumentException("Credenciales incorrectas");
            }
        } catch (DataAccessException e) {
            throw new Exception("Error al autenticar el usuario: " + e.getMessage());
        }
    }

    public UsuarioDto obtenerUsuarioPorId(int idUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            UsuarioDto usuarioDto = new UsuarioDto();
            usuarioDto.setId(usuario.getId_usuario());
            usuarioDto.setNombre(usuario.getNombre());
            usuarioDto.setCorreo(usuario.getCorreo());
            usuarioDto.setTelefono(usuario.getTelefono());
            // No incluir la contraseña ni el rol por razones de seguridad
            return usuarioDto;
        } else {
            throw new IllegalArgumentException("No se encontró ningún usuario con el ID proporcionado");
        }
    }

    public void actualizarPerfilUsuario(UsuarioDto usuarioDto) throws Exception {
        try {
            // Obtener el usuario existente de la base de datos
            Usuario usuario = usuarioRepository.findById(usuarioDto.getId()).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Actualizar los campos del usuario con los datos del DTO
            usuario.setNombre(usuarioDto.getNombre());
            usuario.setTelefono(usuarioDto.getTelefono());

            // Si se proporcionó una nueva contraseña, encriptarla y actualizarla
            if (usuarioDto.getContrasena() != null && !usuarioDto.getContrasena().isEmpty()) {
                usuario.setContrasena(passwordEncoder.encode(usuarioDto.getContrasena()));
            }

            // Guardar los cambios en la base de datos
            usuarioRepository.save(usuario);
        } catch (DataAccessException e) {
            throw new Exception("Error al actualizar el perfil del usuario: " + e.getMessage());
        }
    }

    public void eliminarUsuario(int id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> obtenerEntrenadores(int rolId) {
        return usuarioRepository.findByRolId(rolId);
    }

    public String obtenerContrasenaPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario != null) {
            return usuario.getContrasena();
        } else {
            throw new IllegalArgumentException("No se encontró ningún usuario con el correo electrónico proporcionado.");
        }
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public List<Usuario> obtenerEntrenadores() {
        // Obtener el ID del rol de entrenador (debes establecer esto según tu base de datos)
        int rolEntrenadorId = 2; // Por ejemplo, si el ID del rol de entrenador es 2

        // Utilizar el método findByRolId para encontrar usuarios por el ID de su rol
        return usuarioRepository.findByRolId(rolEntrenadorId);
    }

    //Generar una nueva contraseña temporal
    private String generarNuevaContrasenaTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return sb.toString();
    }

    /* metodo para contraseña tempoaral*/
    public void generarYEnviarContrasenaTemporal(String correo) throws Exception {
        try {
            String nuevaContrasenaTemporal = generarNuevaContrasenaTemporal();

            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario != null) {
                usuario.setContrasena(passwordEncoder.encode(nuevaContrasenaTemporal));
                usuarioRepository.save(usuario);
            } else {
                throw new IllegalArgumentException("No se encontró ningún usuario con el correo electrónico proporcionado.");
            }

            emailService.sendHtmlEmail(correo, "Recuperación de Contraseña - DogWalker", nuevaContrasenaTemporal);
        } catch (DataAccessException e) {
            throw new Exception("Error al generar y enviar la contraseña temporal: " + e.getMessage());
        }
    }



}

