package me.fru1t.csgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Boot {
	private static final String[] DEFAULT_MAPS = {
			"cs_italy", "cs_office", "de_aztec", "de_dust", "de_dust2", "de_inferno", "de_nuke",
			"de_shorttrain", "ar_baggage", "ar_shoots", "de_bank", "de_lake", "de_safehouse",
			"de_sugarcane", "de_stmarc", "de_train", "training1", "ar_monastery", "cs_assault",
			"cs_militia", "de_cache", "de_cbble", "de_mirage", "de_overpass", "de_shortdust",
			"de_vertigo"
	};
	
	private static final String[] MAP_EXTENSIONS = { "bsp", "nav" };
	private static final String[] DECAL_EXTENSIONS = { "vtf", "vmt" };
	private static final String[] MODEL_EXTENSIONS = { "mdl" };
	
	
	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<FolderSyncSettings> allFss = FolderSyncSettings.loadFolderSyncSettings("folder_sync_settings.json");
		for (FolderSyncSettings fss : allFss) {
			for (File f : fss.getFiles()) {
				System.out.println(f.getName());
			}
		}
	}
}
