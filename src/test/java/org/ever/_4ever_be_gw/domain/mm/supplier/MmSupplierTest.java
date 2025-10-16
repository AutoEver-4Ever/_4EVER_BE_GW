package org.ever._4ever_be_gw.domain.mm.supplier;

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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmSupplierTest.MockConfig.class)
class MmSupplierTest {

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
        mockMvc.perform(get("/api/scm-pp/mm/supplier")
                        .servletPath("/api")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("공급업체 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].supplierInfo").exists())
                .andExpect(jsonPath("$.data.content[0].supplierInfo.supplierName").exists())
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.size").value(10))
                .andExpect(jsonPath("$.data.page.totalElements").value(50))
                .andExpect(jsonPath("$.data.page.hasNext").value(true));
    }

    @Test
    @DisplayName("공급업체 목록 검증 실패 422")
    void getVendors_validationErrors() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/supplier")
                        .servletPath("/api")
                        .queryParam("status", "BAD")
                        .queryParam("page", "-1")
                        .queryParam("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].reason", hasItem("MIN_0")));
    }

    @Test
    @DisplayName("공급업체 목록 권한 없음 403(모킹)")
    void getVendors_forbidden() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/supplier")
                        .servletPath("/api")
                        .queryParam("category", "금지")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("공급업체 조회 권한이 없습니다."));
    }

    @Test
    @DisplayName("공급업체 상세 조회 성공(1~10)")
    void getVendorDetail_success() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/supplier/{supplierId}", 1L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("공급업체 상세 정보를 조회했습니다."))
                .andExpect(jsonPath("$.data.supplierInfo").exists())
                .andExpect(jsonPath("$.data.supplierInfo.supplierName").exists())
                .andExpect(jsonPath("$.data.supplierInfo.deliveryLeadTime").isNumber())
                .andExpect(jsonPath("$.data.managerInfo").exists())
                .andExpect(jsonPath("$.data.managerInfo.managerEmail").exists());
    }

    @Test
    @DisplayName("공급업체 상세 권한 없음 403(모킹)")
    void getVendorDetail_forbidden() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/supplier/{supplierId}", 403001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("공급업체 조회 권한이 없습니다."));
    }

    @Test
    @DisplayName("공급업체 상세 미존재 404")
    void getVendorDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/supplier/{supplierId}", 51L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("해당 공급업체를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("공급업체 상세 서버 오류 500(모킹)")
    void getVendorDetail_serverError() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/supplier/{supplierId}", 500001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("공급업체 조회 처리 중 오류가 발생했습니다."));
    }
}
