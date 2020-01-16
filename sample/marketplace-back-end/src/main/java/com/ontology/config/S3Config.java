//package com.ontology.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class S3Config {
//    @Value("${s3.access.key.id}")
//    private String awsId;
//    @Value("${s3.key.secret}")
//    private String awsKey;
//    @Value("${s3.region}")
//    private String region;
//
//    @Bean
//    public S3Client s3client() {
//        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
//                awsId,
//                awsKey);
//        return S3Client.builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
//    }
//}
