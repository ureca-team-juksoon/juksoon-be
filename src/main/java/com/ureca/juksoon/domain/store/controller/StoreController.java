package com.ureca.juksoon.domain.store.controller;

import com.ureca.juksoon.domain.store.dto.request.StoreReq;
import com.ureca.juksoon.domain.store.dto.response.CreateStoreRes;
import com.ureca.juksoon.domain.store.dto.response.ModifyStoreRes;
import com.ureca.juksoon.domain.store.dto.response.StoreReadRes;
import com.ureca.juksoon.domain.store.service.StoreService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public CommonResponse<CreateStoreRes> createStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute StoreReq request) {

        return CommonResponse.success(storeService.createStore(customUserDetails.getUserId(), request));
    }

    @GetMapping
    public CommonResponse<StoreReadRes> readStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return CommonResponse.success(storeService.readStore(customUserDetails.getUserId()));
    }

    @PatchMapping
    public CommonResponse<ModifyStoreRes> updateStore(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute StoreReq request) {

        return CommonResponse.success(storeService.updateStore(customUserDetails.getUserId(), request));
    }
}
