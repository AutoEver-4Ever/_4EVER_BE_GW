package org.ever._4ever_be_gw.scmpp.dto.mrp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrpRequestBodyDto {
    private Long[] plannedId;
}
