package com.ureca.juksoon.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ureca.juksoon.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ureca.juksoon.global.response.ResultCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private static final String IMAGE_JPG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";
    private static final String VIDEO_MP4 = "video/mp4";

    private static final int MAX_UPLOAD_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final int MAX_UPLOAD_VIDEO_SIZE = 100 * 1024 * 1024;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile, FilePath filePath) {
        // 파일 형식 확인
        String contentType = multipartFile.getContentType();

        if (contentType == null || (!contentType.equals(IMAGE_JPG) && !contentType.equals(IMAGE_PNG) && !contentType.equals(VIDEO_MP4))) {
            throw new GlobalException(INVALID_FILE);
        }

        // 각 파일의 크기 확인 (이미지는 10MB, 동영상은 50MB까지 가능)
        long fileSize = multipartFile.getSize();

        if((contentType.equals(VIDEO_MP4) && fileSize > MAX_UPLOAD_VIDEO_SIZE)
            || ((contentType.equals(IMAGE_JPG) || contentType.equals(IMAGE_PNG)) && fileSize > MAX_UPLOAD_IMAGE_SIZE)) {
            throw new GlobalException(MAXIMUM_UPLOAD_FILE_SIZE);
        }

        // 업로드할 파일의 고유한 파일명 생성 (파일 덮어쓰기 방지)
        String fileName =  filePath.getPath() + createFileName(contentType);
        // 업로드할 파일의 메타데이터 생성
        ObjectMetadata metadata = setObjectMetadata(multipartFile);

        try { // S3에 파일 업로드
            amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), metadata);
        } catch (Exception e) { // 업로드 중 예외 발생 시 오류 발생
            log.info("file upload fail: {}", e.getMessage());
            throw new GlobalException(SYSTEM_ERROR);
        }

        // 업로드한 파일의 URL 반환
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteMultiFiles(List<String> fileUrls, FilePath filePath) {
        List<DeleteObjectsRequest.KeyVersion> keys = fileUrls.stream()
            .map(url -> extractFileName(url, filePath))  // Key (파일명) 추출
            .map(DeleteObjectsRequest.KeyVersion::new)
            .collect(Collectors.toList());

        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);

        try {
            amazonS3.deleteObjects(deleteRequest);
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

    // UUID로 생성한 고유한 문자열로 고유한 파일 이름 생성
    private String createFileName(String contentType) {
        return switch (contentType) {
            case IMAGE_PNG -> UUID.randomUUID() + ".png";
            case IMAGE_JPG -> UUID.randomUUID() + ".jpeg";
            case VIDEO_MP4 -> UUID.randomUUID() + ".mp4";
            default -> throw new GlobalException(INVALID_FILE);
        };
    }

    // Url 중 파일명만 추출
    private String extractFileName(String fileUrl, FilePath filePath) {
        int startIdx = fileUrl.indexOf(filePath.getPath());
        if(startIdx == -1) throw new GlobalException(NOT_FOUND_FILE);
        return fileUrl.substring(startIdx);
    }
}
