package org.ever._4ever_be_gw.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PageResponseDto<T> {

    private List<T> items;
    private PageDto page;
}