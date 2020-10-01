package com.hhjang.restapidemo.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhjang.restapidemo.common.TestDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext applicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    // junit5에서 관련 기능 지원하는게 있으니 나중에 변경하기
    @TestDescription("정상적으로 이벤트를 생성하는 코드")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("hh-jang")
                .description("테스트 데이터")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 21, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 22, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2020, 9, 20, 11, 11))
                .closeEventDateTime(LocalDateTime.of(2020, 9, 23, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("죽전역 근처")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsBytes(eventDto)))
                .andDo(print())
                .andExpect(jsonPath("id").exists())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(not(100)))
                .andExpect(jsonPath("free").value(not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우 400 에러가 발생하는 코드")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("hh-jang")
                .description("테스트 데이터")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 21, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 22, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2020, 9, 20, 11, 11))
                .closeEventDateTime(LocalDateTime.of(2020, 9, 23, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("죽전역 근처")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어있는 경우 에러가 발생하는 테스트")
    public void createEvent_BadRequest_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().
                build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못되었을 경우 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto event = EventDto.builder()
                .name("hh-jang")
                .description("테스트 데이터")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 23, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 22, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2020, 9, 24, 11, 11))
                .closeEventDateTime(LocalDateTime.of(2020, 9, 23, 11, 11))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("죽전역 근처")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(event)))
                .andDo(print())
                // TODO global 외에 field error 처리 추가
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(status().isBadRequest());
    }
}