package ru.javaschool.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.controller.dto.IdDto;
import ru.javaschool.documents.controller.dto.Status;
import ru.javaschool.documents.exception.DocumentNotFoundException;
import ru.javaschool.documents.exception.IllegalDocumentStatusException;
import ru.javaschool.documents.repository.DocumentRepository;
import ru.javaschool.documents.repository.entity.Document;
import ru.javaschool.documents.repository.entity.Inbox;
import ru.javaschool.documents.repository.entity.StatusCode;
import ru.javaschool.documents.util.DocumentMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepo;
    private final OutboxService outboxService;
    private final InboxService inboxService;
    private final DocumentMapper documentMapper;

    @Override
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        documentDto.setId(null);
        documentDto.setDate(new Date());
        Status status = Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName());
        documentDto.setStatus(status);
        Document document = documentRepo.save(documentMapper.map(documentDto, Document.class));
        return documentMapper.map(document, DocumentDto.class);
    }

    @Override
    @Transactional
    public void deleteAll(Set<Long> ids) {
        documentRepo.deleteByIdIn(ids);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        documentRepo.deleteById(id);
    }

    @Override
    @Transactional
    public DocumentDto sendForProcessing(IdDto idDto) {
        long id = idDto.getId();
        Document document = documentRepo.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(
                        String.format("Can't process document with id=%d, because it's not found", id)));
        if (!document.getStatus().equals(StatusCode.NEW)) {
            throw new IllegalDocumentStatusException(
                    String.format("Can't process document with id=%d, because it's already processed", id));
        }
        document.setStatus(StatusCode.IN_PROCESS);
        document = documentRepo.save(document);
        DocumentDto savedDocumentDto = documentMapper.map(document, DocumentDto.class);
        outboxService.addMessage(savedDocumentDto);
        return savedDocumentDto;
    }

    /**
     * Читает сообщения из таблицы исходящих, применяет к документам результаты обработки,
     * которые описаны в сообщениях. Помечает сообщения прочитанными.
     * <br/>
     * Если применить результат обработки не удается, пишет лог.
     *
     * @return документы, к которым были применены результаты обработки
     */
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public List<Document> readInboxAndApplyProcessingResult() {
        List<Inbox> unreadInboxes = inboxService.getUnread();

        Map<Long, String> documentIdToStatuses = unreadInboxes.stream()
                .collect(Collectors.toMap(
                        in -> in.getPayload().getDocumentId(),
                        in -> in.getPayload().getStatusCode()
                ));
        List<Document> documents = documentRepo.findAllById(documentIdToStatuses.keySet());

        for (Document document : documents) {
            String status = documentIdToStatuses.get(document.getId());
            if (statusCanBeApplied(status, document)) {
                document.setStatus(StatusCode.valueOf(status));
            }
        }
        documents = documentRepo.saveAll(documents);
        inboxService.markAsRead(unreadInboxes.stream().map(Inbox::getId).collect(Collectors.toSet()));
        return documents;
    }

    /**
     * Проверяет, можно ли применить результат обработки к документу.
     * <br/>
     * Если нет - пишет лог.
     *
     * @param status   результат обработки
     * @param document документ, к которому надо применить результат
     * @return true, если результат обработки можно применить к документу, false - в противном случае
     */
    private boolean statusCanBeApplied(String status, Document document) {
        if (!document.getStatus().equals(StatusCode.IN_PROCESS)) {
            log.warn(
                    "Processing results can't be applied to document with id={}, " +
                            "because document status is '{}', but should be '{}'",
                    document.getId(), document.getStatus(), StatusCode.IN_PROCESS
            );
            return false;

        }
        if (status == null ||
                !status.equals(StatusCode.ACCEPTED.name()) &&
                        !status.equals(StatusCode.REJECTED.name())) {
            log.warn(
                    "Processing results can't be applied to document with id={}, " +
                            "because results status is '{}', but should be one of {}",
                    document.getId(), status, List.of(StatusCode.ACCEPTED.name(), StatusCode.REJECTED.name())
            );
            return false;
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> findAll() {
        List<Document> documents = documentRepo.findAll();
        return documents.stream().map(d -> documentMapper.map(d, DocumentDto.class))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public DocumentDto get(Long id) {
        Document document = documentRepo.findById(id).orElseThrow(() -> new DocumentNotFoundException(
                String.format("Document with id=%d not found", id)
        ));
        return documentMapper.map(document, DocumentDto.class);
    }
}
