package me.fru1t.csgo;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * Provides a means of concurrently BZip2-ing files and folders.
 */
public class BZ2 {
	private static final int BUFFER_SIZE = 1000;
	
	/**
	 * Compresses the given inPath file and writes to the outPath. Once complete, this method
	 * will call the callback.
	 * @param inPath
	 * @param outPath
	 * @param callback
	 */
	public static boolean compress(String inPath, String outPath) {
			FileInputStream in = null;
			FileOutputStream fout = null;
			BufferedOutputStream out = null;
			BZip2CompressorOutputStream bzOut = null;
			
			try {
				in = new FileInputStream(inPath);
				fout = new FileOutputStream(outPath);
				out = new BufferedOutputStream(fout);
				bzOut = new BZip2CompressorOutputStream(out);
				
				final byte[] buffer = new byte[BUFFER_SIZE];
				int n = 0;
				while ((n = in.read(buffer)) != -1) {
					bzOut.write(buffer, 0, n);
				}
				bzOut.flush();
			} catch (IOException e) {
				return false;
			} finally {
				try { if (in != null) in.close(); } catch (IOException e) { }
				try { if (fout != null) fout.close(); } catch (IOException e) { }
				try { if (out != null) out.close(); } catch (IOException e) { }
				try { if (bzOut != null) bzOut.close(); } catch (IOException e) { }
			}
			return true;
	}
}
