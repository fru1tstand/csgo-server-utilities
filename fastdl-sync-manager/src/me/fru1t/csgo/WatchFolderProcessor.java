package me.fru1t.csgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.fru1t.csgo.FastDLSyncManagerSettings.WatchFolderSettings;
import me.fru1t.csgo.S3Handler.UploadFile;

/**
 * Contains data associated to folder syncing and file grabbing. Holds a reference to a directory
 * and filters for files within that directory. 
 */
public class WatchFolderProcessor {
	public static class FileMapping implements UploadFile {
		public File file;
		public String nameAndPathFromFastDLRoot;
		
		public FileMapping(File file, String nameAndPathFromFastDLRoot) {
			this.file = file;
			this.nameAndPathFromFastDLRoot = nameAndPathFromFastDLRoot;
		}

		@Override
		public String toString() {
			return "FileMapping [file=" + file + ", nameAndPathFromFastDLRoot="
					+ nameAndPathFromFastDLRoot + "]";
		}

		@Override
		public String getFileKey() {
			return nameAndPathFromFastDLRoot;
		}

		@Override
		public File getFile() {
			return file;
		}
	}
	
	private final File watchFolder;
	private final WatchFolderSettings settings;
	
	public WatchFolderProcessor(String csgoDir, WatchFolderSettings settings)
			throws FileNotFoundException {
		this.settings = settings;
		
		watchFolder = new File(csgoDir + "/" + settings.folderName);
		if (!watchFolder.isDirectory()) {
			throw new FileNotFoundException("Folder " + csgoDir + "/" + settings.folderName
					+ " doesn't exist.");
		}
	}
	
	/**
	 * Returns a list of all files within the watch folder adhering to the filters.
	 * @return
	 */
	public List<FileMapping> getFileMappings() {
		return getFileMappings(watchFolder, settings.folderName);
	}
	
	private List<FileMapping> getFileMappings(File currentFolder, String pathFromCsgoFolder) {
		if (!currentFolder.isDirectory()) {
			return new ArrayList<>();
		}
		
		File[] contents = currentFolder.listFiles();
		ArrayList<FileMapping> results = new ArrayList<>();
		for (File f : contents) {
			// Ignore self and parent
			if (f.getName().equals(".") || f.getName().equals("..")) {
				continue;
			}
			
			// FastDL root starts at the server CSGO folder
			String nameAndPathFromFastDLRoot = pathFromCsgoFolder + "/" + f.getName();
			
			// Recursive searching
			if (f.isDirectory()) {
				if (settings.includeSubfolders) {
					results.addAll(getFileMappings(f, nameAndPathFromFastDLRoot));
				}
				continue;
			}
			
			// Otherwise it's just a file.
			if (settings.includeExtensions.contains(getExtension(f))
					&& !settings.ignoreFiles.contains(nameAndPathFromFastDLRoot)) {
				results.add(new FileMapping(f, nameAndPathFromFastDLRoot));
			}
		}
		
		return results;
	}
	
	
	private static String getExtension(File f) {
		return f.getName().substring(f.getName().lastIndexOf('.') + 1);
	}
}
