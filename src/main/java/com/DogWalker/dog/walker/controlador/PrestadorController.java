package com.DogWalker.dog.walker.controlador;

import com.DogWalker.dog.walker.modelo.dtos.ReservaDto;
import com.DogWalker.dog.walker.modelo.dtos.UsuarioDto;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Reserva;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.servicio.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PrestadorController {
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ServicioService servicioService;


    @GetMapping("/homePrestador")
    public String viewPrincipalEntrenador(Model model, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesión y tiene el rol "entrenador"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");
        if (usuario != null && rol != null && rol.equals("prestador")) {
            // Obtener las reservas asociadas al entrenador
            List<Reserva> reservas = reservaService.obtenerReservasPorIdEntrenador(usuario.getId_usuario());
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
            // Pasar las reservas a la vista
            model.addAttribute("reservas", reservas);
            model.addAttribute("servicios", servicios);

            return "PrincipalPrestador";
        } else {
            // Si el usuario no ha iniciado sesión o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }
    @GetMapping("/registroPrestador")
    public String viewRegistro() {
        return "registroPrestador";
    }
    @PostMapping("/registroPrestador")
    public String registroEntrenador(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        if (usuario != null /*&& usuario1.getRol().getRol().equals("admin")*/) {
            try {
                usuarioService.registrarUsuario(usuario, rol);
                // Agregar un mensaje de éxito al modelo para que se muestre en la vista de PrincipalAdmin
                model.addAttribute("exito", true);
                return "redirect:/login"; // Redirigir a la página principal del administrador
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir durante el registro
                model.addAttribute("error", "Error al registrar al entrenador: " + e.getMessage());
                return "redirect:/registroPrestador"; // Redirigir de nuevo a la página de registro de entrenador con un mensaje de error
            }
        /*} else if (usuario != null ) {
            try {
                usuarioService.registrarUsuario(usuario, rol);
                // Agregar un mensaje de éxito al modelo para que se muestre en la vista de Principal
                model.addAttribute("exito", true);
                return "redirect:/login"; // Redirigir a la pagina de login para un usuario
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir durante el registro
                model.addAttribute("error", "Error al registrar al entrenador: " + e.getMessage());
                return "redirect:/registroPrestador"; // Redirigir de nuevo a la página de registro de entrenador con un mensaje de error
            }*/
        }else {
             return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }
    @GetMapping("/editarPrestador")
    public String mostrarFormularioEditarEntrenador(@RequestParam("id") int idEntrenador, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            try {
                // Obtener los datos del entrenador a partir de su ID
                UsuarioDto entrenadorDto = usuarioService.obtenerUsuarioPorId(idEntrenador);
                // Agregar el entrenador al modelo para que la vista pueda acceder a sus datos
                model.addAttribute("prestador", entrenadorDto);

                // Agregar también el usuario al modelo
                model.addAttribute("usuario", entrenadorDto); // Suponiendo que el objeto UsuarioDto tiene un método getId() que se pueda usar en Thymeleaf

                return "editarPrestador"; // Nombre de la vista Thymeleaf para el formulario de edición
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir al obtener los datos del entrenador
                e.printStackTrace();
                // Redirigir de nuevo a la vista principal del administrador con un mensaje de error
                return "redirect:/homeAdmin?error";
            }
        } else if (usuario != null && usuario.getRol().getRol().equals("prestador")) {
            try {
                // Obtener los datos del entrenador a partir de su ID
                UsuarioDto entrenadorDto = usuarioService.obtenerUsuarioPorId(idEntrenador);
                // Agregar el entrenador al modelo para que la vista pueda acceder a sus datos
                model.addAttribute("prestador", entrenadorDto);

                // Agregar también el usuario al modelo
                model.addAttribute("usuario", entrenadorDto); // Suponiendo que el objeto UsuarioDto tiene un método getId() que se pueda usar en Thymeleaf

                return "editarPrestador"; // Nombre de la vista Thymeleaf para el formulario de edición
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir al obtener los datos del entrenador
                e.printStackTrace();
                // Redirigir de nuevo a la vista principal del administrador con un mensaje de error
                return "redirect:/homePrestador?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }
    @PostMapping("/actualizarPrestador")
    public String actualizarEntrenador(@ModelAttribute("prestador") UsuarioDto entrenadorDto, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            try {
                // Llamar al método para actualizar los datos del entrenador
                usuarioService.actualizarPerfilUsuario(entrenadorDto);
                // Redirigir a la vista principal del administrador
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir durante la actualización
                e.printStackTrace();
                // Redirigir de nuevo a la vista principal del administrador con un mensaje de error
                return "redirect:/homeAdmin?error";
            }
        } else if (usuario != null && usuario.getRol().getRol().equals("prestador")) {
            try {
                // Llamar al método para actualizar los datos del entrenador
                usuarioService.actualizarPerfilUsuario(entrenadorDto);
                // Redirigir a la vista principal del prestador
                return "redirect:/homePrestador";
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir durante la actualización
                e.printStackTrace();
                // Redirigir de nuevo a la vista principal del prestador con un mensaje de error
                return "redirect:/homePrestador?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }
    @PostMapping("/eliminarPrestador")
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
}