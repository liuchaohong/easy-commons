package com.github.easy.commons.util.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 把文件压缩成tar.gz文件
 * @author LIUCHAOHONG
 *
 */
public class TarGzipUtil {

	private static final Log logger = LogFactory.getLog(TarGzipUtil.class);

	/**
	 * Compress (tar.gz) the input file (or directory) to the output file
	 * <p/>
	 *
	 * In the case of a directory all files within the directory (and all nested
	 * directories) will be added to the archive
	 *
	 * @param file
	 *            The file(s if a directory) to compress
	 * @param output
	 *            The resulting output file (should end in .tar.gz)
	 * @throws IOException
	 */
	public static void compressFile(File file, File output,FileFilter filter) throws IOException {
		ArrayList<File> list = new ArrayList<File>(1);
		list.add(file);
		compressFiles(list, output, filter);
	}

	/**
	 * Compress (tar.gz) the input files to the output file
	 *
	 * @param files
	 *            The files to compress
	 * @param output
	 *            The resulting output file (should end in .tar.gz)
	 * @throws IOException
	 */
	public static void compressFiles(Collection<File> files, File output,FileFilter filter)
			throws IOException {
		logger.debug("Compressing " + files.size() + " to "
				+ output.getAbsoluteFile());
		// Create the output stream for the output file
		FileOutputStream fos = new FileOutputStream(output);
		// Wrap the output file stream in streams that will tar and gzip
		// everything
		TarArchiveOutputStream taos = new TarArchiveOutputStream(
				new GZIPOutputStream(new BufferedOutputStream(fos)));
		// TAR has an 8 gig file limit by default, this gets around that
		taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR); // to get
																		// past
																		// the 8
																		// gig
																		// limit
		// TAR originally didn't support long file names, so enable the support
		// for it
		taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

		// Get to putting all the files in the compressed output file
		for (File f : files) {
			addFilesToCompression(taos, f, "",filter);
		}

		// Close everything up
		taos.close();
		fos.close();
	}

	/**
	 * Does the work of compression and going recursive for nested directories
	 * <p/>
	 *
	 * Borrowed heavily from http://www.thoughtspark.org/node/53
	 *
	 * @param taos
	 *            The archive
	 * @param file
	 *            The file to add to the archive
	 * @param dir
	 *            The directory that should serve as the parent directory in the
	 *            archivew
	 * @throws IOException
	 */
	private static void addFilesToCompression(TarArchiveOutputStream taos,
			File file, String dir,FileFilter filter) throws IOException {
		if(filter!=null){
			if(!filter.accept(file)){
				return ;
			}
		}
		String archName=file.getName();
		if(StringUtils.isNotBlank(dir)){
			archName=dir + File.separator + file.getName();
		}
		taos.putArchiveEntry(new TarArchiveEntry(file, archName));
		logger.debug(file.getAbsolutePath()+" compress to "+archName);
		if (file.isFile()) {
			// Add the file to the archive
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			IOUtils.copy(bis, taos);
			taos.closeArchiveEntry();
			bis.close();
		} else if (file.isDirectory()) {
			// close the archive entry
			taos.closeArchiveEntry();
			// go through all the files in the directory and using recursion,
			// add them to the archive
			for (File childFile : file.listFiles()) {
				addFilesToCompression(taos, childFile, archName,filter);
			}
		}
	}

}
