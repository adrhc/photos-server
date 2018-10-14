package image.exifweb.util.io;

import image.photos.config.AppConfigService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian.petre on 18-06-2014.
 */
@Service
@Scope("prototype")
public class EndingLinesFileReader {
	private static final Logger logger = LoggerFactory.getLogger(EndingLinesFileReader.class);
	private static final int CHUNK_SIZE = 256;
	@Value("${movie.name.prefix}")
	private String movieNamePrefix;
	@Inject
	private AppConfigService appConfigService;
	private String runningMessage;
	private String videoFolder;
	private String path;
	private int linesToRead = 0;

	@PostConstruct
	public void postConstruct() {
		this.videoFolder = this.appConfigService.getConfig("video root folder");
		this.linesToRead = this.appConfigService.getConfigInteger("subtitles-extractor-lines");
		this.path = getLastModifiedPath(this.appConfigService.getConfig("subtitles-extractor.log"));
	}

	private String getLastModifiedPath(String path) {
		String[] paths = path.split(",");
		File tmpf, file = null;
		String fpath = null;
		for (String p : paths) {
			if (!(tmpf = new File(p)).exists()) {
				logger.debug("{} does not exists!", p);
				continue;
			}
			if (file == null) {
				file = tmpf;
				fpath = p;
			} else if (tmpf.lastModified() > file.lastModified()) {
				file = tmpf;
				fpath = p;
			}
		}
		return fpath;
	}

	public List<String> getLines() throws IOException {
		List<String> lines = new ArrayList<>(this.linesToRead);
		if (this.path == null) {
			lines.add("Nu exista fisiere de log!");
			lines.add(DateFormatUtils.format(System.currentTimeMillis(), "dd-MM-yyyy HH:mm:ss"));
			return lines;
		}
		lines.add("Using " + this.path);// linia 1 la pozitia 0
		lines.add(this.runningMessage);// linia 2 la pozitia 1
		lines.add("");// linia 3 la pozitia 2
		RandomAccessFile fis = new RandomAccessFile(this.path, "r");
		long size = fis.length();
		if (size == 0) {
			lines.add(this.path + " este gol!");
			lines.add(DateFormatUtils.format(System.currentTimeMillis(), "dd-MM-yyyy HH:mm:ss"));
			return lines;
		}
		this.linesToRead -= 4;// liniile ramase disponibile de a se mai adauga (fara cele de mai jos comentate cu "linia x")
		long position1 = Math.max(size - CHUNK_SIZE, 0), position2 = size;
		byte[] bytes;
		int bcount;
		String str, movieFolder = null, movieName = null;
		String[] slines;
		boolean doneWithLines = false, doneWithMovieInfo = false, firstRead = true;
		while (position1 >= 0) {
			fis.seek(position1);
			bytes = new byte[(int) (position2 - position1)];
			bcount = fis.read(bytes);
			str = new String(bytes, 0, bcount, Charset.forName("UTF-8"));
			slines = str.split("\n");
			if (firstRead) {
				firstRead = false;
				// ultima linie din subtitles-extractor.log
				doneWithMovieInfo = !slines[slines.length - 1].endsWith("%");
			}
			// linia 0 s-ar putea sa fie trunchiata asa ca daca
			// nu e prima linie din fisier atunci nu o procesez
			for (int i = slines.length - 1; i >= (position1 > 0 ? 1 : 0); i--) {
				if (!doneWithMovieInfo) {
					if (movieName == null) {
						// primul se va gasi/procesa movieName
						movieName = getExtractedMovieName(slines[i]);
					} else if (movieFolder == null) {
						// movieFolder se va gasi/procesa dupa movieName
						movieFolder = getExtractedMovieFolder(slines[i]);
					}
					doneWithMovieInfo = (movieName != null && movieFolder != null);
				}
				if (!doneWithLines) {
					lines.add(3, slines[i]);// 3 linii adaugate mai sus pana acum
					doneWithLines = this.linesToRead == lines.size();
				} else if (doneWithMovieInfo) {
					break;
				}
			}
			if (position1 == 0 || doneWithLines && doneWithMovieInfo) {
				break;
			}
			// largim intervalul ca sa utilizam si prima linie, cea posibil trunchiata
			position2 = Math.max(position1 + slines[0].length(), 0);
			position1 = Math.max(position1 - CHUNK_SIZE, 0);
		}
		fis.close();
		lines.add("");// linia 4 la pozitia >= 3
		if (movieFolder != null) {
			lines.add(movieFolder);// linia 5 la pozitia >= 3
		}
		if (movieName != null) {
			lines.add(movieName);// linia 6 la pozitia >= 3
		}
		lines.add(DateFormatUtils.format(System.currentTimeMillis(), "dd-MM-yyyy HH:mm:ss"));// linia 7 la pozitia >= 3
		return lines;
	}

	private String getExtractedMovieFolder(String line) {
		int idx = line.indexOf(this.videoFolder);
		if (idx < 0) {
			return null;
		}
		return line.substring(idx);
	}

	private String getExtractedMovieName(String line) {
		int idx = line.indexOf(this.movieNamePrefix);
		if (idx < 0) {
			return null;
		}
		return line.substring(idx + this.movieNamePrefix.length()).trim();
	}

	public void setRunningMessage(String runningMessage) {
		this.runningMessage = runningMessage;
	}
}
