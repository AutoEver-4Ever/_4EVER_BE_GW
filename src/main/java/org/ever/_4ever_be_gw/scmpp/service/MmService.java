package org.ever._4ever_be_gw.scmpp.service;

import org.ever._4ever_be_gw.scmpp.dto.mm.supplier.SupplierCreateRequestDto;
import reactor.core.publisher.Mono;

public interface MmService {
    Mono<SupplierCreateRequestDto> createSupplier(SupplierCreateRequestDto requestDto);
}
