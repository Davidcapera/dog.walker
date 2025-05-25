package com.DogWalker.dog.walker.controlador;

import com.DogWalker.dog.walker.modelo.dtos.RolUsuarioDto;
import com.DogWalker.dog.walker.repositorio.MascotaRepository;
import com.DogWalker.dog.walker.repositorio.ServicioRepository;
import com.DogWalker.dog.walker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @GetMapping("/dashboard")
    public String obtenerDashboard(Model model) {
        // Obtener la cantidad de usuarios por rol
        List<RolUsuarioDto> cantidadUsuariosPorRol = usuarioRepository.countUsuariosPorRol();


        long cantidadMascotas = mascotaRepository.count();
        long cantidadServicios = servicioRepository.count();


        model.addAttribute("cantidadUsuarios", cantidadUsuariosPorRol);
        model.addAttribute("cantidadMascotas", cantidadMascotas);
        model.addAttribute("cantidadServicios", cantidadServicios);

        return "dashboardAdmin"; // Vista para mostrar el gráfico
    }
}

