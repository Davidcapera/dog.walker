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
}