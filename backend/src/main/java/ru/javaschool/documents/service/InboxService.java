package ru.javaschool.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaschool.documents.repository.InboxRepository;
import ru.javaschool.documents.repository.entity.Inbox;
import ru.javaschool.documents.exception.InboxAlreadyExistsException;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboxService {

    private final InboxRepository inboxRepo;

    /**
     * Сохраняет сообщение в таблицу входящих, если сообщения с таким id еще нет.
     *
     * @param inbox сообщение, которое нужно сохранить
     * @return сохраненное сообщение
     * @throws InboxAlreadyExistsException если сообщение с таким id уже существует
     */
    @Transactional
    public Inbox addIfNotExistsById(Inbox inbox) {
        if (inboxRepo.existsById(inbox.getId())) {
            throw new InboxAlreadyExistsException("Inbox with id=" + inbox.getId() + " already exists");
        }
        return inboxRepo.save(inbox);
    }

    /**
     * Возвращает все непрочитанные сообщения.
     *
     * @return список непрочитанных сообщений
     */
    @Transactional(readOnly = true)
    public List<Inbox> getUnread() {
        List<Inbox> inboxes = inboxRepo.findAllByIsRead(false);
        inboxes.forEach(in -> in.setRead(true));
        return inboxRepo.saveAll(inboxes);
    }

    /**
     * Помечает сообщения с переданными id как прочитанные.
     *
     * @param ids - id сообщений, которые нужно пометить как прочитанные
     */
    @Transactional
    public void markAsRead(Collection<Long> ids) {
        List<Inbox> inboxes = inboxRepo.findAllByIdIn(ids);
        inboxes.forEach(in -> in.setRead(true));
        inboxRepo.saveAll(inboxes);
    }
}
