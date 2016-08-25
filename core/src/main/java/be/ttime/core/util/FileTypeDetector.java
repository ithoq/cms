package be.ttime.core.util;

/**
 * Detects the mime type of files (ideally based on marker in file content)

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
 */