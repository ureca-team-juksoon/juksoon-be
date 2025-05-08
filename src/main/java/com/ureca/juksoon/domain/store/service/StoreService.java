package com.ureca.juksoon.domain.store.service;

import com.ureca.juksoon.domain.store.dto.request.StoreCreateReq;
import com.ureca.juksoon.domain.store.dto.response.StoreRes;
import com.ureca.juksoon.domain.store.dto.request.StoreUpdateReq;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.store.repository.StoreRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommonResponse<?> createStore(Long userId, StoreCreateReq request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.USER_NOT_FOUNT));

        boolean isExist = storeRepository.existsByUser(findUser);

        if (isExist) throw new GlobalException(ResultCode.ALREADY_EXISTS_STORE);

        Store store = toEntity(request, findUser);

        storeRepository.save(store);

        return CommonResponse.success("ok");
    }

    @Transactional(readOnly = true)
    public CommonResponse<StoreRes> readStore(Long userId) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.USER_NOT_FOUNT));

        Store findStore = storeRepository.findByUser(findUser)
                .orElseThrow(() -> new GlobalException(ResultCode.STORE_NOT_FOUND));

        StoreRes response = toDto(findStore);

        return CommonResponse.success(response);
    }

    @Transactional
    public CommonResponse<StoreRes> updateStore(Long userId, StoreUpdateReq request) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.USER_NOT_FOUNT));

        Store findStore = storeRepository.findByUser(findUser)
                .orElseThrow(() -> new GlobalException(ResultCode.STORE_NOT_FOUND));

        findStore.updateStore(request.getName(), request.getAddress(), request.getDescription(), request.getLogoImage());

        return CommonResponse.success(null);
    }

    private StoreRes toDto(Store store) {
        return StoreRes.builder()
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .logoImage(store.getLogoImage())
                .build();
    }

    private Store toEntity(StoreCreateReq request, User user) {
        return Store.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .logoImage(request.getLogoImage())
                .user(user)
                .build();
    }
}
