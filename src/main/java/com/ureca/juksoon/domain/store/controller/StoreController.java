package com.ureca.juksoon.domain.store.controller;

import com.ureca.juksoon.domain.store.dto.request.StoreCreateReq;
import com.ureca.juksoon.domain.store.dto.response.StoreRes;
import com.ureca.juksoon.domain.store.dto.request.StoreUpdateReq;
import com.ureca.juksoon.domain.store.service.StoreService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public CommonResponse<?> createStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody StoreCreateReq request) {
        return storeService.createStore(customUserDetails.getUserId(), request);
    }

    @GetMapping
    public CommonResponse<StoreRes> readStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return storeService.readStore(customUserDetails.getUserId());
    }

    @PutMapping
    public CommonResponse<StoreRes> updateStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody StoreUpdateReq request) {

        return storeService.updateStore(customUserDetails.getUserId(), request);
    }
}
