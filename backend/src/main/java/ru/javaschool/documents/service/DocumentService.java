package ru.javaschool.documents.service;

import ru.javaschool.documents.controller.dto.DocumentDto;
import ru.javaschool.documents.exception.DocumentNotFoundException;

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
     * Обновить документ
     *
     * @param documentDto документ
     * @return обновленный документ
     * @throws DocumentNotFoundException если документ не найден
     */
    DocumentDto sendForProcessing(DocumentDto documentDto);


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
