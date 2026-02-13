package com.alissontfraga.unspokenwords.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alissontfraga.unspokenwords.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByOwner_Id(Long ownerId);

    Optional<Message> findByIdAndOwner_Username(Long id, String username);
}
