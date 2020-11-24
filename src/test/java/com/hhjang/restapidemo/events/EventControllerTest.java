package com.hhjang.restapidemo.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhjang.restapidemo.MockMvcTest;
import com.hhjang.restapidemo.accounts.Account;
import com.hhjang.restapidemo.accounts.AccountRepository;
import com.hhjang.restapidemo.accounts.AccountRole;
import com.hhjang.restapidemo.accounts.AccountService;
import com.hhjang.restapidemo.common.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends MockMvcTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    // junit5에서 관련 기능 지원하는게 있으니 나중에 변경하기
    @DisplayName("정상적으로 이벤트를 생성하는 코드")
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                .andExpect(jsonPath("_links.get-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-event",     // Document 생성
                        links(
                                // 링크 정보를 문서조각에 추가
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-events").description("link to query events"),
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
                                fieldWithPath("manager.id").description("account's id of new event"),

                                // optional -
                                fieldWithPath("_links.self.href").description("self href of new event").optional(),
                                fieldWithPath("_links.get-events.href").description("query events href of new event").optional(),
                                fieldWithPath("_links.update-event.href").description("update event href of new event").optional(),
                                fieldWithPath("_links.profile.href").description("profile event href of new event").optional()
                        )
                ))
        ;
    }

    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAuthToken(needToCreateAccount);
    }

    @Test
    @DisplayName("입력받을 수 없는 값을 사용한 경우 400 에러가 발생하는 코드")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 에러가 발생하는 테스트")
    public void createEvent_BadRequest_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().
                build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 잘못되었을 경우 발생하는 테스트")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
    @DisplayName("30개의 이벤트를 10개씩 2번째 페이지 조회하기")
    public void getEvents() throws Exception {
        // Given
        Account account = createAccount();
        IntStream.range(0, 30).forEach(i -> generateEvent(i, account));

        // When
        mockMvc.perform(get("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
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
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("get-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile an existing event"),
                                linkWithRel("first").description("link to first page of events"),
                                linkWithRel("prev").description("link to previous page of events"),
                                linkWithRel("next").description("link to next page of events"),
                                linkWithRel("last").description("link to last page of events"),
                                linkWithRel("create-event").description("link to create event")
                        ),
                        requestParameters(
                                parameterWithName("page").description("page number of events"),
                                parameterWithName("size").description("size of page(offset)"),
                                parameterWithName("sort").description("sort arguments")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[].id").description("id of event"),
                                fieldWithPath("_embedded.eventList[].name").description("name of event"),
                                fieldWithPath("_embedded.eventList[].description").description("description of event"),
                                fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("datetime of begin of event"),
                                fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("datetime of close of event"),
                                fieldWithPath("_embedded.eventList[].beginEventDateTime").description("datetime of begin of event"),
                                fieldWithPath("_embedded.eventList[].closeEventDateTime").description("datetime of close of event"),
                                fieldWithPath("_embedded.eventList[].location").description("location of event"),
                                fieldWithPath("_embedded.eventList[].basePrice").description("base price of event"),
                                fieldWithPath("_embedded.eventList[].maxPrice").description("max price of event"),
                                fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("limit of event"),
                                fieldWithPath("_embedded.eventList[].offline").description("offline of event"),
                                fieldWithPath("_embedded.eventList[].free").description("free of event"),
                                fieldWithPath("_embedded.eventList[].eventStatus").description("event status of event"),
                                fieldWithPath("_embedded.eventList[].manager.id").description("account's id of event"),
                                fieldWithPath("_embedded.eventList[]._links.self.href").description("self href of events"),
                                fieldWithPath("page.size").description("size of event page"),
                                fieldWithPath("page.totalElements").description("number of total elements"),
                                fieldWithPath("page.totalPages").description("number of total pages"),
                                fieldWithPath("page.number").description("current page number"),

                                // optional -
                                fieldWithPath("_links.self.href").description("link to self").optional(),
                                fieldWithPath("_links.profile.href").description("link to profile an existing event").optional(),
                                fieldWithPath("_links.first.href").description("link to first page of events").optional(),
                                fieldWithPath("_links.prev.href").description("link to previous page of events").optional(),
                                fieldWithPath("_links.next.href").description("link to next page of events").optional(),
                                fieldWithPath("_links.last.href").description("link to last page of events").optional(),
                                fieldWithPath("_links.create-event.href").description("link to create event").optional()
                        )
                ))
        ;
    }

    private Event generateEvent(int i, Account account) {
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
                .manager(account)
                .build();
        return eventRepository.save(event);
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
        return eventRepository.save(event);
    }

    @Test
    @DisplayName("이벤트 1개를 조회한다")
    public void getEvent() throws Exception {
        // Given
        Account account = createAccount();
        Event event = this.generateEvent(100, account);

        // When & then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event",     // Document 생성
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile an existing event"),
                                linkWithRel("update-event").description("if authenticated user, provide update link").optional()
                        ),
                        pathParameters(
                                parameterWithName("id").description("id of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of event"),
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("datetime of begin of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("datetime of close of event"),
                                fieldWithPath("beginEventDateTime").description("datetime of begin of event"),
                                fieldWithPath("closeEventDateTime").description("datetime of close of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event"),
                                fieldWithPath("offline").description("offline of event"),
                                fieldWithPath("free").description("free of event"),
                                fieldWithPath("eventStatus").description("event status of event"),
                                fieldWithPath("manager.id").description("account's id of event"),

                                // optional -
                                fieldWithPath("_links.self.href").description("self href of event").optional(),
                                fieldWithPath("_links.profile.href").description("profile event href of event").optional()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 조회 시 404 응답")
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
    @DisplayName("이벤트가 존재할 때에 수정을 성공하는 테스트")
    public void updateEvent() throws Exception {
        // Given
        Account account = createAccount();
        Event generatedEvent = generateEvent(1, account);
        String modifiedName = "modified hh-jang";

        EventDto modifiedDto = modelMapper.map(generatedEvent, EventDto.class);
        modifiedDto.setName(modifiedName);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", generatedEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
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
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event",     // Document 생성
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile an existing event"),
                                linkWithRel("update-event").description("link to update event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type of request header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authenticated user's token"),
                                headerWithName(HttpHeaders.ACCEPT).description("Accept of request header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("datetime of begin of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("datetime of close of event"),
                                fieldWithPath("beginEventDateTime").description("datetime of begin of event"),
                                fieldWithPath("closeEventDateTime").description("datetime of close of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event")
                        ),
                        pathParameters(
                                parameterWithName("id").description("id of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of event"),
                                fieldWithPath("name").description("name of event"),
                                fieldWithPath("description").description("description of event"),
                                fieldWithPath("beginEnrollmentDateTime").description("datetime of begin of event"),
                                fieldWithPath("closeEnrollmentDateTime").description("datetime of close of event"),
                                fieldWithPath("beginEventDateTime").description("datetime of begin of event"),
                                fieldWithPath("closeEventDateTime").description("datetime of close of event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("base price of event"),
                                fieldWithPath("maxPrice").description("max price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of event"),
                                fieldWithPath("offline").description("offline of event"),
                                fieldWithPath("free").description("free of event"),
                                fieldWithPath("eventStatus").description("event status of event"),
                                fieldWithPath("manager.id").description("account's id of event"),

                                // optional -
                                fieldWithPath("_links.self.href").description("self href of event").optional(),
                                fieldWithPath("_links.update-event.href").description("link to update event").optional(),
                                fieldWithPath("_links.profile.href").description("profile event href of event").optional()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("이벤트가 존재할때에 잘못된 수정 내용일 때에 badRequest")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(modifiedDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("이벤트가 존재할때에 비어있는 값을 보낼 경우 badRequest")
    public void updateEvent_Bad_Request_Empty_Input() throws Exception {
        // Given
        Event generatedEvent = generateEvent(1);
        EventDto modifiedDto = EventDto.builder().build();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", generatedEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(modifiedDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 시 404")
    public void updateEvent_Not_Found() throws Exception {
        // Given
        int notExistId = 123123;
        EventDto eventDto = EventDto.builder().build();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", notExistId)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsBytes(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Account createAccount() {
        Account account = Account.builder()
                .email(appProperties.getGeneralUsername())
                .password(appProperties.getGeneralUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        return this.accountService.saveAccount(account);
    }

    public String getAuthToken(boolean needToCreateAccount) throws Exception {
        if(needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getGeneralUsername())
                .param("password", appProperties.getGeneralUserPassword())
                .param("grant_type", "password"))
                .andDo(print());

        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jsonParser = new Jackson2JsonParser();
        return jsonParser.parseMap(responseBody).get("access_token").toString();
    }
}