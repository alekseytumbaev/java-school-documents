package ru.javaschool.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javaschool.documents.repository.entity.Inbox;

import java.util.Collection;
import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, Long> {

    List<Inbox> findAllByIsRead(boolean isRead);

    List<Inbox> findAllByIdIn(Collection<Long> ids);
}
