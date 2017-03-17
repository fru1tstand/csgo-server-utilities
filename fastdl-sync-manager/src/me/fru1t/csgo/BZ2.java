package me.fru1t.csgo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * Provides a means of concurrently BZip2-ing files and folders.
 */
public class BZ2 {
	private static final int COMPRESSION_WAIT_TIME_PER_FILE_MINUTES = 5;
	
	public static class CompressionFileMap {
		public String source;
		public String destination;
		
		public CompressionFileMap(String source, String destination) {
			this.source = source;
			this.destination = destination;
		}
	}
	
	private static class FinalInteger {
		public int i;
		public FinalInteger() {
			i = 0;
		}
	}
	
	private static final int BUFFER_SIZE = 1000;
	
	/**
	 * Compresses the given inPath file and writes to the outPath. Once complete, this method
	 * will call the callback.
	 * @param inPath
	 * @param outPath
	 * @param callback
	 * @throws IOException 
	 */
	public static void compress(String inPath, String outPath) throws IOException {
		FileInputStream in = null;
		FileOutputStream fout = null;
		BufferedOutputStream out = null;
		BZip2CompressorOutputStream bzOut = null;
		
		try {
			File outFileParentFile = (new File(outPath)).getParentFile();
			if (!outFileParentFile.exists()) {
				outFileParentFile.mkdirs();
			}
			
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
			out.flush();
			fout.flush();
		} finally {
			try { if (bzOut != null) bzOut.close(); } catch (IOException e) { }
			try { if (out != null) out.close(); } catch (IOException e) { }
			try { if (fout != null) fout.close(); } catch (IOException e) { }
			try { if (in != null) in.close(); } catch (IOException e) { }
		}
	}
	
	/**
	 * 
	 * @param fileMaps
	 * @return
	 * @throws InterruptedException 
	 */
	public static boolean[] compressMultiple(Collection<CompressionFileMap> fileMaps, int threads)
			throws InterruptedException {
		boolean[] results = new boolean[fileMaps.size()];
		
		System.out.println("Compressing " + fileMaps.size() + " objects using " + threads
				+ " threads.");
		
		ExecutorService threadPool = Executors.newFixedThreadPool(threads);
		int currentFileMap = 0;
		FinalInteger completedCompressions = new FinalInteger();
		for (CompressionFileMap fm : fileMaps) {
			int thisFileMap = currentFileMap++;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("\tCompressing " + fm.source + " -> " + fm.destination);
						compress(fm.source, fm.destination);
						results[thisFileMap] = true;
					} catch (IOException e) {
						System.out.println("\t" + fm.source
								+ " couldn't be compressed due to the following exception:");
						System.out.println(e);
						results[thisFileMap] = false;
					} finally {
						synchronized (completedCompressions) {
							completedCompressions.i++;
							System.out.println("\tCompleted compressing " + fm.source + " ("
									+ completedCompressions.i + " of "
									+ fileMaps.size() + ")");
						}
					}
				}
			});
		}
		
		// TODO(v2): Track progress through file size and bytes eaten from buffer
		threadPool.shutdown();
		if (!threadPool.awaitTermination(COMPRESSION_WAIT_TIME_PER_FILE_MINUTES * fileMaps.size(),
				TimeUnit.MINUTES)) {
			System.out.println("We timed out before the compressions could finish. Waited "
					+ (COMPRESSION_WAIT_TIME_PER_FILE_MINUTES * fileMaps.size()) + " minutes.");
		}
		return results;
	}
}
