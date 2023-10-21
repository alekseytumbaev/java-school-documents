package ru.template.example.documents.controller.dto;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Status {

    @NotBlank
    @Length(max = 16)
    private String code;

    @NotNull
    private String name;

    public static Status of(String code, String name) {
        Status codeName = new Status();
        codeName.setCode(code);
        codeName.setName(name);
        return codeName;
    }
}
