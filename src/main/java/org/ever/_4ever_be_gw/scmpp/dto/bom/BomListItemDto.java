package org.ever._4ever_be_gw.scmpp.dto.bom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomListItemDto {
    private Long bomId;
    private String bomCode;
    private Long productId;
    private String productCode;
    private String productName;
    private String version;
    private String status;
    private LocalDateTime lastModifiedAt;
}
