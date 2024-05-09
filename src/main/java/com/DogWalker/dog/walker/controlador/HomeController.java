package com.DogWalker.dog.walker.controlador;
import com.DogWalker.dog.walker.modelo.dtos.MascotaDto;
import com.DogWalker.dog.walker.modelo.dtos.ServicioDto;
import com.DogWalker.dog.walker.modelo.dtos.UsuarioDto;
import com.DogWalker.dog.walker.modelo.entidades.Mascota;
import com.DogWalker.dog.walker.modelo.entidades.Servicio;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.servicio.MascotaService;
import com.DogWalker.dog.walker.servicio.ServicioService;
import com.DogWalker.dog.walker.servicio.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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
            usuarioService.registrarUsuario(usuario, rol);
            model.addAttribute("exitoRegistro", true);
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante el registro
            model.addAttribute("errorRegistro", true);
            e.printStackTrace(); // Opcional: imprimir el stack trace para debug
        }
        return "redirect:/login?registroExitoso";
    }

    @GetMapping("/homeUsuario")
    public String viewPrincipalUsuario(Model model, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            // Obtener la lista de mascotas asociadas al usuario
            List<Mascota> mascotas = mascotaService.obtenerMascotasPorUsuario(usuario);
            // Pasar la lista de mascotas a la vista
            model.addAttribute("mascotas", mascotas);
            return "PrincipalUsuario";
        } else {
            throw new Exception("El usuario no ha iniciado sesión");
        }
    }

    @GetMapping("/registroAdmin")
    public String viewRegistroAdmin() {
        return "registroAdmin";
    }


    @PostMapping("/registroAdmin")
    public String registroAdmin(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol) throws Exception {
        usuarioService.registrarUsuario(usuario, rol);
        return "redirect:/login?registroExitoso";
    }
    //vista administrador
    @GetMapping("/homeAdmin")
    public String viewPrincipalAdmin(Model model, HttpSession session) throws Exception {
        // Verificar si el usuario ha iniciado sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            // Obtener la lista de entrenadores cuyo rol sea 2
            List<Usuario> entrenadores = usuarioService.obtenerEntrenadores(2);
            // Pasar la lista de entrenadores a la vista
            model.addAttribute("entrenadores", entrenadores);
            // Obtener la lista de servicios
            List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
            model.addAttribute("servicios", servicios);
            return "PrincipalAdmin";
        } else {
            throw new Exception("El usuario no ha iniciado sesión");
        }
    }

    @GetMapping("/login")
    public String viewLogin(){return "login";}

    @PostMapping("/login")
    public String login(@ModelAttribute("usuario") UsuarioDto usuario, HttpSession session, Model model) throws Exception {
        // Obtener correo y contraseña del usuario
        String correo = usuario.getCorreo();
        String contrasena = usuario.getContrasena();

        // Validar las credenciales
        Usuario usuarioAutenticado = usuarioService.autenticarUsuario(correo, contrasena);

        if (usuarioAutenticado != null) {
            // Las credenciales son válidas, establecer sesión
            session.setAttribute("usuario", usuarioAutenticado);
            session.setAttribute("rol", usuarioAutenticado.getRol().getRol());
            // Redirigir según el rol
            switch (usuarioAutenticado.getRol().getRol()) {
                case "usuario":
                    return "redirect:/homeUsuario"; // Redirigir al home de usuario
                case "entrenador":
                    return "redirect:/homeEntrenador"; // Redirigir al home de entrenador
                case "admin":
                    return "redirect:/homeAdmin"; // Redirigir al home de administrador
                default:
                    return "redirect:/login?error"; // Rol desconocido
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";

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
    public String viewRegistroMascota(Model model) {
        model.addAttribute("mascota", new MascotaDto());
        return "registroMascota";
    }

    @PostMapping("/registrarMascota")
    public String registrarMascota(@ModelAttribute("mascota") MascotaDto mascotaDto, HttpSession session) throws Exception {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            int idUsuario = usuario.getId_usuario(); // Obtener el ID del usuario
            mascotaService.registrarMascota(mascotaDto, usuario);
            return "redirect:/homeUsuario?exito"; // Redirigir al home de usuario con un mensaje de éxito
        } else {
            throw new Exception("El usuario no ha iniciado sesión");
        }
    }

    @PostMapping("/eliminarMascota")
    public String eliminarMascota(@RequestParam("mascotaId") int mascotaId) throws Exception {
        mascotaService.eliminarMascota(mascotaId);
        return "redirect:/homeUsuario";
    }

    @GetMapping("/editarMascota")
    public String mostrarFormularioActualizacionMascota(@RequestParam("mascotaId") int mascotaId, Model model) {
        // Obtener la mascota por su ID
        Mascota mascota = mascotaService.obtenerMascotaPorId(mascotaId);
        // Pasar la mascota al modelo
        model.addAttribute("mascota", mascota);
        return "editarMascota";
    }

    @PostMapping("/editarMascota")
    public String actualizarMascota(@ModelAttribute("mascota") MascotaDto mascotaDto) throws Exception {
        mascotaService.actualizarMascota(mascotaDto);
        return "redirect:/homeUsuario";
    }


    @GetMapping("/perfilUsuario")
    public String mostrarPerfilUsuario(HttpSession session, Model model) {
        // Obtener el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            // Pasar el usuario al modelo para que se muestren sus detalles en la vista
            model.addAttribute("usuario", usuario);
            return "perfilUsuario"; // Nombre de la vista Thymeleaf
        } else {
            // Si no hay usuario en sesión, redirigir a la página de inicio de sesión
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
    public String viewPrincipalEntrenador() {
        return "PrincipalEntrenador";
    }

    @GetMapping("/registroEntrenador")
    public String viewRegistroEntrenador() {
        return "registroEntrenador";
    }


    @PostMapping("/registroEntrenador")
    public String registroEntrenador(@ModelAttribute("usuario") UsuarioDto usuario, @RequestParam("rol") String rol, Model model) {
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
    }

    @PostMapping("/eliminarEntrenador")
    public String eliminarEntrenador(@RequestParam("id") int idEntrenador) {
        try {
            // Llamar al método para eliminar el entrenador
            usuarioService.eliminarUsuario(idEntrenador);
            // Redirigir a la vista principal del administrador
            return "redirect:/homeAdmin";
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante la eliminación
            e.printStackTrace();
            // Redirigir de nuevo a la vista principal del administrador con un mensaje de error
            return "redirect:/homeAdmin?error";
        }
    }

    @GetMapping("/editarEntrenador")
    public String mostrarFormularioEditarEntrenador(@RequestParam("id") int idEntrenador, Model model) {
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
    }

    @PostMapping("/actualizarEntrenador")
    public String actualizarEntrenador(@ModelAttribute("entrenador") UsuarioDto entrenadorDto) {
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
    }

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/registrarServicio")
    public String viewRegistroServicio(Model model) {
        model.addAttribute("servicio", new ServicioDto());
        return "registroServicio";
    }

    @PostMapping("/registrarServicio")
    public String registrarServicio(@ModelAttribute("servicio") ServicioDto servicioDto) {
        servicioService.registrarServicio(servicioDto);
        return "redirect:/homeAdmin?exito"; // Redirigir al home de administrador con un mensaje de éxito
    }

    @PostMapping("/eliminarServicio")
    public String eliminarServicio(@RequestParam("servicioId") int servicioId) throws Exception {
        servicioService.eliminarServicio(servicioId);
        return "redirect:/homeAdmin";
    }

    @GetMapping("/editarServicio")
    public String mostrarFormularioActualizacion(@RequestParam("servicioId") int servicioId, Model model) {
        // Obtener el servicio por su ID
        ServicioDto servicioDto = servicioService.obtenerServicioDtoPorId(servicioId);
        // Pasar el servicio al modelo
        model.addAttribute("servicio", servicioDto);
        return "actualizarServicio";
    }

    @PostMapping("/editarServicio")
    public String actualizarServicio(@ModelAttribute("servicio") ServicioDto servicioDto) throws Exception {
        servicioService.actualizarServicio(servicioDto);
        return "redirect:/homeAdmin";
    }

}

