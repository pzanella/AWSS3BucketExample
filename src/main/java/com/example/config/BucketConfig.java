package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class BucketConfig {

	@Value("${aws.bucket.accessKeyId}")
	private String accessKeyId;

	@Value("${aws.bucket.secretAccessKey}")
	private String secretAccessKey;

	@Value("${aws.bucket.region}")
	private String region;

	@Bean
	@Profile("int")
	public AmazonS3 s3client() {

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_2)
				.withCredentials(new ProfileCredentialsProvider()).build();
		return s3Client;
	}

	@Bean
	@Profile("dev")
	public AmazonS3 s3clientDev() {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withPathStyleAccessEnabled(true)
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "eu-west-2"))
				.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials())).build();
		return s3Client;
	}

}