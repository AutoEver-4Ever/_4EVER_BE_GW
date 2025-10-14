package org.ever._4ever_be_gw.scmpp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/scm-pp/pp")
@Tag(name = "Production Management", description = "생산 관리 API")
public class ProductionController {
    @PostMapping("/boms")
    @Operation(
            summary = "BOM 생성",
            description = "새로운 BOM을 생성합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM이 성공적으로 생성되었습니다.\",\n  \"data\": {\n    \"bomId\": 1,\n    \"bomCode\": \"BOM-001\"\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBom(
            @RequestBody BomCreateRequestDto request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("bomId", 1L);
        response.put("bomCode", "BOM-001");

        return ResponseEntity.ok(ApiResponse.success(response, "BOM이 성공적으로 생성되었습니다.", HttpStatus.OK));
    }
}
