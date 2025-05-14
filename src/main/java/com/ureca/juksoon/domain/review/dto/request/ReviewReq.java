package com.ureca.juksoon.domain.review.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ReviewReq {
    private String title;
    private String content;
    private List<MultipartFile> images;
    private MultipartFile video;
}
