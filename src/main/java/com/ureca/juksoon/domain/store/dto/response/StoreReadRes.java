package com.ureca.juksoon.domain.store.dto.response;

import com.ureca.juksoon.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreReadRes {
    private String name;
    private String address;
    private String description;
    private String logoImageURL;

    public static StoreReadRes from(Store store) {
        return StoreReadRes.builder()
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .logoImageURL(store.getLogoImageURL())
                .build();
    }
}
