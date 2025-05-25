package com.DogWalker.dog.walker.controlador;
import com.DogWalker.dog.walker.modelo.dtos.ReservaDto;
import com.DogWalker.dog.walker.modelo.entidades.Reserva;
import com.DogWalker.dog.walker.servicio.ReservaService;
import com.DogWalker.dog.walker.servicio.MascotaService;
import com.DogWalker.dog.walker.servicio.ServicioService;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/realizarReserva")
    public String viewRealizarReserva(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            List<Mascota> mascotas = mascotaService.obtenerMascotasPorUsuario(usuario);
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
            ReservaDto reservaDto = new ReservaDto();
            model.addAttribute("reservaDto", reservaDto);
            model.addAttribute("mascotas", mascotas);
            model.addAttribute("servicios", servicios);
            return "registroReserva";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/realizarReserva")
    public String realizarReserva(@ModelAttribute("reservaDto") ReservaDto reservaDto, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            try {
                reservaDto.setIdUsuario(usuario.getId_usuario());
                reservaService.crearReservaUsuario(reservaDto);
                return "redirect:/homeUsuario";
            } catch (Exception e) {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/eliminarReserva")
    public String eliminarReserva(@RequestParam("reservaId") int reservaId, HttpSession session) throws Exception {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            reservaService.eliminarReserva(reservaId);
            return "redirect:/homeUsuario";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editarReserva")
    public String mostrarFormularioActualizacionReserva(@RequestParam("reservaId") int reservaId, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            Reserva reserva = reservaService.obtenerReservaPorId(reservaId);
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
            model.addAttribute("reserva", reserva);
            model.addAttribute("servicios", servicios);
            return "actualizarReserva";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/editarReserva")
    public String actualizarReserva(@ModelAttribute("reserva") ReservaDto reservaDto, @RequestParam("idReserva") int idReserva, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            try {
                reservaDto.setId_reserva(idReserva);
                reservaService.actualizarReserva(reservaDto);
                return "redirect:/homeUsuario";
            } catch (Exception e) {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
    }
}