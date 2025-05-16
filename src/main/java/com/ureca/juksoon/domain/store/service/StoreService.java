package com.ureca.juksoon.domain.store.service;

import com.ureca.juksoon.domain.store.dto.request.StoreReq;
import com.ureca.juksoon.domain.store.dto.response.CreateStoreRes;
import com.ureca.juksoon.domain.store.dto.response.ModifyStoreRes;
import com.ureca.juksoon.domain.store.dto.response.StoreReadRes;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.store.repository.StoreRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.s3.FilePath;
import com.ureca.juksoon.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ureca.juksoon.global.response.ResultCode.STORE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    /**
     * 가게 생성
     */
    @Transactional
    public CreateStoreRes createStore(Long userId, StoreReq req) {

        // S3에 로고 이미지 먼저 업로드
        String imageURL = "";
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            imageURL = s3Service.uploadFile(req.getImage(), FilePath.STORE);
        }

        User user = userRepository.getReferenceById(userId);
        Store store = Store.of(user, req, imageURL);

        Store saveStore = storeRepository.save(store);

        return new CreateStoreRes(saveStore.getId());
    }

    /**
     * 가게 정보 조회
     */
    public StoreReadRes readStore(Long userId) {

        Store store = findStoreByUserId(userId);

        return StoreReadRes.from(store);
    }

    /**
     * 가게 정보 수정
     */
    @Transactional
    public ModifyStoreRes updateStore(Long userId, StoreReq req) {

        // 이전 파일 가져오기
        Store store = findStoreByUserId(userId);
        String logoImageURL = store.getLogoImageURL();

        if (req.getImage() != null && !req.getImage().isEmpty()) {
            // 이전 파일이 있다면 삭제
            if(logoImageURL != null){
                s3Service.deleteMultiFiles(List.of(logoImageURL), FilePath.STORE);
            }

            logoImageURL = s3Service.uploadFile(req.getImage(), FilePath.STORE);
        }

        // 수정
        store.update(req, logoImageURL);

        return new ModifyStoreRes(store.getId());
    }

    private Store findStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId);
        if (store == null) throw new GlobalException(STORE_NOT_FOUND);
        return store;
    }

}
