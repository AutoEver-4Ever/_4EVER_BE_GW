package org.ever._4ever_be_gw.domain.sd;

import org.ever._4ever_be_gw.business.controller.SdController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SdController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
class SdQuotationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("신규 견적서 생성 성공(201)")
    void createQuotation_success() throws Exception {
        String futureDate = LocalDate.now().plusDays(10).toString();
        String body = "{\n" +
                "  \"dueDate\": \"" + futureDate + "\",\n" +
                "  \"items\": [\n" +
                "    { \"itemId\": 10001, \"quantity\": 10, \"unitPrice\": 500000 },\n" +
                "    { \"itemId\": 10002, \"quantity\": 5,  \"unitPrice\": 200000 }\n" +
                "  ],\n" +
                "  \"note\": \"긴급 납품 요청\"\n" +
                "}";

        mockMvc.perform(post("/api/business/sd/quotations").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("신규 견적서 등록이 완료되었습니다."))
                .andExpect(jsonPath("$.data.quotationId").exists())
                .andExpect(jsonPath("$.data.quotationDate").exists())
                .andExpect(jsonPath("$.data.dueDate").value(futureDate))
                .andExpect(jsonPath("$.data.totalAmount").value(6000000))
                .andExpect(jsonPath("$.data.statusCode").value("PENDING"))
                .andExpect(jsonPath("$.data.statusLabel").value("대기"));
    }

    @Test
    @DisplayName("신규 견적서 생성 실패 - 납기일 과거/오늘")
    void createQuotation_dueDateInvalid_400() throws Exception {
        String todayOrPast = LocalDate.now().toString();
        String body = "{\n" +
                "  \"dueDate\": \"" + todayOrPast + "\",\n" +
                "  \"items\": [ { \"itemId\": 10001, \"quantity\": 1, \"unitPrice\": 1 } ]\n" +
                "}";

        mockMvc.perform(post("/api/business/sd/quotations").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청 납기일은 현재 날짜 이후여야 합니다."));

    }

    @Test
    @DisplayName("신규 견적서 생성 실패 - items 비어있음(422)")
    void createQuotation_itemsEmpty_422() throws Exception {
        String futureDate = LocalDate.now().plusDays(5).toString();
        String body = "{\n" +
                "  \"dueDate\": \"" + futureDate + "\",\n" +
                "  \"items\": [ ],\n" +
                "  \"note\": \"긴급 납품 요청\"\n" +
                "}";

        mockMvc.perform(post("/api/business/sd/quotations").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("items는 1개 이상이어야 합니다."));
    }
}
