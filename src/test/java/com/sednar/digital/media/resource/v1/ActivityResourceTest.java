package com.sednar.digital.media.resource.v1;

import com.sednar.digital.media.common.type.Type;
import com.sednar.digital.media.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
//@WebMvcTest(ActivityResource.class)
public class ActivityResourceTest {

    @InjectMocks
    ActivityResource activityResource;

    @Mock
    private ActivityService activityService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(activityResource)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    public void test_generateThumbs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/{type}/generate/thumbs", Type.IMAGE.getCode())
                .accept(MediaType.APPLICATION_JSON))
                //.andDo(MockMvcResultMatchers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].employeeId").isNotEmpty());
    }

}