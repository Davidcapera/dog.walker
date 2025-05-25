package com.DogWalker.dog.walker.servicio;
import com.DogWalker.dog.walker.modelo.dtos.ServicioDto;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.repositorio.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ServicioService {


    @Autowired
    private ServicioRepository servicioRepository;

    // Registrar un nuevo servicio
    public void registrarServicio(ServicioDto servicioDto, Usuario usuario) {
        Servicio servicio = new Servicio();
        servicio.setServicio(servicioDto.getServicio());
        servicio.setDescripcion(servicioDto.getDescripcion());
        servicio.setPrecio(servicioDto.getPrecio());  // Establecer el precio
        servicio.setUsuario(usuario);  // Asignar el usuario (prestador) al servicio

        // Guardar el servicio en la base de datos
        servicioRepository.save(servicio);
    }

    // Eliminar un servicio por ID
    public void eliminarServicio(int servicioId) throws Exception {
        Optional<Servicio> optionalServicio = servicioRepository.findById(servicioId);
        if (optionalServicio.isPresent()) {
            servicioRepository.deleteById(servicioId);
        } else {
            throw new Exception("El servicio con ID " + servicioId + " no existe");
        }
    }

    // Obtener todos los servicios registrados
    public List<Servicio> obtenerTodosLosServicios() {
        return servicioRepository.findAll();
    }

    // Obtener un servicio por su ID
    public ServicioDto obtenerServicioDtoPorId(int servicioId) {
        Optional<Servicio> optionalServicio = servicioRepository.findById(servicioId);
        if (optionalServicio.isPresent()) {
            Servicio servicio = optionalServicio.get();
            // Crear un DTO y asignar los valores del servicio
            ServicioDto servicioDto = new ServicioDto();
            servicioDto.setId_servicio(servicio.getId_servicio());
            servicioDto.setServicio(servicio.getServicio());
            servicioDto.setDescripcion(servicio.getDescripcion());
            servicioDto.setPrecio(servicio.getPrecio());
            return servicioDto;
        } else {
            // Manejo de error si el servicio no se encuentra
            throw new NoSuchElementException("El servicio con ID " + servicioId + " no existe");
        }
    }

    // Actualizar un servicio
    public void actualizarServicio(ServicioDto servicioDto) {
        try {
            Optional<Servicio> optionalServicio = servicioRepository.findById(servicioDto.getId_servicio());
            if (optionalServicio.isPresent()) {
                Servicio servicio = optionalServicio.get();
                // Actualizar los valores del servicio con los del DTO
                servicio.setServicio(servicioDto.getServicio());
                servicio.setDescripcion(servicioDto.getDescripcion());
                servicio.setPrecio(servicioDto.getPrecio());  // Actualizar el precio
                System.out.println("Precio recibido: " + servicioDto.getPrecio());
                System.out.println("Precio recibido: " + servicio.getPrecio());

                // Guardar los cambios en la base de datos
                servicioRepository.save(servicio);
            } else {
                throw new Exception("El servicio con ID " + servicioDto.getId_servicio() + " no existe");
            }
        } catch (Exception ex) {
            // Manejo de la excepción
            ex.printStackTrace();
            throw new RuntimeException("Ha ocurrido un error al actualizar el servicio: " + ex.getMessage());
        }
    }
}