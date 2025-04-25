package com.DogWalker.dog.walker.controlador;

import com.DogWalker.dog.walker.modelo.dtos.UsuarioDto;
import com.DogWalker.dog.walker.servicio.UsuarioService;
import com.DogWalker.dog.walker.servicio.ServicioService;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/homeAdmin")
    public String viewPrincipalAdmin(Model model, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if ("admin".equals(rol)) {
            List<Usuario> entrenadores = usuarioService.obtenerEntrenadores(2);
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
            model.addAttribute("entrenadores", entrenadores);
            model.addAttribute("servicios", servicios);
            return "PrincipalAdmin";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/registroEntrenador")
    public String viewRegistroEntrenador(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if ("admin".equals(rol)) {
            return "registroEntrenador";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/registroEntrenador")
    public String registroEntrenador(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model, HttpSession session) {
        String rolSession = (String) session.getAttribute("rol");
        if ("admin".equals(rolSession)) {
            try {
                usuarioService.registrarUsuario(usuario, rol);
                return "redirect:/homeAdmin?exito";
            } catch (Exception e) {
                model.addAttribute("error", "Error al registrar al entrenador");
                return "redirect:/registroEntrenador";
            }
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/eliminarEntrenador")
    public String eliminarEntrenador(@RequestParam("id") int idEntrenador, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (rol != null && "admin".equals(rol)) {
            usuarioService.eliminarUsuario(idEntrenador);
            return "redirect:/homeAdmin";
        } else {
            return "redirect:/login";
        }
    }
}