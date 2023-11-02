package ru.javaschool.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javaschool.documents.repository.entity.Document;

import java.util.Collection;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    void deleteByIdIn(Collection<Long> ids);
}
