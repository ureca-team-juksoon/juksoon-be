package com.ureca.juksoon.domain.store.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StoreReq {
    private String name;
    private String address;
    private String description;
    private MultipartFile image;
}
