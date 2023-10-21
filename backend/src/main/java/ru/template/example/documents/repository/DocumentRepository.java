package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.template.example.documents.repository.entity.Document;

import java.util.Collection;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    void deleteByIdIn(Collection<Long> ids);
}
