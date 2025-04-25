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
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            model.addAttribute("servicio", new ServicioDto());
            return "registroServicio";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/registrarServicio")
    public String registrarServicio(@ModelAttribute("servicio") ServicioDto servicioDto, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            servicioService.registrarServicio(servicioDto);
            return "redirect:/homeAdmin?exito";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/eliminarServicio")
    public String eliminarServicio(@RequestParam("servicioId") int servicioId, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            try {
                servicioService.eliminarServicio(servicioId);
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editarServicio")
    public String mostrarFormularioActualizacion(@RequestParam("servicioId") int servicioId, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            try {
                ServicioDto servicioDto = servicioService.obtenerServicioDtoPorId(servicioId);
                model.addAttribute("servicio", servicioDto);
                return "actualizarServicio";
            } catch (Exception e) {
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/editarServicio")
    public String actualizarServicio(@ModelAttribute("servicio") ServicioDto servicioDto, HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion != null && usuarioSesion.getRol().equals("admin")) {
            try {
                servicioService.actualizarServicio(servicioDto);
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login";
        }
    }
}