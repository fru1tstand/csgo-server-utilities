package me.fru1t.csgo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;

import com.google.gson.Gson;

import me.fru1t.annotation.Nullable;

/**
 * A simple java object for use with the JSON reader to load in the sync manager settings.
 */
public class FastDLSyncManagerSettings {
	public static final String DEFAULT_SETTINGS_FILE_PATH = "fastdl_sync_manager_settings.json";
	
	/**
	 * Loads in a json file of settings.
	 * @param settingsPath The path for the settings.
	 * @return
	 * @throws FileNotFoundException Thrown if the file couldn't be found given the path.
	 */
	public static FastDLSyncManagerSettings parseFromFile(@Nullable String settingsPath)
			throws FileNotFoundException {
		if (settingsPath == null) {
			settingsPath = DEFAULT_SETTINGS_FILE_PATH;
		}
		
		Gson gson = new Gson();
		FastDLSyncManagerSettings settings =
				gson.fromJson(new FileReader(settingsPath), FastDLSyncManagerSettings.class);
		return settings;
	}
	
	public static class WatchFolderSettings {
		public String folderName;
		public boolean includeSubfolders;
		public HashSet<String> includeExtensions;
		public HashSet<String> ignoreFiles;
	}
	public static class UploadSettings {
		public static class AmazonS3Settings {
			public boolean enable;
			public String keyId;
			public String secretKey;
			public String bucketName;
			public String regionName;
		}
		public static class FtpSettings {
			public boolean enable;
			public String host;
			public int port;
			public String username;
			public String password;
		}
		
		public AmazonS3Settings amazonS3;
		public FtpSettings ftp;
	}
	public String serverCsgoFolder;
	public String tempBzipFolder;
	public int threadsToUseForCompression;
	public UploadSettings upload;
	public WatchFolderSettings[] watchFolders;
}
