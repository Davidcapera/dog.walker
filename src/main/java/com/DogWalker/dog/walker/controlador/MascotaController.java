package com.DogWalker.dog.walker.controlador;
import com.DogWalker.dog.walker.modelo.dtos.MascotaDto;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.servicio.MascotaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping("/registrarMascota")
    public String viewRegistroMascota(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            model.addAttribute("mascota", new MascotaDto());
            return "registroMascota";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/registrarMascota")
    public String registrarMascota(@ModelAttribute("mascota") MascotaDto mascotaDto, HttpSession session) throws Exception {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            mascotaService.registrarMascota(mascotaDto, usuario);
            return "redirect:/homeUsuario?exito";
        } else {
            throw new Exception("No tienes permisos para registrar mascotas");
        }
    }
    @PostMapping("/eliminarMascota")
    public String eliminarMascota(@RequestParam("mascotaId") int mascotaId, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesion y tiene el rol de "usuario"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            mascotaService.eliminarMascota(mascotaId);
            return "redirect:/homeUsuario";
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            throw new Exception("El usuario no ha iniciado sesión o no tiene permisos para eliminar mascotas");
        }
    }

    @GetMapping("/editarMascota")
    public String mostrarFormularioActualizacionMascota(@RequestParam("mascotaId") int mascotaId, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesion y tiene el rol de "usuario"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            // Obtener la mascota por su ID
            Mascota mascota = mascotaService.obtenerMascotaPorId(mascotaId);
            // Pasar la mascota al modelo
            model.addAttribute("mascota", mascota);
            return "editarMascota"; // Permitir el acceso al formulario de edicion de mascotas
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }

    @PostMapping("/editarMascota")
    public String actualizarMascota(@ModelAttribute("mascota") MascotaDto mascotaDto, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesion y tiene el rol de "usuario"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            mascotaService.actualizarMascota(mascotaDto);
            return "redirect:/homeUsuario"; // Redirigir al home de usuario
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            throw new Exception("El usuario no ha iniciado sesión o no tiene permisos para actualizar mascotas");
        }
    }


}