package me.fru1t.csgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.fru1t.csgo.BZ2.CompressionFileMap;
import me.fru1t.csgo.FastDLSyncManagerSettings.WatchFolderSettings;
import me.fru1t.csgo.WatchFolderProcessor.FileMapping;

public class FastDLSyncManager {
	private static final String BZIP_EXTENSION = ".bz2";
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		if (args.length == 0) {
			File settingsFile = new File(FastDLSyncManagerSettings.DEFAULT_SETTINGS_FILE_PATH);
			if (!settingsFile.exists()) {
				System.out.println("I couldn't find a settings file to use. Please visit "
						+ "https://github.com/fru1tstand/csgo-server-utilities to learn more.");
				return;
			}
			run(FastDLSyncManagerSettings.DEFAULT_SETTINGS_FILE_PATH);
		} else {
			for (String s : args) {
				run(s);
			}
		}
	}
	
	public static Set<FileMapping> loadLocalFiles(FastDLSyncManagerSettings fdlmSettings) {
		HashSet<FileMapping> result = new HashSet<>();
		int folderSkips = 0;
		for (WatchFolderSettings wfSettings : fdlmSettings.watchFolders) {
			try {
				WatchFolderProcessor processor =
						new WatchFolderProcessor(fdlmSettings.serverCsgoFolder, wfSettings);
				result.addAll(processor.getFileMappings());
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage() + "; Skipping folder.");
				folderSkips++;
			}
		}
		System.out.println("Loaded " + result.size() + " local files. Skipped "
				+ folderSkips + " folders.");
		return result;
	}
	
	public static void run(String settingFilePath) throws FileNotFoundException, InterruptedException {
		// Load local server files
		FastDLSyncManagerSettings settings =
				FastDLSyncManagerSettings.parseFromFile(settingFilePath);
		Set<FileMapping> fileMappings = loadLocalFiles(settings);
		
		// Amazon S3 service
		if (settings.upload.amazonS3.enable) {
			System.out.println();
			System.out.println("Amazon S3 service enabled...");
			
			// Load S3 Files
			System.out.println("Logging into Amazon AWS");
			S3Handler s3Handler = new S3Handler(settings.upload.amazonS3);
			System.out.println("Loading objects from " + settings.upload.amazonS3.bucketName);
			Set<String> s3Objects = s3Handler.listObjects();
			System.out.println("Successfully loaded " + s3Objects.size() + " objects.");
			
			// Diff
			List<FileMapping> filesToUpload = new ArrayList<>();
			for (FileMapping fm : fileMappings) {
				if (!s3Objects.contains(fm.nameAndPathFromFastDLRoot + BZIP_EXTENSION)) {
					filesToUpload.add(fm);
				}
			}
			
			System.out.println("Found " + filesToUpload.size() + " files not in the bucket:");
			for (FileMapping fm : filesToUpload) {
				System.out.println("\t" + fm.nameAndPathFromFastDLRoot);
			}
			
			if (filesToUpload.size() == 0) {
				System.out.println();
				System.out.println("The Amazon S3 bucket seems to be up-to-date! Exiting.");
				return;
			}
			
			// Temp folder
			System.out.println();
			System.out.println("Starting file compression.");
			System.out.println("Creating temporary folder...");
			File tempFolder = new File(settings.tempBzipFolder);
			if (tempFolder.exists()) {
				System.out.println("The temporary folder " + settings.tempBzipFolder + " already "
						+ "exists. Please delete it or specify a different, nonexistent folder.");
				return;
			}
			tempFolder.mkdirs();
			System.out.println("Path " + tempFolder.getAbsolutePath() + " created.");
			
			// Bz2
			System.out.println("Compressing files...");
			Set<CompressionFileMap> filesToUploadMap = new HashSet<>();
			for (FileMapping fm : filesToUpload) {
				filesToUploadMap.add(new CompressionFileMap(fm.file.getAbsolutePath(),
						settings.tempBzipFolder + "/" + fm.nameAndPathFromFastDLRoot
						+ BZIP_EXTENSION));
			}
			boolean[] compressionResults =
					BZ2.compressMultiple(filesToUploadMap, settings.threadsToUseForCompression);
			System.out.println("Finished Bz2 compression.");
			
			// Bz2 Check
			System.out.println("Verifying compressed files...");
			int validFiles = 0;
			for (int i = 0; i < compressionResults.length; i++) {
				if (compressionResults[i]) {
					File bzipCheckFile = new File(settings.tempBzipFolder + "/"
							+ filesToUpload.get(i).nameAndPathFromFastDLRoot + BZIP_EXTENSION);
					if (bzipCheckFile.exists()) {
						validFiles++;
					}
				}
			}
			if (validFiles != filesToUpload.size()) {
				System.out.println("*** WARNING ***");
				System.out.println("It seems as though there was an error compressing "
						+ (filesToUpload.size() - validFiles)
						+ " files. These will be ignored, but may indicate a larger problem.");
			} else {
				System.out.println("Everything looks good from here.");
			}
			System.out.println("Verification complete!");
			
			// Upload
			System.out.println();
			System.out.println("Starting upload to Amazon S3...");
			s3Handler.putFolder(settings.tempBzipFolder);
			System.out.println("Finished uploading to Amazon S3!");
			
			// Clean up
			System.out.println();
			System.out.println("Deleting the temp directory...");
			deleteFolder(tempFolder);
			System.out.println("Completed my cleanup duties!");
			
			System.out.println();
			System.out.println("Seems like my job is done. Have a good day :)");
		}
		
		// TODO(v2): If there's interest, implement FTP & sFTP.
	}
	
	private static void deleteFolder(File folder) {
		// Delete files within the folder.
		for (File f : folder.listFiles()) {
			if (f.getName().equals(".") || f.getName().equals("..")) {
				continue;
			}
			
			// Recursively delete subfolders.
			if (f.isDirectory()) {
				deleteFolder(f);
				continue;
			}
			
			System.out.println("\t"
					+ ((f.delete()) ? "Deleted " : "Couldn't delete ") + f.getAbsolutePath());
		}
		// Finally, delete the root folder.
		System.out.println("\t"
				+ ((folder.delete()) ? "Deleted " : "Couldn't delete ") + folder.getAbsolutePath());
	}
}
