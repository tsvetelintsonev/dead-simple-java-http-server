package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Tsvetelin Tsonev <tsvetelin.tsonev@yahoo.co.uk>
 */
public class FilesUtil {

	public static String getFileContents(String filePath) throws FileNotFoundException, IOException  {
		String line, content;
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while( (line = br.readLine()) != null ) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
		}
		content  = sb.toString();
		return content.substring(0, content.length() - System.lineSeparator().length());
	}
}
