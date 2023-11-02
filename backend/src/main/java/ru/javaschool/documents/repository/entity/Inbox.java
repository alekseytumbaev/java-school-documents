package ru.javaschool.documents.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.javaschool.documents.controller.dto.ProcessingResultDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "inbox")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inbox {

    /**
     * В качестве id используется ключ сообщения из кафки
     */
    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Column
    private ProcessingResultDto payload;

    @Column(name = "is_read")
    private boolean isRead;

    public Inbox(Long id, ProcessingResultDto payload) {
        this.id = id;
        this.payload = payload;
        this.isRead = false;
    }
}
