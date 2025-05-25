package com.DogWalker.dog.walker.servicio;

import com.DogWalker.dog.walker.modelo.dtos.MensajeChatDto;
import com.DogWalker.dog.walker.modelo.entidades.MensajeChat;
import com.DogWalker.dog.walker.modelo.entidades.Usuario;
import com.DogWalker.dog.walker.repositorio.MensajeChatRepository;
import com.DogWalker.dog.walker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private MensajeChatRepository mensajeChatRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<MensajeChatDto> getConversation(int id1, int id2) {
        return mensajeChatRepository.findConversation(id1, id2)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MensajeChatDto sendMessage(int emisorId, int receptorId, String contenido) {
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new IllegalArgumentException("Emisor no encontrado"));
        Usuario receptor = usuarioRepository.findById(receptorId)
                .orElseThrow(() -> new IllegalArgumentException("Receptor no encontrado"));

        MensajeChat mensaje = new MensajeChat(
                emisor,
                receptor,
                contenido,
                LocalDateTime.now()
        );
        MensajeChat guardado = mensajeChatRepository.save(mensaje);
        return toDto(guardado);
    }

    private MensajeChatDto toDto(MensajeChat m) {
        return new MensajeChatDto(
                m.getId(),
                m.getEmisor().getId_usuario(),
                m.getReceptor().getId_usuario(),
                m.getContenido(),
                m.getFechaHora()
        );
    }
}
