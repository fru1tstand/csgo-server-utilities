package me.fru1t.csgo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import me.fru1t.csgo.FastDLSyncManagerSettings.UploadSettings.AmazonS3Settings;

public class S3Handler {
	public interface UploadFile {
		public String getFileKey();
		public File getFile();
	}
	
	private final AmazonS3Settings settings;
	private final AmazonS3 s3;
	
	public S3Handler(AmazonS3Settings settings) {
		this.settings = settings;
		
		AWSCredentialsProvider awsCredentials = new AWSCredentialsProvider() {
			private AWSCredentials credentials = null;
			
			@Override
			public AWSCredentials getCredentials() {
				if (credentials == null) {
					refresh();
				}
				return credentials;
			}

			@Override
			public void refresh() {
				credentials = new BasicAWSCredentials(settings.keyId, settings.secretKey);
			}
		};
		
		s3 = AmazonS3ClientBuilder
				.standard()
				.withCredentials(awsCredentials)
				.withRegion(settings.regionName)
				.build();
	}
	
	/**
	 * Returns the set of all files and folders within the bucket in the form of the filepath.
	 * @return
	 */
	public Set<String> listObjects() {
		HashSet<String> result = new HashSet<>();
		int page = 1;
		System.out.println("Loading page " + (page++));
		ObjectListing listing = s3.listObjects(settings.bucketName);
		for (S3ObjectSummary summary : listing.getObjectSummaries()) {
			result.add(summary.getKey());
		}
		
		while (listing.isTruncated()) {
			System.out.println("Loading page " + (page++));
			listing = s3.listNextBatchOfObjects(listing);
			for (S3ObjectSummary summary : listing.getObjectSummaries()) {
				result.add(summary.getKey());
			}
		}
		
		return result;
	}
	
	/**
	 * Batch queues and uploads files to the Amazon S3 bucket.
	 * @param files
	 * @throws InterruptedException 
	 */
	public void putFolder(String folderPath) throws InterruptedException {
		TransferManager tx = TransferManagerBuilder
				.standard()
				.withS3Client(s3)
				.build();
		
		File rootFolder = new File(folderPath);
		if (!rootFolder.exists() || !rootFolder.isDirectory()) {
			System.out.println(folderPath + " isn't a valid folder.");
			return;
		}
		
		// start transfer
		System.out.println("Using folder " + folderPath + " as root.");
		try {
			MultipleFileUpload mfu = tx.uploadDirectory(settings.bucketName, "", rootFolder, true);
			
			// Block until done.
			while (!mfu.isDone()) {
				System.out.println("\tStatus: " + mfu.getState()
					+ "; Progress: " + mfu.getProgress().getPercentTransferred() + "%");
				Thread.sleep(1000);
			}
			
			System.out.println("Transfers complete.");
		} finally {
			tx.shutdownNow();
		}
	}
}
