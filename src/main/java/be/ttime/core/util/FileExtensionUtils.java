package be.ttime.core.util;

import java.util.HashMap;

public class FileExtensionUtils {

    public static final String ARCHIVE = "archive.png";
    public static final String CODE = "code.png";
    public static final String CSS = "css.png";
    public static final String DOWNLOAD = "download.png";
    public static final String FILE = "file.png";
    public static final String UNKNOW = "unknow.png";
    public static final String GOOGLE_DRIVE = "gdrive.png";
    public static final String EXECUTABLE = "executable.png";
    public static final String DMG = "dmg.png";
    public static final String HTML5 = "html5.png";
    public static final String IMAGE = "image.png";
    public static final String IMAGES = "images.png";
    public static final String MUSIC = "music.png";
    public static final String PDF = "pdf.png";
    public static final String PRESENTATION = "presentation.png";
    public static final String PSD = "psd.png";
    public static final String SPREADSHEET = "spreadsheet.png";
    public static final String TEXT = "text.png";
    public static final String UPLOAD = "upload.png";
    public static final String VECTOR = "vector.png";
    public static final String VIDEO = "video.png";


    private static HashMap<String, String> imageMapping;

    static {
        imageMapping = new HashMap<>(5);

        imageMapping.put("bmp", IMAGE);
        imageMapping.put("jpg", IMAGE);
        imageMapping.put("jpeg", IMAGE);
        imageMapping.put("ico", IMAGE);
        imageMapping.put("tiff", IMAGE);
        imageMapping.put("tif", IMAGE);
        imageMapping.put("png", IMAGE);
        imageMapping.put("gif", IMAGES);

        imageMapping.put("xls", SPREADSHEET);
        imageMapping.put("xlsx", SPREADSHEET);
        imageMapping.put("ods", SPREADSHEET);
        imageMapping.put("odt", SPREADSHEET);
        imageMapping.put("gsheet", SPREADSHEET);
        imageMapping.put("numbers", SPREADSHEET);

        imageMapping.put("odt", TEXT);
        imageMapping.put("doc", TEXT);
        imageMapping.put("docx", TEXT);
        imageMapping.put("txt", TEXT);
        imageMapping.put("rtf", TEXT);
        imageMapping.put("wpd", TEXT);
        imageMapping.put("pages", TEXT);
        imageMapping.put("gdoc", TEXT);

        imageMapping.put("odp", PRESENTATION);
        imageMapping.put("keynote", PRESENTATION);
        imageMapping.put("key", PRESENTATION);
        imageMapping.put("ppt", PRESENTATION);
        imageMapping.put("pptx", PRESENTATION);
        imageMapping.put("gslide", PRESENTATION);

        imageMapping.put("pdf", PDF);
        imageMapping.put("psd", PSD);

        imageMapping.put("ai", VECTOR);
        imageMapping.put("svg", VECTOR);
        imageMapping.put("dxf", VECTOR);
        imageMapping.put("ai", VECTOR);
        imageMapping.put("sketch", VECTOR);

        imageMapping.put("html", HTML5);

        imageMapping.put("css", CSS);
        imageMapping.put("sass", CSS);
        imageMapping.put("scss", CSS);
        imageMapping.put("less", CSS);

        imageMapping.put("zip", ARCHIVE);
        imageMapping.put("iso", ARCHIVE);
        imageMapping.put("taz", ARCHIVE);
        imageMapping.put("bz2", ARCHIVE);
        imageMapping.put("gz", ARCHIVE);
        imageMapping.put("rar", ARCHIVE);
        imageMapping.put("7z", ARCHIVE);
        imageMapping.put("apk", ARCHIVE);
        imageMapping.put("dmg", ARCHIVE);

        imageMapping.put("js", CODE);
        imageMapping.put("java", CODE);
        imageMapping.put("php", CODE);
        imageMapping.put("xml", CODE);
        imageMapping.put("htm", CODE);
        imageMapping.put("asp", CODE);

        imageMapping.put("mkv", VIDEO);
        imageMapping.put("webm", VIDEO);
        imageMapping.put("flv", VIDEO);
        imageMapping.put("ogg", VIDEO);
        imageMapping.put("htm", VIDEO);
        imageMapping.put("avi", VIDEO);
        imageMapping.put("mov", VIDEO);
        imageMapping.put("mp4", VIDEO);
        imageMapping.put("wmv", VIDEO);
        imageMapping.put("m4v", VIDEO);
        imageMapping.put("3gp", VIDEO);

        imageMapping.put("mkv", MUSIC);
        imageMapping.put("webm", MUSIC);
        imageMapping.put("flv", MUSIC);
        imageMapping.put("ogg", MUSIC);
        imageMapping.put("htm", MUSIC);
        imageMapping.put("avi", MUSIC);
        imageMapping.put("mov", MUSIC);
        imageMapping.put("mp4", MUSIC);

        imageMapping.put("dmg", DMG);

        imageMapping.put("exe", EXECUTABLE);
        imageMapping.put("jar", EXECUTABLE);
        imageMapping.put("pl", EXECUTABLE);
        imageMapping.put("sh", EXECUTABLE);
        imageMapping.put("bin", EXECUTABLE);
        imageMapping.put("bat", EXECUTABLE);
        imageMapping.put("msi", EXECUTABLE);
        imageMapping.put("cmd", EXECUTABLE);
    }

    public static String getFileImage(String ext) {
        return imageMapping.get(ext) == null ? UNKNOW : imageMapping.get(ext);
    }
}
