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
    @GetMapping("/registroAdmin")
    public String viewRegistroAdmin(HttpSession session) {
        // Verificar si el usuario ha iniciado sesion y tiene el rol "admin"
        String rol = (String) session.getAttribute("rol");
        if (rol != null && rol.equals("admin")) {
            return "registroAdmin"; // Permitir el acceso a la vista de registro para el admin
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }
    @PostMapping("/registroAdmin")
    public String registroAdmin(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesion y tiene el rol "admin"
        String rolSession = (String) session.getAttribute("rol");
        if (rolSession != null && rolSession.equals("admin")) {
            usuarioService.registrarUsuario(usuario, rol);
            return "redirect:/homeAdmin"; // Permitir el registro si el usuario es admin
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }
    @GetMapping("/homeAdmin")
    public String viewPrincipalAdmin(Model model, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if ("admin".equals(rol)) {
            List<Usuario> prestadores = usuarioService.obtenerEntrenadores(3);
            List<Usuario> usuarios = usuarioService.obtenerEntrenadores(2);
            List<Usuario> administradores = usuarioService.obtenerEntrenadores(1);
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
            model.addAttribute("prestador", prestadores);
            model.addAttribute("usuario", usuarios);
            model.addAttribute("administrador", administradores);
            model.addAttribute("servicios", servicios);
            return "PrincipalAdmin";
        } else {
            return "redirect:/login";
        }
    }
    /*@GetMapping("/registroPrestador")
    public String viewRegistroEntrenador(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if ("admin".equals(rol)) {
            return "registroPrestador";
        } else {
            return "redirect:/login";
        }
    }
*/
   /* @PostMapping("/registroPrestador")
    public String registroEntrenador(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model, HttpSession session) {
        String rolSession = (String) session.getAttribute("rol");
        if ("admin".equals(rolSession)) {
            try {
                usuarioService.registrarUsuario(usuario, rol);
                return "redirect:/homeAdmin?exito";
            } catch (Exception e) {
                model.addAttribute("error", "Error al registrar al entrenador");
                return "redirect:/registroPrestador";
            }
        } else {
            return "redirect:/login";
        }
    }
    @PostMapping("/eliminarEntrenador")
    public String eliminarEntrenador(@RequestParam("id") int idEntrenador, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null ) {
            try {
                // Llamar al método para eliminar el entrenador
                usuarioService.eliminarUsuario(idEntrenador);
                // Redirigir a la vista principal del administrador
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }
*/
}