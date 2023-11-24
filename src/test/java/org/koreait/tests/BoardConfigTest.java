package org.koreait.tests;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=test")
public class BoardConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("게시판 설정 저장 테스트 - 유효성 검사")
    void boardConfigTest() throws Exception {
        String body = mockMvc.perform(
                    post("/admin/board/save")
                            .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(Charset.forName("UTF-8"));

        assertTrue(body.contains("게시판 아이디"));
        assertTrue(body.contains("게시판 이름"));
    }
}
