package ru.template.example.documents.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.template.example.controller.AbstractWebMvcTest;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.service.DocumentServiceImpl;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DocumentControllerTest extends AbstractWebMvcTest {

    private static final String ROUTE = "/documents";

    @MockBean
    private DocumentServiceImpl service;

    @Test
    public void shouldSuccessToPostWhenRequiredFieldsMaxLength() throws Exception {
        var name = randomAlphabetic(100);

        when(service.save(any())).thenReturn(any());

        var cityDto = new DocumentDto();
        cityDto.setOrganization(name);
        mvc.perform(postAction(ROUTE, cityDto)).andExpect(status().isOk());
    }
}
