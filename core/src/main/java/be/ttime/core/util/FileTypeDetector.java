package be.ttime.core.util;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Detects the mime type of files (ideally based on marker in file content)
 */
@Component
public class FileTypeDetector extends java.nio.file.spi.FileTypeDetector {

    private final Tika tika = new Tika();

    @Override
    public String probeContentType(Path path) throws IOException {
        return tika.detect(path.toFile());
    }

    public String probeContentType(byte[] file, String filename) throws IOException {
        TikaConfig config = TikaConfig.getDefaultConfig();
        Detector detector = config.getDetector();

        TikaInputStream stream = TikaInputStream.get(file);

        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, filename);
        MediaType mediaType = detector.detect(stream, metadata);
        return mediaType.toString();

    }

    public String probeContentType(byte[] file) {
        return tika.detect(file);
    }
}