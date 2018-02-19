package image.exifweb.picture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IdentifyPictureTest {
	public static void main(String[] args) {
		System.out.println("PATH=" + System.getenv().get("PATH"));
		try {
			Process exec = Runtime.getRuntime().exec("identify /home/adr/Pictures/766.jpg");
			InputStream is = exec.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
} 
