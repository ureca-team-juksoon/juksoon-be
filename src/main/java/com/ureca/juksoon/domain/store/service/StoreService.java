package com.ureca.juksoon.domain.store.service;

import com.ureca.juksoon.domain.store.dto.request.StoreCreateReq;
import com.ureca.juksoon.domain.store.dto.request.StoreUpdateReq;
import com.ureca.juksoon.domain.store.dto.response.StoreRes;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.store.repository.StoreRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.response.ResultCode;
import com.ureca.juksoon.global.s3.FilePath;
import com.ureca.juksoon.global.s3.S3Service;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final EntityManager em;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    @Transactional
    public CommonResponse<?> createStore(Long userId, StoreCreateReq request, MultipartFile image) {

        //해당 userId로 이미 가게가 존재하면 가게 정보 생성 불가
        boolean isExist = storeRepository.existsByUserId(userId);
        if (isExist) throw new GlobalException(ResultCode.ALREADY_EXISTS_STORE);
        String imageURL = "EMPTY";
        //이미지 파일이 왔을 경우 업로드 메서드 동작
        if (image != null && !image.isEmpty()) {
            imageURL = s3Service.uploadFile(image, FilePath.STORE);
        }

        User findUser = em.getReference(User.class, userId);
        Store store = toEntity(request, findUser, imageURL);

        storeRepository.save(store);

        return CommonResponse.success("ok");
    }

    @Transactional(readOnly = true)
    public CommonResponse<StoreRes> readStore(Long userId) {

        Store findStore = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.STORE_NOT_FOUND));

        StoreRes response = toDto(findStore);

        return CommonResponse.success(response);
    }

    @Transactional
    public CommonResponse<StoreRes> updateStore(Long userId, StoreUpdateReq request, MultipartFile image) throws UnsupportedEncodingException {

        Store findStore = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.STORE_NOT_FOUND));
        String logoImageURL = findStore.getLogoImageURL();
        //이전 파일 삭제 및 재업로드
        if (image != null && !image.isEmpty()) {
            s3Service.deleteFile(findStore.getLogoImageURL(), FilePath.STORE);
            logoImageURL = s3Service.uploadFile(image, FilePath.STORE);
        }

        findStore.updateStore(request.getName(), request.getAddress(), request.getDescription(), logoImageURL);

        return CommonResponse.success(null);
    }

    private StoreRes toDto(Store store) {
        return StoreRes.builder()
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .logoImageURL(store.getLogoImageURL())
                .build();
    }

    private Store toEntity(StoreCreateReq request, User user, String imageURL) {
        return Store.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .logoImageURL(imageURL)
                .user(user)
                .build();
    }
}
