package com.ureca.juksoon.domain.store.controller;

import com.ureca.juksoon.domain.store.dto.request.StoreCreateReq;
import com.ureca.juksoon.domain.store.dto.request.StoreUpdateReq;
import com.ureca.juksoon.domain.store.dto.response.StoreReadRes;
import com.ureca.juksoon.domain.store.service.StoreService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public CommonResponse<?> createStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart StoreCreateReq request,
            @RequestPart(required = false) MultipartFile image) {
        return storeService.createStore(customUserDetails.getUserId(), request, image);
    }

    @GetMapping
    public CommonResponse<StoreReadRes> readStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return storeService.readStore(customUserDetails.getUserId());
    }

    @PutMapping
    public CommonResponse<StoreReadRes> updateStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart StoreUpdateReq request,
            @RequestPart(required = false) MultipartFile image) throws UnsupportedEncodingException {

        return storeService.updateStore(customUserDetails.getUserId(), request, image);
    }
}
