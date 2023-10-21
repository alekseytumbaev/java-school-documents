package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.Status;
import ru.template.example.documents.exception.DocumentNotFoundException;
import ru.template.example.documents.exception.NullDocumentIdException;
import ru.template.example.documents.repository.DocumentRepository;
import ru.template.example.documents.repository.entity.Document;
import ru.template.example.documents.repository.entity.StatusCode;
import ru.template.example.documents.util.DocumentMapper;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepo;

    @Override
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        documentDto.setId(null);
        documentDto.setDate(new Date());
        Status status = Status.of(StatusCode.NEW.name(), StatusCode.NEW.getDisplayName());
        documentDto.setStatus(status);
        Document document = documentRepo.save(DocumentMapper.toEntity(documentDto));
        return DocumentMapper.toDto(document, documentDto.getStatus().getName());
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
    public DocumentDto update(DocumentDto documentDto) {
        if (documentDto.getId() == null) {
            throw new NullDocumentIdException("Can't update document by id");
        }
        if (!existsById(documentDto.getId())) {
            throw new DocumentNotFoundException(
                    String.format("Can't update document with id=%d, because it's not found", documentDto.getId())
            );
        }
        Document document = documentRepo.save(DocumentMapper.toEntity(documentDto));
        return DocumentMapper.toDto(document, documentDto.getStatus().getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> findAll() {
        List<Document> documents = documentRepo.findAll();
        return documents.stream().map(d -> DocumentMapper.toDto(d, d.getStatus().getDisplayName()))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public DocumentDto get(Long id) {
        Document document = documentRepo.findById(id).orElseThrow(() -> new DocumentNotFoundException(
                String.format("Document with id=%d not found", id)
        ));
        return DocumentMapper.toDto(document, document.getStatus().getDisplayName());
    }

    /**
     * Проверяет, существует ли документ с переданным id
     *
     * @param id документа
     * @return true, если документ существует, иначе - false
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return documentRepo.existsById(id);
    }
}
