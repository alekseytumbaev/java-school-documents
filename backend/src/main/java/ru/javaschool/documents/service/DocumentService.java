package ru.javaschool.documents.service;

import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.controller.dto.IdDto;
import ru.javaschool.documents.exception.DocumentNotFoundException;
import ru.javaschool.documents.exception.IllegalDocumentStatusException;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохранить документ
     *
     * @param documentDto документ
     * @return сохраненный документ
     */
    DocumentDto save(DocumentDto documentDto);

    /**
     * Удалить документ
     *
     * @param ids идентификаторы документов
     */
    void deleteAll(Set<Long> ids);

    /**
     * Удалить документ по ид
     *
     * @param id идентификатор документа
     */
    void delete(Long id);

    /**
     * Отправить документ на обработку
     *
     * @param idDto идентификатор документа
     * @return документ со статусом "в обработке"
     * @throws DocumentNotFoundException      если документ не найден
     * @throws IllegalDocumentStatusException если документ уже был обработан
     */
    DocumentDto sendForProcessing(IdDto idDto);


    /**
     * Получить все документы
     *
     * @return список документов
     */
    List<DocumentDto> findAll();

    /**
     * Получить документ по номеру
     *
     * @param id идентификатор
     * @return документ
     * @throws DocumentNotFoundException если документ не найден
     */
    DocumentDto get(Long id);
}
