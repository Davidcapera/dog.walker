package com.DogWalker.dog.walker.servicio;
import com.DogWalker.dog.walker.modelo.dtos.ReservaDto;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Reserva;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.repositorio.MascotaRepository;
import com.DogWalker.dog.walker.repositorio.ReservaRepository;
import com.DogWalker.dog.walker.repositorio.ServicioRepository;
import com.DogWalker.dog.walker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ReservaService {
@Autowired
private ReservaRepository reservaRepository;
@Autowired
private MascotaRepository mascotaRepository;
@Autowired
private UsuarioService usuarioService;
@Autowired
private ServicioRepository servicioRepository;
@Autowired
private UsuarioRepository usuarioRepository;

    public void crearReservaUsuario(ReservaDto reservaDto) {
        try {
            // Crear una nueva reserva
            Reserva reserva = new Reserva();

            // Obtener el usuario asociado a la reserva por su ID
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(reservaDto.getIdUsuario());
            Usuario usuario = usuarioOptional.orElseThrow(() -> new Exception("Usuario no encontrado"));
            reserva.setUsuario(usuario); // Asignar el usuario a la reserva

            // Obtener la mascota seleccionada por su ID
            Optional<Mascota> mascotaOptional = mascotaRepository.findById(reservaDto.getIdMascota());
            Mascota mascota = mascotaOptional.orElseThrow(() -> new Exception("Mascota no encontrada"));
            reserva.setMascota(mascota);

            // Obtener el servicio seleccionado por su ID
            Optional<Servicio> servicioOptional = servicioRepository.findById(reservaDto.getIdServicio());
            Servicio servicio = servicioOptional.orElseThrow(() -> new Exception("Servicio no encontrado"));
            reserva.setServicio(servicio);

            // Obtener la fecha de la reserva del DTO
            LocalDate fechaReserva = reservaDto.getFecha(); // Por ejemplo, podrías establecer la hora al inicio del día
            reserva.setFecha(fechaReserva);

            // Obtener la lista de entrenadores
            List<Usuario> entrenadores = usuarioService.obtenerEntrenadores();

            // Asignar un entrenador aleatorio a la reserva
            if (!entrenadores.isEmpty()) {
                int indiceAleatorio = new Random().nextInt(entrenadores.size());
                Usuario entrenadorAsignado = entrenadores.get(indiceAleatorio);
                reserva.setEntrenador(entrenadorAsignado);
            } else {
                throw new RuntimeException("No hay entrenadores disponibles");
            }

            // Guardar la reserva en la base de datos
            reservaRepository.save(reserva);
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir al crear la reserva
            e.printStackTrace();
            // Lanzar una excepción personalizada o manejar el error de alguna otra manera
        }
    }

    // Método para asignar un entrenador aleatorio a una reserva
    public void asignarEntrenadorAleatorio(Reserva reserva) {
        try {
        // Obtener una lista de todos los entrenadores
        List<Usuario> entrenadores = usuarioService.obtenerEntrenadores();

        // Verificar si hay al menos un entrenador disponible
        if (!entrenadores.isEmpty()) {
            // Seleccionar un entrenador aleatorio de la lista
            Random random = new Random();
            Usuario entrenadorAsignado = entrenadores.get(random.nextInt(entrenadores.size()));

            // Asignar el entrenador a la reserva
            reserva.setEntrenador(entrenadorAsignado);

            // Aquí puedes guardar la reserva con el entrenador asignado en la base de datos
        } else {
            // No hay entrenadores disponibles, manejar esta situación según sea necesario
        }
        } catch (Exception e) {
        // Manejar cualquier excepción que pueda ocurrir durante la asignación del entrenador
        e.printStackTrace();
        // Lanzar una excepción personalizada o manejar el error de alguna otra manera
         }

    }

    public List<Reserva> obtenerReservasPorIdUsuario(int usuarioId) {
        return reservaRepository.findByUsuario_IdUsuario(usuarioId);
    }

    public void eliminarReserva(int reservaId) throws Exception {
        try {
            reservaRepository.deleteById(reservaId);
        } catch (DataAccessException e) {
            throw new Exception("Error al eliminar la reserva: " + e.getMessage());
        }
    }

    public void actualizarReserva(ReservaDto reservaDto) throws Exception {
        try {
            // Obtener la reserva a actualizar por su ID
            Optional<Reserva> optionalReserva = reservaRepository.findById(reservaDto.getId_reserva());
            if (optionalReserva.isPresent()) {
                Reserva reserva = optionalReserva.get();
                // Actualizar la fecha y el tipo de servicio
                reserva.setFecha(reservaDto.getFecha());
                Servicio servicio = servicioRepository.findById(reservaDto.getIdServicio()).orElse(null);
                reserva.setServicio(servicio);
                // Guardar los cambios en la base de datos
                reservaRepository.save(reserva);
            } else {
                throw new Exception("No se encontró la reserva con ID: " + reservaDto.getId_reserva());
            }
        } catch (DataAccessException e) {
            throw new Exception("Error al actualizar la reserva: " + e.getMessage());
        }
    }

    public Reserva obtenerReservaPorId(int reservaId) {
        return reservaRepository.findByIdReserva(reservaId);
    }

    public List<Reserva> obtenerReservasPorIdEntrenador(int idEntrenador) {
        return reservaRepository.findByEntrenador_IdEntrenador(idEntrenador);
    }


}