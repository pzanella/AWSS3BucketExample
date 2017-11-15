package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.example.services.BucketServices;
import com.example.services.BucketServicesImpl;
import com.example.utilities.Utilities;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BucketServicesImplTest {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(BucketServicesImpl.class);

	@Autowired
	BucketServices bucketServices;

	@Value("${cloud.aws.s3.key}")
	private String keyName;

	@Value("${cloud.aws.s3.uploadfile}")
	private String uploadFilePath;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${local.downloadFile}")
	private String filePath;

	private static File file;

	public Utilities utilities;
	public static AmazonS3 s3client;

	private static String staticBucketName;

	@Before
	public void init() {
		utilities = new Utilities();
		s3client = utilities.startConnection();

		staticBucketName = bucketName;

		logger.info("Create bucket in S3 mock.");
		s3client.createBucket(staticBucketName);

		assertTrue(s3client.doesBucketExist(bucketName));
	}

	@Test
	public void uploadFileBucketTest() throws InterruptedException {
		logger.info("Upload file in S3Mock.");
		bucketServices.uploadFile(keyName, uploadFilePath, s3client);

		assertThat(s3client.doesObjectExist(bucketName, keyName));
	}

	@Test
	public void downloadFileTest() {
		logger.info("Start downloading file from S3Mock.");
		bucketServices.downloadFile(keyName, s3client);

		file = new File(filePath);
		logger.info("ABSOLUTE PATH => " + file.getAbsolutePath());
		assertTrue(file.exists());
		assertTrue(file.isFile());
	}

	@After
	public void closeConnection() {
		logger.info("Close connection to S3Mock.");
		utilities.closeConnection();
	}

	@AfterClass
	public static void tearDown() {
		logger.info("Removing objects from bucket");

		try {

			ObjectListing object_listing = s3client.listObjects(staticBucketName);

			while (true) {
				for (Iterator<?> iterator = object_listing.getObjectSummaries().iterator(); iterator.hasNext();) {
					S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
					s3client.deleteObject(staticBucketName, summary.getKey());
				}

				if (object_listing.isTruncated()) {
					object_listing = s3client.listNextBatchOfObjects(object_listing);
				} else {
					break;
				}
			}

			logger.info("Removing versions from bucket");
			VersionListing version_listing = s3client
					.listVersions(new ListVersionsRequest().withBucketName(staticBucketName));
			while (true) {
				for (Iterator<?> iterator = version_listing.getVersionSummaries().iterator(); iterator.hasNext();) {
					S3VersionSummary vs = (S3VersionSummary) iterator.next();
					s3client.deleteVersion(staticBucketName, vs.getKey(), vs.getVersionId());
				}

				if (version_listing.isTruncated()) {
					version_listing = s3client.listNextBatchOfVersions(version_listing);
				} else {
					break;
				}
			}

			s3client.deleteBucket(staticBucketName);

			logger.info("Bucket and files are delete.");

		} catch (AmazonServiceException e) {
			logger.error(e.getMessage());
		}
		
		file.delete();
	}
}
