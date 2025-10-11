package org.ever._4ever_be_gw.domain.mm;

import org.ever._4ever_be_gw.domain.mm.controller.MmController;
import org.ever._4ever_be_gw.domain.mm.service.MmStatisticsService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmVendorTest.MockConfig.class)
class MmVendorTest {

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
    @DisplayName("공급업체 목록 기본 조회 성공")
    void getVendors_success() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("공급업체 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data.total").value(10))
                .andExpect(jsonPath("$.data.vendors").isArray())
                .andExpect(jsonPath("$.data.vendors[0].vendorId").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrev").value(false));
    }

    @Test
    @DisplayName("공급업체 목록 검증 실패 422")
    void getVendors_validationErrors() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .queryParam("status", "BAD")
                        .queryParam("page", "0")
                        .queryParam("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("공급업체 목록 권한 없음 403(모킹)")
    void getVendors_forbidden() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .queryParam("category", "금지")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("공급업체 조회 권한이 없습니다."));
    }
}
