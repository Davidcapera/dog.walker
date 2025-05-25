package com.DogWalker.dog.walker.controlador;
import com.DogWalker.dog.walker.modelo.dtos.MensajeChatDto;
import com.DogWalker.dog.walker.modelo.dtos.UsuarioDto;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.servicio.ChatService;
import com.DogWalker.dog.walker.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UsuarioService usuarioService;

    // Mostrar conversación con otro usuario
    @GetMapping("/{receptorId}")
    public String chat(
            @PathVariable int receptorId,
            Model model,
            HttpSession session
    ) {
        Usuario actual = (Usuario) session.getAttribute("usuario");
        UsuarioDto destino = usuarioService.obtenerUsuarioPorId(receptorId);

        List<MensajeChatDto> mensajes =
                chatService.getConversation(actual.getId_usuario(), receptorId);

        model.addAttribute("mensajes", mensajes);
        model.addAttribute("usuarioDestino", destino);//pasamos el dto a la vista
        model.addAttribute("mensajeChatDto", new MensajeChatDto());
        return "chat";  // src/main/resources/templates/chat.html
    }
    // Enviar mensaje
    @PostMapping("/enviar")
    public String enviar(
            @ModelAttribute("mensajeChatDto") MensajeChatDto dto,
            HttpSession session
    ) {
        Usuario actual = (Usuario) session.getAttribute("usuario");
        chatService.sendMessage(
                actual.getId_usuario(),
                dto.getReceptorId(),
                dto.getContenido()
        );
        return "redirect:/chat/" + dto.getReceptorId();
    }
    //trae los prestadores servicios
    @GetMapping("/chat")
    public String chatView(HttpSession session, Model model) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");

        if (usuarioActual == null) {
            return "redirect:/login";  // Redirigir al login si no hay usuario en sesión
        }
        List<Usuario> usuarios = usuarioService.obtenerEntrenadores();  // Obtener entrenadores u otros usuarios
        model.addAttribute("usuarios", usuarios);
        return "chat";  // La vista del chat
    }

}
