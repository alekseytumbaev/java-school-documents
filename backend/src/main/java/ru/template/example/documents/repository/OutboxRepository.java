package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.template.example.documents.repository.entity.Outbox;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
}
