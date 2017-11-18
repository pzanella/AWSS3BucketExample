package com.example.services;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class BucketServicesImpl implements BucketServices {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(BucketServicesImpl.class);
	
	@Value("${aws.bucket.name}")
	private String bucketName;
	
	@Value("${local.download.filepath}")
	private String filePath;
	
	@Override
	public void uploadFile(String keyName, String uploadFilePath, AmazonS3 s3client) {

		logger.info("uploadFile()");
		
		try {

			File file = new File(uploadFilePath);
			s3client.putObject(new PutObjectRequest(bucketName, keyName, file));

			logger.info("Upload File - Done!");

		} catch (AmazonServiceException ase) {

			logger.info("Error Message:    " + ase.getMessage());

		} catch (AmazonClientException ace) {

			logger.info("Error Message: " + ace.getMessage());

		}
	}

	@Override
	public void downloadFile(String keyName, AmazonS3 s3client) {

		logger.info("downloadFile()");

		try {

			File file = new File(filePath);
			s3client.getObject(new GetObjectRequest(bucketName, keyName), file);

			logger.info("Download File - Done!");

		} catch (AmazonServiceException ase) {

			logger.info("Error Message:    " + ase.getMessage());

		} catch (AmazonClientException ace) {

			logger.info("Error Message: " + ace.getMessage());

		}
	}
}