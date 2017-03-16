package me.fru1t.csgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Contains data associated to folder syncing and file grabbing. Holds a reference to a directory
 * and filters for files within that directory. 
 */
public class FolderSyncSettings {
	private static final Gson gson = new Gson();
	
	public static class FolderSyncSettingsJsonObject {
		public String localFolder;
		public String mappedFolder;
		public boolean includeSubdirectories;
		public String[] includeExtensions;
	}
	
	public static class FileMapping {
		public File file;
		public String mappedFilePath;
	}
	
	public static List<FolderSyncSettings> loadFolderSyncSettings(String settingsPath)
			throws FileNotFoundException, JsonIOException, JsonSyntaxException {
		FolderSyncSettingsJsonObject[] loadedJsonObjects =
				gson.fromJson(new FileReader(settingsPath), FolderSyncSettingsJsonObject[].class);

		// Convert
		ArrayList<FolderSyncSettings> results = new ArrayList<>();
		for (FolderSyncSettingsJsonObject o : loadedJsonObjects) {
			try {
				results.add(new FolderSyncSettings(o));
			} catch (FileNotFoundException e) {
				System.out.println("[LoadFolderSyncSettings] " + e.getMessage());
			}
		}
		
		return results;
	}
	
	private final File rootFolder;
	private final String mappedFolder;
	private final boolean includeSubdirectories;
	private final HashSet<String> includeExtensions;
	
	public FolderSyncSettings(FolderSyncSettingsJsonObject fssjo) throws FileNotFoundException {
		rootFolder = new File(fssjo.localFolder);
		if (!rootFolder.isDirectory()) {
			throw new FileNotFoundException("No folder with path: \"" + fssjo.localFolder + "\"");
		}
		
		mappedFolder = fssjo.mappedFolder;
		includeSubdirectories = fssjo.includeSubdirectories;
		
		includeExtensions = new HashSet<>();
		for (String s : fssjo.includeExtensions) {
			includeExtensions.add(s);
		}
	}
	
	/**
	 * Returns a set of all files found within the directory and all subdirectories.
	 * @return
	 */
	public HashSet<File> getFiles() {
		return getFilesFromFolder(rootFolder);
	}
	
//	public HashSet<FileMapping> getFileMappings() {
//		TODO: Complete
//	}
	
	public String getMappedPath() {
		return mappedFolder;
	}
	
	private HashSet<File> getFilesFromFolder(File folder) {
		if (!folder.isDirectory()) {
			return null;
		}
		
		HashSet<File> result = new HashSet<>();
		for (File f : folder.listFiles()) {
			// Ignore this folder and previous folder
			if (f.getName().equals(".") || f.getName().equals("..")) continue;
			
			// Append subfolders recursively if enabled
			if (f.isDirectory()) {
				if (includeSubdirectories) {
					result.addAll(getFilesFromFolder(f));
				}
				continue;
			}
			
			// Append file
			String ext = getExtension(f);
			if (includeExtensions.contains(ext)) {
				result.add(f);
			}
		}
		
		return result;
	}
	
	
	private static String getExtension(File f) {
		return f.getName().substring(f.getName().lastIndexOf('.') + 1);
	}
}
