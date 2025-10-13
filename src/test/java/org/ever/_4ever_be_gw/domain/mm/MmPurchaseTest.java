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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmPurchaseTest.MockConfig.class)
class MmPurchaseTest {

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
    @DisplayName("구매요청 목록 기본 페이지 성공")
    void getPurchaseRequisitions_success() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구매요청서 목록입니다."))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.size").value(20));
    }

    @Test
    @DisplayName("구매요청 목록 - 요청자명 검색")
    void getPurchaseRequisitions_filterByRequesterName() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .queryParam("requesterName", "김민수")
                        .queryParam("sort", "createdAt,desc")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("구매요청 목록 - 부서 필터링")
    void getPurchaseRequisitions_filterByDepartment() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .queryParam("departmentId", "15")
                        .queryParam("sort", "createdAt,asc")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("검증 실패 시 422와 errors 배열")
    void getPurchaseRequisitions_validationErrors() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .queryParam("createdFrom", "2024-13-01")
                        .queryParam("createdTo", "2024-01-32")
                        .queryParam("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("createdFrom"))
                .andExpect(jsonPath("$.errors[0].reason").value("INVALID_DATE"))
                .andExpect(jsonPath("$.errors[1].field").value("createdTo"))
                .andExpect(jsonPath("$.errors[1].reason").value("INVALID_DATE"))
                .andExpect(jsonPath("$.errors[2].field").value("size"))
                .andExpect(jsonPath("$.errors[2].reason").value("MAX_200"));
    }

    @Test
    @DisplayName("권한 제한 범위 조회시 403")
    void getPurchaseRequisitions_forbidden() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .queryParam("createdFrom", "2023-12-31")
                        .queryParam("createdTo", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 범위의 데이터를 조회할 권한이 없습니다."));
    }

    @Test
    @DisplayName("발주서 목록 조회 성공(페이지네이션 메타 포함)")
    void getPurchaseOrders_success() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-orders")
                        .servletPath("/api")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .queryParam("sort", "orderDate,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("발주서 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.size").value(10))
                .andExpect(jsonPath("$.data.page.totalElements").value(10))
                .andExpect(jsonPath("$.data.page.totalPages").value(1))
                .andExpect(jsonPath("$.data.page.hasNext").value(false))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].id").value(1001))
                .andExpect(jsonPath("$.data.orders[9].id").value(1010));
    }

    @Test
    @DisplayName("발주서 목록 검증 실패 422(status 허용값 오류)")
    void getPurchaseOrders_validationError_status() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-orders")
                        .servletPath("/api")
                        .queryParam("status", "INVALID")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("status"))
                .andExpect(jsonPath("$.errors[0].reason").value("ALLOWED_VALUES: APPROVED, PENDING, DELIVERED"));
    }

    @Test
    @DisplayName("발주서 목록 권한 없음 403")
    void getPurchaseOrders_forbidden() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-orders")
                        .servletPath("/api")
                        .queryParam("orderDateFrom", "2023-12-31")
                        .queryParam("orderDateTo", "2024-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 데이터를 조회할 권한이 없습니다."));
    }

    @Test
    @DisplayName("발주서 목록 서버 오류 500(모킹 트리거)")
    void getPurchaseOrders_serverError() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-orders")
                        .servletPath("/api")
                        .queryParam("sort", "error")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("발주서 상세 조회 성공(1~10)")
    void getPurchaseOrderDetail_success() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-orders/{purchaseId}", 1L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("발주서 상세 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.poNumber").exists())
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    @DisplayName("발주서 상세 미존재 404(범위 밖)")
    void getPurchaseOrderDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-orders/{purchaseId}", 11L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("해당 발주서를 찾을 수 없습니다: poId=11"));
    }
    @Test
    @DisplayName("구매요청 상세 조회 성공(1~10)")
    void getPurchaseRequisitionDetail_success() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions/{purchaseId}", 1L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구매요청서 상세입니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.prNumber").value("PR-2024-001"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.totalAmount").value(2550000));
    }

    @Test
    @DisplayName("구매요청 상세 권한 없음 403")
    void getPurchaseRequisitionDetail_forbidden() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions/{purchaseId}", 403001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 구매요청서를 조회할 권한이 없습니다."));
    }

    @Test
    @DisplayName("구매요청 상세 미존재 404(범위 밖)")
    void getPurchaseRequisitionDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/purchase-requisitions/{purchaseId}", 11L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("해당 구매요청서를 찾을 수 없습니다: purchaseId=11"));
    }
}
