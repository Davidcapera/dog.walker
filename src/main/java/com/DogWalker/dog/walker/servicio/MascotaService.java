package com.DogWalker.dog.walker.servicio;
import com.DogWalker.dog.walker.modelo.dtos.MascotaDto;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.repositorio.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    public void registrarMascota(MascotaDto mascotaDto, Usuario usuario) throws Exception {
        try {
            Mascota mascota = new Mascota();
            mascota.setNombre(mascotaDto.getNombre());
            mascota.setRaza(mascotaDto.getRaza());
            mascota.setPeso(mascotaDto.getPeso());
            mascota.setDescripcion(mascotaDto.getDescripcion());

            // Crear un objeto Usuario con el ID proporcionado
            Usuario propietario = new Usuario();
            propietario.setId_usuario(usuario.getId_usuario());

            // Establecer el usuario propietario en la mascota
            mascota.setUsuario(propietario);

            // Guardar la mascota en la base de datos
            mascotaRepository.save(mascota);
        } catch (DataAccessException e) {
            throw new Exception("Error al registrar la mascota: " + e.getMessage());
        }
    }

    public List<Mascota> obtenerMascotasPorUsuario(Usuario usuario) {
        return mascotaRepository.findByUsuario(usuario);
    }

    public void eliminarMascota(int mascotaId) throws Exception {
        try {
            mascotaRepository.deleteById(mascotaId);
        } catch (DataAccessException e) {
            throw new Exception("Error al eliminar la mascota: " + e.getMessage());
        }
    }

    public void actualizarMascota(MascotaDto mascotaDto) throws Exception {
        try {
            // Obtener la mascota a actualizar por su ID
            Optional<Mascota> mascotaOptional = mascotaRepository.findById(mascotaDto.getId_mascota());
            if (mascotaOptional.isPresent()) {
                Mascota mascota = mascotaOptional.get();
                // Actualizar los campos de peso y descripción
                mascota.setPeso(mascotaDto.getPeso());
                mascota.setDescripcion(mascotaDto.getDescripcion());
                // Guardar los cambios en la base de datos
                mascotaRepository.save(mascota);
            } else {
                throw new Exception("No se encontró la mascota con ID: " + mascotaDto.getIdusuario());
            }
        } catch (DataAccessException e) {
            throw new Exception("Error al actualizar la mascota: " + e.getMessage());
        }
    }

    public Mascota obtenerMascotaPorId(int idMascota) {
        return mascotaRepository.findByid_mascota(idMascota);
    }
}
