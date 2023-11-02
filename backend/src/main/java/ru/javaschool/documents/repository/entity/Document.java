package ru.javaschool.documents.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Тип документа, может быть любым
     */
    @Column
    private String type;

    /**
     * Медицинская организация-владелец документа
     */
    @Column
    private String organization;

    @Column
    private String description;

    /**
     * Дата создания документа
     */
    @Column
    private Date date;

    /**
     * Пациент, к которому относится документ
     */
    @Column
    private String patient;

    /**
     * Статус документа
     */
    @Column
    @Enumerated(EnumType.STRING)
    private StatusCode status;
}
