package com.hhjang.restapidemo.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhjang.restapidemo.MockMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PojoControllerTest extends MockMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Pojo안에 존재하는 Foo의 snippets가 별도 생성된다.")
    public void create_success() throws Exception {
        Foo foo = Foo.builder()
                .fooName("foo name")
                .value(100)
                .build();

        Pojo pojo = Pojo.builder()
                .name("pojo name")
                .foo(foo)
                .build();
        mockMvc.perform(post("/temp/pojo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsBytes(pojo)))
                    .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andDo(document("create-pojo",     // Document 생성
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of pojo"),
                                fieldWithPath("foo.fooName").description("name of foo"),
                                fieldWithPath("foo.value").description("value of foo")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("name").description("name of pojo"),
                                subsectionWithPath("foo").description("description of foo")
                        ),
                        responseFields(
                                beneathPath("foo").withSubsectionId("pojo"),

                                fieldWithPath("fooName").description("name of foo"),
                                fieldWithPath("value").description("value of foo")
                        )
                ));
    }
}