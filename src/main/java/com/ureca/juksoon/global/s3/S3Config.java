package com.ureca.juksoon.global.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        // AWS IAM 사용자의 자격증명(Access Key, Secret Access Key)으로 리소스 접근 위한 자격증명 객체 생성
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        // 앞선 자격 증명을 AmazonS3ClientBuilder에 등록 (자격 증명을 고정값으로 제공 & S3 사용할 리전 지정 -> Amazon S3 클라이언트 생성
        return (AmazonS3)
            AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

}
