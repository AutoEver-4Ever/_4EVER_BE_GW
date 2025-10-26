package org.ever._4ever_be_gw.common.dto.pagable;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.ever._4ever_be_gw.common.dto.PageDto;


@Data
@Builder
public class PageResponseDto<T> {

    private List<T> items;
    private PageDto page;
}