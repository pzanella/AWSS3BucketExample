package com.example.utilities;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;

public class Utilities {
	
	public S3Mock api;

	public AmazonS3 startConnection() {
		this.api = S3Mock.create(8001, "/tmp/s3");
	  	this.api.start();

	    AmazonS3 client = AmazonS3ClientBuilder
	            .standard()
	            .withPathStyleAccessEnabled(true)
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "eu-west-2"))
	            .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
	            .build();
	    return client;
	}
	
	public void closeConnection() {
		this.api.stop();
	}
}
