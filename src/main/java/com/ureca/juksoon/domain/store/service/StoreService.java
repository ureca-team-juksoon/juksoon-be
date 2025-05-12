package com.ureca.juksoon.domain.store.service;

import com.ureca.juksoon.domain.store.dto.request.StoreCreateReq;
import com.ureca.juksoon.domain.store.dto.request.StoreUpdateReq;
import com.ureca.juksoon.domain.store.dto.response.StoreReadRes;
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
import java.util.ArrayList;
import java.util.List;

import static com.ureca.juksoon.global.response.ResultCode.STORE_NOT_FOUND;

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
    public CommonResponse<StoreReadRes> readStore(Long userId) {

        Store findStore = findStoreByUserId(userId);

        StoreReadRes response = toDto(findStore);

        return CommonResponse.success(response);
    }

    @Transactional
    public CommonResponse<StoreReadRes> updateStore(Long userId, StoreUpdateReq request, MultipartFile image) throws UnsupportedEncodingException {

        Store findStore = findStoreByUserId(userId);

        String logoImageURL = findStore.getLogoImageURL();
        //이전 파일 삭제 및 재업로드
        if (image != null && !image.isEmpty()) {
            s3Service.deleteMultiFiles(new ArrayList<>(List.of(findStore.getLogoImageURL())), FilePath.STORE);
            logoImageURL = s3Service.uploadFile(image, FilePath.STORE);
        }

        findStore.updateStore(request.getName(), request.getAddress(), request.getDescription(), logoImageURL);

        return CommonResponse.success(null);
    }

    private Store findStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId);
        if(store == null) throw new GlobalException(STORE_NOT_FOUND);
        return store;
    }

    private StoreReadRes toDto(Store store) {
        return StoreReadRes.builder()
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
