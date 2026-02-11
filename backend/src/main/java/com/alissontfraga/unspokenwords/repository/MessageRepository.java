package com.alissontfraga.unspokenwords.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alissontfraga.unspokenwords.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByOwner_Id(Long ownerId);

}
