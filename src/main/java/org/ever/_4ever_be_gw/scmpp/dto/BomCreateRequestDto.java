package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomCreateRequestDto {
    private String productName;
    private String productCode;
    private String version;
    private List<ComponentDto> components;
    private List<RoutingDto> routing;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDto {
        private Long id;
        private String code;
        private String name;
        private Integer quantity;
        private String unit;
        private String level;
        private String supplier;
        private Integer operationId;
        private String operationName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutingDto {
        private Integer sequence;
        private Integer operationId;
        private String operationName;
        private Integer setupTime;
        private Integer runTime;
    }
}
