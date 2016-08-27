import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class test {
    public static void main(String[] args) {
        prepareImageDimensions("/i-data/md0/photo/albums/1956-01-01 TATA, MAMA ALTII 1956-1974/66.jpg");
//        prepareImageDimensions("/home/adr/Pictures/766.jpg");
    }

    private static void prepareImageDimensions(String path) {
        try {
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "identify", "-format", "%[fx:w]x%[fx:h]", path);
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "identify", "-verbose", path);
            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
                    "/home/adr/x.sh", "image-dims", path);
            String dimensions = getProcessOutput(identifyImgDimensions);
            System.out.println("dimensions " + dimensions + " for:\n" + path);
            String[] dims = dimensions.split("x");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getProcessOutput(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        Process p = processBuilder.start();
        p.waitFor();
        InputStream is = p.getInputStream();
        String psOutput = IOUtils.toString(is, "UTF-8");
        IOUtils.closeQuietly(is);
        p.destroy();
        return psOutput;
    }
}