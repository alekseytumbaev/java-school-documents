package ru.template.example.documents.controller.dto;

import lombok.Data;

@Data
public class Status {
    private String code;
    private String name;

    public static Status of(String code, String name) {
        Status codeName = new Status();
        codeName.setCode(code);
        codeName.setName(name);
        return codeName;
    }
}
