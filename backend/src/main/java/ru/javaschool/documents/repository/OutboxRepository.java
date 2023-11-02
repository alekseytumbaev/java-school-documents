package ru.javaschool.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javaschool.documents.repository.entity.Outbox;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
}
