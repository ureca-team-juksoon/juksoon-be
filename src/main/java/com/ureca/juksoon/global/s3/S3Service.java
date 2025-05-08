package com.ureca.juksoon.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ureca.juksoon.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.ureca.juksoon.global.response.ResultCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile, FilePath filePath) {
        // 이미지 파일인지 확인
        String contentType = multipartFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GlobalException(INVALID_IMAGE_FILE);
        }

        // 업로드할 파일의 고유한 파일명 생성 (파일 덮어쓰기 방지)
        String fileName = filePath.getPath() + createFileName(multipartFile.getOriginalFilename());
        // 업로드할 파일의 메타데이터 생성
        ObjectMetadata metadata = setObjectMetadata(multipartFile);

        try {
            // S3에 파일 업로드
            amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), metadata);
        } catch (Exception e) { // 업로드 중 예외 발생 시 오류 발생
            log.info("file upload fail: {}", e.getMessage());
            throw new GlobalException(SYSTEM_ERROR);
        }

        // 업로드한 파일의 URL 반환
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileUrl, FilePath filePath) {
        // 파일명 추출 (Ex. path/to/file.jpg)
        String fileName = extractFileName(fileUrl, filePath);

        // 파일이 존재하지 않으면 예외 발생
        if (!amazonS3.doesObjectExist(bucketName, fileName)) {
            throw new GlobalException(NOT_FOUND_FILE);
        }

        try {
            amazonS3.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            log.error("file delte fail: {}", e.getMessage());
            throw new GlobalException(SYSTEM_ERROR);
        }
    }


    // 업로드할 파일의 메타데이터를 설정
    private static ObjectMetadata setObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }

    // UUID로 생성한 고유한 문자열 + 기존 파일명으로 고유한 파일 이름 생성
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(fileName);
    }

    // Url 중 파일명만 추출
    private String extractFileName(String fileUrl, FilePath filePath) {
        int startIdx = fileUrl.indexOf(filePath.getPath());
        if(startIdx == -1) throw new GlobalException(NOT_FOUND_FILE);
        return fileUrl.substring(startIdx);
    }
}
