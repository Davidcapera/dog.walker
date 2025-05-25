package com.DogWalker.dog.walker.repositorio;

import com.DogWalker.dog.walker.modelo.entidades.MensajeChat;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Integer> {

    @Query("""
      SELECT m
      FROM MensajeChat m
      WHERE (m.emisor.id = :u1 AND m.receptor.id = :u2)
         OR (m.emisor.id = :u2 AND m.receptor.id = :u1)
      ORDER BY m.fechaHora ASC
    """)
    List<MensajeChat> findConversation(
            @Param("u1") int usuario1,
            @Param("u2") int usuario2
    );
}
