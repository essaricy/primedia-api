package com.sednar.digital.media.config.mvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    public static final String API_PATH = "/test/exception";

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TestExceptionHandlerResource())
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void shouldThrowValidationException() throws Exception {
        String url = API_PATH + "/validation";
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation exception"))
                .andExpect(jsonPath("$.timestamp").value(Matchers.any(Long.class)))
                .andExpect(jsonPath("$.url").value("uri=" + url)
                );
    }

    @Test
    void shouldThrowMediaException() throws Exception {
        String url = API_PATH + "/media";
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("media exception"))
                .andExpect(jsonPath("$.timestamp").value(Matchers.any(Long.class)))
                .andExpect(jsonPath("$.url").value("uri=" + url)
                );
    }

}