package com.hhjang.restapidemo.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhjang.restapidemo.MockMvcTest;
import com.hhjang.restapidemo.accounts.Account;
import com.hhjang.restapidemo.accounts.AccountRole;
import com.hhjang.restapidemo.accounts.AccountService;
import com.hhjang.restapidemo.common.TestDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends MockMvcTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventRepository repository;

    @Autowired
    AccountService accountService;

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
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAuthToken())
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
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-event",     // Document 생성
                        links(
                                // 링크 정보를 문서조각에 추가
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("datetime of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("datetime of close of new event"),
                                fieldWithPath("beginEventDateTime").description("datetime of begin of new event"),
                                fieldWithPath("closeEventDateTime").description("datetime of close of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        // relaxed를 사용하면 response에서 오지 않는 값에 대해서 검증하지 않음
                        // -> 엄격하지 않음. 따라서 명확한 Document 생성과 테스트 코드를 통한 검증이 확실하지 않아지므로 쓰지않는게 맞다고 생각
                        // relaxedResponseFields
                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("datetime of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("datetime of close of new event"),
                                fieldWithPath("beginEventDateTime").description("datetime of begin of new event"),
                                fieldWithPath("closeEventDateTime").description("datetime of close of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new event"),
                                fieldWithPath("offline").description("offline of new event"),
                                fieldWithPath("free").description("free of new event"),
                                fieldWithPath("eventStatus").description("event status of new event"),

                                // optional -
                                fieldWithPath("_links.self.href").description("self href of new event").optional(),
                                fieldWithPath("_links.query-events.href").description("query events href of new event").optional(),
                                fieldWithPath("_links.update-event.href").description("update event href of new event").optional(),
                                fieldWithPath("_links.profile.href").description("profile event href of new event").optional(),

                                // manager 에 대한 로직이 아직 진행하지 않았으므로 임시로 optional 추가
                                fieldWithPath("manager").description("manager of new event").optional()
                        )
                ))
        ;
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
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 2번째 페이지 조회하기")
    public void getEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When
        mockMvc.perform(get("/api/events/")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        // TODO Add Document Description
        ;
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event " + i)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 9, 21, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 22, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2020, 9, 20, 11, 11))
                .closeEventDateTime(LocalDateTime.of(2020, 9, 23, 11, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("죽전역 근처")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return repository.save(event);
    }

    @Test
    @TestDescription("이벤트 1개를 조회한다")
    public void getEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        // When & then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        // TODO Add Document
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 조회 시 404 응답")
    public void getEvent_NotFound() throws Exception {
        // Given
        int notExistId = 123123;

        // When & then
        this.mockMvc.perform(get("/api/events/{id}", String.valueOf(notExistId)))
                .andDo(print())
                .andExpect(status().isNotFound())
        // TODO Add Document
        ;
    }

    // 수정 테스트
    // 1. 존재하는 이벤트를 수정 성공
    // 2. 존재하는 이벤트를 수정 시 값이 잘못된 실패(not valid)
    // 3. 존재하는 이벤트를 수정 시 값이 비어있을 경우 실패
    // 4. 존재하지 않는 이벤트 수정 에러
    @Test
    @TestDescription("이벤트가 존재할 때에 수정을 성공하는 테스트")
    public void updateEvent() throws Exception {
        // Given
        Event generatedEvent = generateEvent(1);
        String modifiedName = "modified hh-jang";

        EventDto modifiedDto = modelMapper.map(generatedEvent, EventDto.class);
        modifiedDto.setName(modifiedName);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", generatedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(modifiedDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(generatedEvent.getId()))
                .andExpect(jsonPath("name").value("modified hh-jang"))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;
    }

    @Test
    @TestDescription("이벤트가 존재할때에 잘못된 수정 내용일 때에 badRequest")
    public void updateEvent_Bad_Request_Wrong_Input() throws Exception {
        // Given
        Event generatedEvent = generateEvent(1);
        String modifiedName = "modified hh-jang";

        EventDto modifiedDto = modelMapper.map(generatedEvent, EventDto.class);
        modifiedDto.setBeginEnrollmentDateTime(LocalDateTime.of(2020, 9, 23, 11, 11));
        modifiedDto.setCloseEnrollmentDateTime(LocalDateTime.of(2020, 9, 22, 11, 11));
        modifiedDto.setBeginEventDateTime(LocalDateTime.of(2020, 9, 24, 11, 11));
        modifiedDto.setCloseEventDateTime(LocalDateTime.of(2020, 9, 23, 11, 11));

        // When & Then
        mockMvc.perform(put("/api/events/{id}", generatedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(modifiedDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("이벤트가 존재할때에 비어있는 값을 보낼 경우 badRequest")
    public void updateEvent_Bad_Request_Empty_Input() throws Exception {
        // Given
        Event generatedEvent = generateEvent(1);
        EventDto modifiedDto = EventDto.builder().build();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", generatedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(modifiedDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 시 404")
    public void updateEvent_Not_Found() throws Exception {
        // Given
        int notExistId = 123123;
        EventDto eventDto = EventDto.builder().build();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", notExistId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    public String getAuthToken() throws Exception {
        String clientId = "clientIdTestValue";
        String clientSecret = "clientSecretTestValue";
        String username = "hhjang@gmail.com";
        String password = "hhjang1";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.accountService.saveAccount(account);

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print());

        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jsonParser = new Jackson2JsonParser();
        return jsonParser.parseMap(responseBody).get("access_token").toString();
    }
}