package org.ever._4ever_be_gw.domain.mm;

import org.ever._4ever_be_gw.scmpp.controller.MmController;
import org.ever._4ever_be_gw.scmpp.service.MmStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmPurchaseUpdateTest.MockConfig.class)
class MmPurchaseUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MmStatisticsService mmStatisticsService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mmStatisticsService);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MockConfig {
        @Bean
        MmStatisticsService mmStatisticsService() {
            return Mockito.mock(MmStatisticsService.class);
        }
    }

    @Test
    @DisplayName("구매요청서 수정 성공")
    void updatePurchaseRequisition_success() throws Exception {
        LocalDate desiredDate = LocalDate.now().plusDays(7);
        String body = "{\n" +
                "  \"desiredDeliveryDate\": \"" + desiredDate + "\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"op\": \"ADD\",\n" +
                "      \"lineNo\": 3,\n" +
                "      \"itemName\": \"화이트보드 마커\",\n" +
                "      \"quantity\": 50,\n" +
                "      \"uomName\": \"EA\",\n" +
                "      \"expectedUnitPrice\": 3000,\n" +
                "      \"preferredVendorName\": \"문구나라\",\n" +
                "      \"purpose\": \"소모품 보충\",\n" +
                "      \"note\": \"색상 혼합\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"op\": \"UPDATE\",\n" +
                "      \"id\": 900001,\n" +
                "      \"quantity\": 12,\n" +
                "      \"expectedUnitPrice\": 14000,\n" +
                "      \"note\": \"수량/단가 재산정\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"op\": \"REMOVE\",\n" +
                "      \"id\": 900002\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(put("/api/scm-pp/mm/purchase-requisitions/{prId}", 102345L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구매요청서가 수정되었습니다."))
                .andExpect(jsonPath("$.data.id").value(102345))
                .andExpect(jsonPath("$.data.desiredDeliveryDate").value(desiredDate.toString()))
                .andExpect(jsonPath("$.data.itemCount").value(2))
                .andExpect(jsonPath("$.data.totalExpectedAmount").value(318000))
                .andExpect(jsonPath("$.data.items[0].quantity").value(12))
                .andExpect(jsonPath("$.data.items[0].expectedUnitPrice").value(14000))
                .andExpect(jsonPath("$.data.items[0].note").value("수량/단가 재산정"))
                .andExpect(jsonPath("$.data.items[1].itemName").value("화이트보드 마커"))
                .andExpect(jsonPath("$.data.items[1].expectedTotalPrice").value(150000));
    }

    @Test
    @DisplayName("비허용 상태 수정 시 409")
    void updatePurchaseRequisition_conflict() throws Exception {
        String body = "{ \"desiredDeliveryDate\": \"2030-01-01\" }";

        mockMvc.perform(put("/api/scm-pp/mm/purchase-requisitions/{prId}", 102346L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("현재 상태에서는 수정이 허용되지 않습니다. (required: NON_STOCK & PENDING)"));
    }

    @Test
    @DisplayName("구매요청서 수정 검증 실패 422")
    void updatePurchaseRequisition_validationErrors() throws Exception {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String body = "{\n" +
                "  \"desiredDeliveryDate\": \"" + pastDate + "\",\n" +
                "  \"items\": [\n" +
                "    { \"op\": \"INVALID\" },\n" +
                "    { \"op\": \"ADD\", \"quantity\": 0 },\n" +
                "    { \"op\": \"UPDATE\", \"id\": 999999, \"quantity\": -1 }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(put("/api/scm-pp/mm/purchase-requisitions/{prId}", 102345L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 본문 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors[*].field", hasItem("desiredDeliveryDate")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("PAST_DATE")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("items[0].op")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("ALLOWED_VALUES: ADD, UPDATE, REMOVE")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("items[1].quantity")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("MUST_BE_POSITIVE")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("items[2].id")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("NOT_FOUND")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("items[2].quantity")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("MUST_BE_POSITIVE")));
    }
}

