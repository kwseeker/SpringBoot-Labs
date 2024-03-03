package top.kwseeker.labs;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ComponentsTest {

    @Test
    public void testLoadResourcesFile() {
        String s = readResourceFile("leave.json");
        System.out.println(s);
    }

    /**
     * svg xml 文件转 png
     */
    @Test
    public void testTranscoder() throws TranscoderException, IOException {
        String s = readResourceFile("svg.xml");
        InputStream svgStream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        TranscoderInput input = new TranscoderInput(svgStream);
        OutputStream os = Files.newOutputStream(Paths.get("out.png"));
        TranscoderOutput output = new TranscoderOutput(os);
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.transcode(input, output);
        os.flush();
        os.close();
    }

    private String readResourceFile(String file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return contentBuilder.toString();
    }
}
