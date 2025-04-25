package com.DogWalker.dog.walker.controlador;

import com.DogWalker.dog.walker.modelo.dtos.UsuarioDto;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String viewRegistro() {
        return "registroUsuario";
    }

    @PostMapping("/registro")
    public String registroUsuario(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model) {
        try {
            Usuario usuarioExistente = usuarioService.buscarUsuarioPorCorreo(usuario.getCorreo());
            if (usuarioExistente != null) {
                model.addAttribute("error", "¡Error! El correo electrónico ingresado ya está registrado.");
                return "redirect:/registro?error";
            } else {
                usuarioService.registrarUsuario(usuario, rol);
                return "redirect:/login?registroExitoso";
            }
        } catch (Exception e) {
            model.addAttribute("error", "¡Error! En el registro.");
            return "redirect:/registro?error";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("usuario") UsuarioDto usuario, HttpSession session, Model model) {
        String correo = usuario.getCorreo();
        String contrasena = usuario.getContrasena();

        try {
            Usuario usuarioAutenticado = usuarioService.autenticarUsuario(correo, contrasena);

            if (usuarioAutenticado != null) {
                session.setAttribute("usuario", usuarioAutenticado);
                session.setAttribute("rol", usuarioAutenticado.getRol().getRol());
                switch (usuarioAutenticado.getRol().getRol()) {
                    case "usuario":
                        return "redirect:/homeUsuario";
                    case "entrenador":
                        return "redirect:/homeEntrenador";
                    case "admin":
                        return "redirect:/homeAdmin";
                    default:
                        return "redirect:/login?error=unknown";
                }
            } else {
                model.addAttribute("error", "Credenciales incorrectas.");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "login";
        }
    }

    @GetMapping("/perfilUsuario")
    public String mostrarPerfilUsuario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            return "perfilUsuario";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editarPerfilUsuario")
    public String mostrarFormularioEditarPerfil(@RequestParam("id") int idUsuario, Model model) {
        UsuarioDto usuarioDto = usuarioService.obtenerUsuarioPorId(idUsuario);
        model.addAttribute("usuario", usuarioDto);
        return "actualizarUsuario";
    }

    @PostMapping("/actualizarPerfilUsuario")
    public String actualizarPerfilUsuario(@ModelAttribute("usuario") UsuarioDto usuarioDto) {
        try {
            usuarioService.actualizarPerfilUsuario(usuarioDto);
            return "redirect:/perfilUsuario";
        } catch (Exception e) {
            return "redirect:/perfilUsuario?error";
        }
    }

    @PostMapping("/eliminarPerfilUsuario")
    public String eliminarPerfilUsuario(@RequestParam("id") int idUsuario, HttpSession session) throws Exception {
        usuarioService.eliminarUsuario(idUsuario);
        String rol = (String) session.getAttribute("rol");
        if ("usuario".equals(rol)) {
            return "redirect:/registro";
        } else {
            return "redirect:/homeAdmin";
        }
    }
}