package com.ureca.juksoon.domain.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreCreateReq {
    private String name;
    private String address;
    private String description;
}
