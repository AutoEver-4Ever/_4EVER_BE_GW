package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;
    private boolean hasNext;
}
