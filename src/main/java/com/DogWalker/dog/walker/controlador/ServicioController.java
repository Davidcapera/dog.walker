package com.DogWalker.dog.walker.controlador;

import com.DogWalker.dog.walker.modelo.dtos.ServicioDto;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.servicio.ServicioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/registrarServicio")
    public String viewRegistroServicio(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("prestador")) {  // Cambiado de admin a prestador
            model.addAttribute("servicio", new ServicioDto());
            return "registroServicio";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/registrarServicio")
    public String registrarServicio(@ModelAttribute("servicio") ServicioDto servicioDto, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("prestador")) {
            servicioService.registrarServicio(servicioDto,usuario);
            return "redirect:/homePrestador?exito"; // Redirigir al home de administrador con un mensaje de éxito
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @PostMapping("/eliminarServicio")
    public String eliminarServicio(@RequestParam("servicioId") int servicioId, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null ) {
            try {
                servicioService.eliminarServicio(servicioId);
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @GetMapping("/editarServicio")
    public String mostrarFormularioActualizacion(@RequestParam("servicioId") int servicioId, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("prestador")) {
            try {
                ServicioDto servicioDto = servicioService.obtenerServicioDtoPorId(servicioId);
                model.addAttribute("servicio", servicioDto);
                return "editarServicio";
            } catch (Exception e) {

                return "redirect:/homePrestador?error";
            }
        } else if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            try {
                ServicioDto servicioDto = servicioService.obtenerServicioDtoPorId(servicioId);
                model.addAttribute("servicio", servicioDto);
                return "editarServicio";
            } catch (Exception e) {

                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @PostMapping("/editarServicio")
    public String actualizarServicio(@ModelAttribute("servicio") ServicioDto servicioDto, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion != null && usuarioSesion.getRol().equals("prestador")) {
            try {
                servicioService.actualizarServicio(servicioDto);
                return "redirect:/homePrestador";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/homePrestador?error";
            }
        /*} else if (usuarioSesion != null && usuarioSesion.getRol().equals("admin")) {
            try {
                servicioService.actualizarServicio(servicioDto);
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/homeAdmin?error";
            }*/
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }
}