package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.example.services.BucketServices;
import com.example.services.BucketServicesImpl;

import io.findify.s3mock.S3Mock;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class BucketServicesImplTest {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(BucketServicesImpl.class);
	public S3Mock s3Mock;
	private File file;

	@Value("${local.upload.filepath}")
	private String uploadFilePath;

	@Value("${aws.bucket.name}")
	private String bucketName;

	@Value("${local.download.filepath}")
	private String downloadFilePath;

	@Value("${aws.bucket.keyname}")
	private String keyName;

	@Autowired
	BucketServices bucketServices;

	@Autowired
	public AmazonS3 s3client;

	@Before
	public void init() {
		this.s3Mock = S3Mock.create(8001, "/tmp/s3");
		this.s3Mock.start();

		logger.info("Create bucket in S3 mock.");
		s3client.createBucket(bucketName);

		assertTrue(s3client.doesBucketExistV2(bucketName));
	}

	@Test
	public void uploadAndDownloadAndDeleteFileTest() {
		logger.info("Upload file in S3Mock.");
		bucketServices.uploadFile(keyName, uploadFilePath, s3client);

		assertThat(s3client.doesObjectExist(bucketName, keyName));

		logger.info("Start downloading file from S3Mock.");
		bucketServices.downloadFile(keyName, s3client);

		file = new File(downloadFilePath);

		assertTrue(file.exists());
		assertTrue(file.isFile());
		
		logger.info("Delete bucket file.");
		bucketServices.deleteFiles(keyName, s3client);
		assertThat(!s3client.doesObjectExist(bucketName, keyName));
		
		logger.info("Delete local file.");
		file.delete();
	}

	@After
	public void tearDown() {
		logger.info("Delete bucket.");

		DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucketName);
		s3client.deleteBucket(deleteBucketRequest);

		logger.info("Close connection to S3Mock.");
		this.s3Mock.stop();
	}
}
