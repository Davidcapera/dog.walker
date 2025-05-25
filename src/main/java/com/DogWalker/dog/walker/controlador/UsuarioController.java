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
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MascotaService mascotaService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ServicioService servicioService;

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

    @GetMapping("/tipoRegistro")
    public String viewTIPORegistro() {
        return "tipoRegistro";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
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
                    case "prestador":
                        return "redirect:/homePrestador";
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

    @GetMapping("/homeUsuario")
    public String viewPrincipalUsuario(Model model, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesion y tiene el rol "usuario"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");
        if (usuario != null && rol != null && rol.equals("usuario")) {
            // Obtener la lista de mascotas asociadas al usuario
            List<Mascota> mascotas = mascotaService.obtenerMascotasPorUsuario(usuario);
            // Pasar la lista de mascotas a la vista
            model.addAttribute("mascotas", mascotas);
            // Obtener las reservas asociadas al usuario
            List<Reserva> reservas = reservaService.obtenerReservasPorIdUsuario(usuario.getId_usuario());
            // Pasar las reservas a la vista
            model.addAttribute("reservas", reservas);
            return "PrincipalUsuario";
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
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
        return "editarUsuario";
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalida la sesion existente
        }
        return "redirect:/"; // Redirige a la pagina de inicio
    }

    @Autowired
    private EmailService emailService;

    @PostMapping("/recuperarcontrasena")
    public String recuperarContrasena(@RequestParam("correo") String correo, Model model) {
        try {
            usuarioService.generarYEnviarContrasenaTemporal(correo);
            model.addAttribute("exitoRecuperacion", true);
        } catch (Exception e) {
            model.addAttribute("errorRecuperacion", true);
        }
        return "redirect:/login?recuperacionExitosa";
    }

    /*@GetMapping("/realizarReserva")
    public String viewRealizarReserva(Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y obtener el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            // Obtener las mascotas del usuario y los servicios disponibles
            List<Mascota> mascotas = mascotaService.obtenerMascotasPorUsuario(usuario);
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();

            // Crear un nuevo DTO de reserva y agregar las mascotas y servicios al modelo
            ReservaDto reservaDto = new ReservaDto();
            model.addAttribute("reservaDto", reservaDto);
            model.addAttribute("mascotas", mascotas);
            model.addAttribute("servicios", servicios);

            return "registroReserva"; // Devolver la vista para realizar la reserva
        } else {
            // Si el usuario no ha iniciado sesión, redirigir al login
            return "redirect:/login";
        }
    }

    @PostMapping("/realizarReserva")
    public String realizarReserva(@ModelAttribute("reservaDto") ReservaDto reservaDto, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y obtener el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            try {
                // Establecer el ID del usuario en el DTO de reserva
                reservaDto.setIdUsuario(usuario.getId_usuario());

                // Crear la reserva con el servicio correspondiente
                reservaService.crearReservaUsuario(reservaDto);

                // Redirigir al home del usuario después de realizar la reserva
                return "redirect:/homeUsuario";
            } catch (Exception e) {
                return "redirect:/login";
            }
        } else {
            // Si el usuario no ha iniciado sesión, redirigir al login
            return "redirect:/login";
        }
    }

    @PostMapping("/eliminarReserva")
    public String eliminarReserva(@RequestParam("reservaId") int reservaId, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesión y tiene el rol adecuado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null ) {
            reservaService.eliminarReserva(reservaId);
            return "redirect:/homeUsuario"; // Redirigir al home del usuario después de eliminar la reserva
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editarReserva")
    public String mostrarFormularioActualizacionReserva(@RequestParam("reservaId") int reservaId, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene el rol adecuado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            // Obtener la reserva por su ID
            Reserva reserva = reservaService.obtenerReservaPorId(reservaId);

            // Obtener todos los servicios disponibles
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();

            // Pasar la reserva y la lista de servicios al modelo
            model.addAttribute("reserva", reserva);
            model.addAttribute("servicios", servicios);

            // Mostrar el formulario de actualización de reserva
            return "actualizarReserva";
        } else {
            // Si el usuario no ha iniciado sesión o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }

    @PostMapping("/editarReserva")
    public String actualizarReserva(@ModelAttribute("reserva") ReservaDto reservaDto, @RequestParam("idReserva") int idReserva, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene el rol adecuado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            try {
                // Actualizar la reserva
                reservaDto.setId_reserva(idReserva); // Establecer el ID de la reserva en el objeto DTO
                reservaService.actualizarReserva(reservaDto);
                return "redirect:/homeUsuario"; // Redirigir al home del usuario después de actualizar la reserva
            } catch (Exception e) {
                return "redirect:/login";
            }
        } else {
            // Si el usuario no ha iniciado sesión o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }
*/
}