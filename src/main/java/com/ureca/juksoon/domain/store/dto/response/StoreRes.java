package com.ureca.juksoon.domain.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRes {
    private String name;
    private String address;
    private String description;
    private String logoImage;
}
