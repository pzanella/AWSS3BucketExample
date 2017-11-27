package com.example.services;

import com.amazonaws.services.s3.AmazonS3;

public interface BucketServices {
	
	public void uploadFile(String keyName, String uploadFilePath, AmazonS3 s3client);
	
	public void downloadFile(String keyName, AmazonS3 s3client);
	
	public boolean deleteFiles(String keyName, AmazonS3 s3client);

}
