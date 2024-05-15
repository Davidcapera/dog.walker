package com.DogWalker.dog.walker.controlador;
import com.DogWalker.dog.walker.modelo.dtos.MascotaDto;
import com.DogWalker.dog.walker.modelo.dtos.ReservaDto;
import com.DogWalker.dog.walker.modelo.dtos.ServicioDto;
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
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String index( ) {
        return "index";
    }

    @GetMapping("/registro")
    public String viewRegistro() {
        return "registroUsuario";
    }
    @PostMapping("/registro")
    public String registroUsuario(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model) {
        try {
            // Verificar si el correo ya existe en la base de datos
            Usuario usuarioExistente = usuarioService.buscarUsuarioPorCorreo(usuario.getCorreo());
            if (usuarioExistente != null) {
                model.addAttribute("error", " ¡Error! El correo electrónico ingresado ya está registrado. Por favor, utiliza otro correo.");
               return  "redirect:/registro?error";
            } else {
                usuarioService.registrarUsuario(usuario, rol);
                model.addAttribute("exitoRegistro", true);
                return "redirect:/login?registroExitoso";
            }
        } catch (Exception e) {
            model.addAttribute("error"," ¡Error! En el registro .");
            return "redirect:/registro?error";
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
            return "redirect:/login?registroExitoso"; // Permitir el registro si el usuario es admin
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }
    //vista administrador
    @GetMapping("/homeAdmin")
    public String viewPrincipalAdmin(Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesion y tiene el rol "admin"
        String rol = (String) session.getAttribute("rol");
        if (rol != null && rol.equals("admin")) {
            try {
                // Obtener la lista de entrenadores cuyo rol sea 2
                List<Usuario> entrenadores = usuarioService.obtenerEntrenadores(2);
                // Pasar la lista de entrenadores a la vista
                model.addAttribute("entrenadores", entrenadores);
                // Obtener la lista de servicios
                List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
                model.addAttribute("servicios", servicios);
                return "PrincipalAdmin"; // Permitir el acceso a la vista principal del admin
            } catch (Exception e) {
                // Redirigir al login si hay algun error
                return "redirect:/login";
            }
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String viewLogin(){return "login";}

    @PostMapping("/login")
    public String login(@ModelAttribute("usuario") UsuarioDto usuario, HttpSession session, Model model) {
        // Obtener correo y contraseña del usuario
        String correo = usuario.getCorreo();
        String contrasena = usuario.getContrasena();

        try {
            // Validar las credenciales
            Usuario usuarioAutenticado = usuarioService.autenticarUsuario(correo, contrasena);

            if (usuarioAutenticado != null) {
                // Las credenciales son validas, establecer sesion
                session.setAttribute("usuario", usuarioAutenticado);
                session.setAttribute("rol", usuarioAutenticado.getRol().getRol());
                // Redirigir segun el rol
                switch (usuarioAutenticado.getRol().getRol()) {
                    case "usuario":
                        return "redirect:/homeUsuario"; // Redirigir al home de usuario
                    case "entrenador":
                        return "redirect:/homeEntrenador"; // Redirigir al home de entrenador
                    case "admin":
                        return "redirect:/homeAdmin"; // Redirigir al home de administrador
                    default:
                        return "redirect:/login?error=unknown"; // Rol desconocido
                }
            } else {
                // Las credenciales son incorrectas
                model.addAttribute("error", "Credenciales incorrectas. Por favor, verifica tu correo y contraseña.");
                return "login"; // Devolver al usuario a la vista de login con el mensaje de error
            }
        } catch (Exception e) {
            // Manejar cualquier excepcion que pueda surgir durante la autenticacion
            model.addAttribute("error", "Credenciales incorrectas. Por favor, verifica tu correo y contraseña..");
            return "login"; // Devolver al usuario a la vista de login con el mensaje de error
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
    private MascotaService mascotaService;

    @GetMapping("/registrarMascota")
    public String viewRegistroMascota(Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesion y tiene el rol de "usuario"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            model.addAttribute("mascota", new MascotaDto());
            return "registroMascota"; // Permitir el acceso a la vista de registro de mascotas
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }

    @PostMapping("/registrarMascota")
    public String registrarMascota(@ModelAttribute("mascota") MascotaDto mascotaDto, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesion y tiene el rol de "usuario"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("usuario")) {
            int idUsuario = usuario.getId_usuario(); // Obtener el ID del usuario
            mascotaService.registrarMascota(mascotaDto, usuario);
            return "redirect:/homeUsuario?exito"; // Redirigir al home de usuario con un mensaje de éxito
        } else {
            // Si el usuario no ha iniciado sesion o no tiene el rol adecuado, redirigir al login
            throw new Exception("El usuario no ha iniciado sesión o no tiene permisos para registrar mascotas");
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


    @GetMapping("/perfilUsuario")
    public String mostrarPerfilUsuario(HttpSession session, Model model) {
        // Obtener el usuario de la sesion
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            // Pasar el usuario al modelo para que se muestren sus detalles en la vista
            model.addAttribute("usuario", usuario);
            return "perfilUsuario"; // Nombre de la vista Thymeleaf
        } else {
            // Si no hay usuario en sesion, redirigir a la página de inicio de sesion
            return "redirect:/login";
        }
    }

    @GetMapping("/editarPerfilUsuario")
    public String mostrarFormularioEditarPerfil(@RequestParam("id") int idUsuario, Model model) {
        // Obtener los datos del usuario a partir de su ID
        UsuarioDto usuarioDto = usuarioService.obtenerUsuarioPorId(idUsuario);
        // Agregar el usuario al modelo para que la vista pueda acceder a sus datos
        model.addAttribute("usuario", usuarioDto);
        return "actualizarUsuario"; // Nombre de la vista Thymeleaf para el formulario de edición
    }

    @PostMapping("/actualizarPerfilUsuario")
    public String actualizarPerfilUsuario(@ModelAttribute("usuario") UsuarioDto usuarioDto, HttpSession session) {
        try {
            // Llamar al método para actualizar el perfil del usuario
            usuarioService.actualizarPerfilUsuario(usuarioDto);
            // Obtener el rol del usuario de la sesión;
            return "redirect:/perfilUsuario";

        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante la actualización

            return "redirect:/perfilUsuario?error";
        }
    }

    @PostMapping("/eliminarPerfilUsuario")
    public String eliminarPerfilUsuario(@RequestParam("id") int idUsuario, HttpSession session)throws Exception {
        // Llamar al método para eliminar el perfil del usuario
        usuarioService.eliminarUsuario(idUsuario);

        // Obtener el rol del usuario de la sesión
        String rol = (String) session.getAttribute("rol");

        // Redirigir según el rol del usuario
        if (rol != null) {
            switch (rol) {
                case "usuario":
                    return "redirect:/registro"; // Redirigir al home de usuario
                case "admin":
                    return "redirect:/registroAdmin"; // Redirigir al home de administrador
                default:
                    return "redirect:/perfilUsuario?error"; // Rol desconocido
            }
        } else {
            return "redirect:/perfilUsuario?error"; // Redirigir al perfil de usuario si hay algún error
        }
    }


    @GetMapping("/homeEntrenador")
    public String viewPrincipalEntrenador(Model model, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesión y tiene el rol "entrenador"
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");
        if (usuario != null && rol != null && rol.equals("entrenador")) {
            // Obtener las reservas asociadas al entrenador
            List<Reserva> reservas = reservaService.obtenerReservasPorIdEntrenador(usuario.getId_usuario());
            // Pasar las reservas a la vista
            model.addAttribute("reservas", reservas);
            return "PrincipalEntrenador";
        } else {
            // Si el usuario no ha iniciado sesión o no tiene el rol adecuado, redirigir al login
            return "redirect:/login";
        }
    }

    @GetMapping("/registroEntrenador")
    public String viewRegistroEntrenador(HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");
        if (usuario != null && rol != null && rol.equals("admin")) {
            return "registroEntrenador";
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @PostMapping("/registroEntrenador")
    public String registroEntrenador(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario1.getRol().getRol().equals("admin")) {
            try {
                usuarioService.registrarUsuario(usuario, rol);
                // Agregar un mensaje de éxito al modelo para que se muestre en la vista de PrincipalAdmin
                model.addAttribute("exito", true);
                return "redirect:/homeAdmin"; // Redirigir a la página principal del administrador
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir durante el registro
                model.addAttribute("error", "Error al registrar al entrenador: " + e.getMessage());
                return "redirect:/registroEntrenador"; // Redirigir de nuevo a la página de registro de entrenador con un mensaje de error
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
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

    @GetMapping("/editarEntrenador")
    public String mostrarFormularioEditarEntrenador(@RequestParam("id") int idEntrenador, Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            try {
                // Obtener los datos del entrenador a partir de su ID
                UsuarioDto entrenadorDto = usuarioService.obtenerUsuarioPorId(idEntrenador);
                // Agregar el entrenador al modelo para que la vista pueda acceder a sus datos
                model.addAttribute("entrenador", entrenadorDto);

                // Agregar también el usuario al modelo
                model.addAttribute("usuario", entrenadorDto); // Suponiendo que el objeto UsuarioDto tiene un método getId() que se pueda usar en Thymeleaf

                return "actualizarEntrenador"; // Nombre de la vista Thymeleaf para el formulario de edición
            } catch (Exception e) {
                // Manejar cualquier excepción que pueda ocurrir al obtener los datos del entrenador
                e.printStackTrace();
                // Redirigir de nuevo a la vista principal del administrador con un mensaje de error
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @PostMapping("/actualizarEntrenador")
    public String actualizarEntrenador(@ModelAttribute("entrenador") UsuarioDto entrenadorDto, HttpSession session) {
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
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/registrarServicio")
    public String viewRegistroServicio(Model model, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            model.addAttribute("servicio", new ServicioDto());
            return "registroServicio";
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @PostMapping("/registrarServicio")
    public String registrarServicio(@ModelAttribute("servicio") ServicioDto servicioDto, HttpSession session) {
        // Verificar si el usuario ha iniciado sesión y tiene rol de administrador
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            servicioService.registrarServicio(servicioDto);
            return "redirect:/homeAdmin?exito"; // Redirigir al home de administrador con un mensaje de éxito
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
        if (usuario != null && usuario.getRol().getRol().equals("admin")) {
            try {
                ServicioDto servicioDto = servicioService.obtenerServicioDtoPorId(servicioId);
                model.addAttribute("servicio", servicioDto);
                return "actualizarServicio";
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
        if (usuarioSesion != null && usuarioSesion.getRol().equals("admin")) {
            try {
                servicioService.actualizarServicio(servicioDto);
                return "redirect:/homeAdmin";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/homeAdmin?error";
            }
        } else {
            return "redirect:/login"; // Redirigir al login si no cumple los requisitos
        }
    }

    @Autowired
    private EmailService emailService;

    @PostMapping("/recuperarcontrasena")
    public String recuperarContrasena(@RequestParam("correo") String correo, Model model) {
        try {
            String contrasena = usuarioService.obtenerContrasenaPorCorreo(correo);

            // Enviar correo electrónico con la contraseña
            emailService.sendHtmlEmail(correo, "Recuperación de Contraseña - DogWalker", contrasena);

            model.addAttribute("exitoRecuperacion", true);
        } catch (Exception e) {
            model.addAttribute("errorRecuperacion", true);
        }
        return "redirect:/login?recuperacionExitosa";
    }

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/realizarReserva")
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

}

