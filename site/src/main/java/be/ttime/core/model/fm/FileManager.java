package be.ttime.core.model.fm;

import com.github.slugify.Slugify;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.unbescape.html.HtmlEscape;
import org.unbescape.html.HtmlEscapeLevel;
import org.unbescape.html.HtmlEscapeType;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {

    private final static String CONFIG_PROPERTIES = "filemanager.properties";

    private ServletContext sc;
    private Properties config = new Properties();
    private String fileManagerRoot;
    private String publicFileManagerRoot;
    private String connectorUrl;
    private String documentRoot;
    private Locale locale;
    private JSONObject translation;
    private DateFormat dt;

    private static Logger logger = Logger.getLogger("FileManager");

    public FileManager(ServletContext servletContext, Locale locale) throws IOException {
        this.sc = servletContext;
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_PROPERTIES));
        //String test1 = sc.getRealPath("/resources/admin/libs/RichFilemanager-master");
        fileManagerRoot = sc.getRealPath("/resources/admin/libs/RichFilemanager-master");
        publicFileManagerRoot = "/resources/admin/libs/RichFilemanager-master";
        connectorUrl = "/admin/fileManager/api";
        this.locale = locale;
        loadLanguageFile();
        dt = new SimpleDateFormat(config.getProperty("date"));
        documentRoot = config.getProperty("doc_root");
    }

    private void loadLanguageFile() {

        BufferedReader br = null;
        InputStreamReader isr = null;
        String text;
        StringBuffer contents = new StringBuffer();
        File configFile = new File(this.fileManagerRoot + "/scripts/languages/" + locale.getLanguage() + ".json");
        if (!configFile.exists()) {
            configFile = new File(this.fileManagerRoot + "/scripts/languages/en.json");
        }

        try {
            isr = new InputStreamReader(new FileInputStream(configFile), "UTF-8");
            br = new BufferedReader(isr);
            while ((text = br.readLine()) != null)
                contents.append(text);
            translation = new JSONObject(contents.toString());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fatal error: language file not found", e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception e2) {
            }
            try {
                if (isr != null)
                    isr.close();
            } catch (Exception e2) {
            }
        }
    }

    private void generateResponse(HttpServletResponse response, String json) throws IOException {
        response.setStatus(200);
        response.addHeader("Content-Type", "application/json; charset=utf-8");
        response.getWriter().write(json);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String method = request.getMethod();
        final String mode = request.getParameter("mode");
        JSONObject responseData = null;
        response.setStatus(200);

        if (StringUtils.isEmpty("mode")) {
            generateResponse(response, errorResponse(translation.getString("MODE_ERROR")).toString());
            return;
        }

        try {
            if (method.equals("GET")) {
                switch (mode) {
                    default:
                        responseData = errorResponse(translation.getString("MODE_ERROR"));
                        break;
                    case "getinfo":
                        if (!StringUtils.isEmpty(request.getParameter("path"))) {
                            responseData = this.getInfo(request);
                        }
                        break;
                    case "getfolder":
                        if (!StringUtils.isEmpty(request.getParameter("path"))) {
                            responseData = this.getFolder(request);
                        }
                        break;
                    case "download":
                        if (!StringUtils.isEmpty(request.getParameter("path"))) {
                            Boolean force = Boolean.parseBoolean(request.getParameter("force"));
                            responseData = download(force, request, response);
                        }
                        break;
                    case "addfolder":
                        if (!StringUtils.isEmpty(request.getParameter("path"))  &&
                                !StringUtils.isEmpty(request.getParameter("name"))) {
                            responseData = addFolder(request);
                        }
                        break;
                    case "delete":
                        if (!StringUtils.isEmpty(request.getParameter("path"))) {
                            responseData = delete(request);
                        }
                        break;
                    case "rename":
                        if ( !StringUtils.isEmpty(request.getParameter("old")) && !StringUtils.isEmpty(request.getParameter("new"))) {
                            responseData = rename(request);
                        }
                        break;
                    case "move":
                        if ( !StringUtils.isEmpty(request.getParameter("old")) && !StringUtils.isEmpty(request.getParameter("new"))) {
                            responseData = move(request);
                        }
                        break;
                    case "getimage":
                        if (!StringUtils.isEmpty(request.getParameter("path"))) {
                            Boolean thumbnail = Boolean.parseBoolean(request.getParameter("thumbnail"));
                            responseData = getImage(thumbnail, request, response);
                        }
                    break;
                    case "editfile":
                        if (!StringUtils.isEmpty(request.getParameter("path"))) {
                            responseData = editFile(request);
                        }
                        break;
                    case "summarize":
                        responseData = getDirSummary();
                        break;
                }
            } else if(method.equals("POST")) {
                switch (mode) {

                    default:
                        responseData = errorResponse(translation.getString("MODE_ERROR"));
                        break;
                    case "add":
                        if(!StringUtils.isEmpty(request.getParameter("currentpath"))) {
                            responseData = add(request);
                        }
                        break;
                    case "replace":
                        if(!StringUtils.isEmpty(request.getParameter("newfilepath"))) {
                            responseData = replace(request);
                        }
                        break;
                    case "savefile" :
                        if(!StringUtils.isEmpty(request.getParameter("path"))) {
                            responseData = save(request);
                        }
                        break;

                }
            }
        }

     catch (IOException e1) {
            generateResponse(response, errorResponse(translation.getString("ERROR_SERVER")).toString());
        } catch (ServletException e2){
            generateResponse(response, errorResponse(translation.getString("ERROR_SERVER")).toString());
        }

        if (responseData != null) {
            generateResponse(response, responseData.toString());
        }
    }

    private JSONObject getDirSummary() throws IOException {

        return FileManagerHelper.getDirSummary(getFile("/").toPath());
    }

    private JSONObject save(HttpServletRequest request) throws IOException {
        String path = request.getParameter("path");
        String content = request.getParameter("content");
        File file = getFile(path);

        if(!file.canWrite()){
            return errorResponse(translation.getString("NOT_ALLOWED_SYSTEM"));
        }
        FileOutputStream oldFile = new FileOutputStream(file, false);

        String unescapedContent = HtmlEscape.unescapeHtml(content);

        oldFile.write(unescapedContent.getBytes());
        oldFile.close();

        JSONObject result = new JSONObject(FileManagerHelper.getDefaultItem());
        result.put("Path", path);
        return result;
    }

    private JSONObject editFile(HttpServletRequest request) throws IOException {

        String path = request.getParameter("path");
        File file = getFile(path);

        if(!file.canWrite()){
            return errorResponse(translation.getString("NOT_ALLOWED_SYSTEM"));
        }

        // to do check editable extension
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));
            for(String line; (line = br.readLine()) != null; ) {
                result.append(line);
                result.append('\n');
            }
        } catch (IOException e){
            return errorResponse(translation.getString("ERROR_SERVER"));
        } finally {
            if(br!= null){
                br.close();
            }
        }
        String escapedString = HtmlEscape.escapeHtml(result.toString(), HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);

        JSONObject object = new JSONObject(FileManagerHelper.getDefaultItem());
        object.put("Content", escapedString);

        object.put("Path", path);

        return object;
    }

    private JSONObject replace(HttpServletRequest request) throws IOException, ServletException {

        String path = request.getParameter("newfilepath");
        File file = getFile(path);

        Part uploadedFile = request.getPart("files");
        if(null == uploadedFile || uploadedFile.getSize() == 0)
            return errorResponse(translation.getString("INVALID_FILE_UPLOAD"));

        try {
            Files.copy(new BufferedInputStream(uploadedFile.getInputStream()), Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e){
            return errorResponse(translation.getString("INVALID_FILE_UPLOAD"));
        }

        JSONObject result = new JSONObject();
        result.put("Error", "");
        result.put("Code", 0);
        JSONArray array = new JSONArray();

        Map fileInfo = new HashMap();
        fileInfo.put("name", FilenameUtils.getBaseName(file.getName()));
        fileInfo.put("type", FileManagerHelper.mimetypes.get(FilenameUtils.getExtension(file.getName())));
        fileInfo.put("size", uploadedFile.getSize());
        fileInfo.put("url", "/../../../public" + path + file.getName());

        array.put(fileInfo);
        result.put("files", array);
        return result;
    }

    public JSONObject add(HttpServletRequest request) throws IOException, ServletException {

        Slugify slugify = new Slugify();

        String path = request.getParameter("currentpath");
        File file = getFile(path);

        Part uploadedFile = request.getPart("files");
        if(null == uploadedFile || uploadedFile.getSize() == 0)
            return errorResponse(translation.getString("INVALID_FILE_UPLOAD"));

        String submittedFileName = uploadedFile.getSubmittedFileName();
        String name = slugify.slugify(FilenameUtils.getBaseName(submittedFileName));
        String ext = FilenameUtils.getExtension(submittedFileName);
        String filename = name + '.' + ext;

        String uploadedPath = file.getAbsolutePath() + "/" +  filename;

        try {
            Files.copy(new BufferedInputStream(uploadedFile.getInputStream()), Paths.get(uploadedPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e){
            return errorResponse(translation.getString("INVALID_FILE_UPLOAD"));
        }

        JSONObject result = new JSONObject();
        result.put("Error", "");
        result.put("Code", 0);
        JSONArray array = new JSONArray();

        Map fileInfo = new HashMap();
        fileInfo.put("name", filename);
        fileInfo.put("type", FileManagerHelper.mimetypes.get(ext));
        fileInfo.put("size", uploadedFile.getSize());
        fileInfo.put("url", "/../../../public" + path + filename);
        //fileInfo.put("thumbnailUrl", "")
        array.put(fileInfo);
        result.put("files", array);
        return result;
    }


    public JSONObject rename(HttpServletRequest request) throws IOException {

        String oldFilePath = request.getParameter("old").replace("//", "/");
        String newName = request.getParameter("new").replace("//", "/").replace(" ", "_").trim();


        if(oldFilePath.endsWith("/")) {
            oldFilePath = oldFilePath.substring(0,(oldFilePath.length() - 1));
        }

        int pathPos = oldFilePath.lastIndexOf("/");
        String path = oldFilePath.substring(0, pathPos + 1);
        String newFilePath = path + newName;

        File fileFrom = getFile(oldFilePath);
        boolean isDirectory = fileFrom.isDirectory();
        File fileTo = getFile(newFilePath);
        String filename = fileFrom.getName();

        JSONObject result = new JSONObject(FileManagerHelper.getDefaultItem());

        try {
            if(fileTo.exists()) {
                if(fileTo.isDirectory()) {
                    return errorResponse(String.format(translation.getString("DIRECTORY_ALREADY_EXISTS"), newName));
                }
                else { // fileTo.isFile
                    return errorResponse(String.format(translation.getString("FILE_ALREADY_EXISTS"), newName));
                }
            }
            else if (!fileFrom.renameTo(fileTo)){
                return errorResponse(String.format(translation.getString("ERROR_RENAMING_DIRECTORY"), filename + "#" + newName));
            }
        } catch (Exception e) {
            if(fileFrom.isDirectory()) {
                return errorResponse(String.format(translation.getString("ERROR_RENAMING_DIRECTORY"), filename + "#" + newName));
            } else {
                return errorResponse(String.format(translation.getString("ERROR_RENAMING_FILE"), filename + "#" + newName));
            }
        }

        result.put("Old Path", isDirectory ? oldFilePath + "/" : oldFilePath);
        result.put("New Path", isDirectory ? newFilePath + "/" : newFilePath );
        result.put("Old Name", filename);
        result.put("New Name", newName);

        return result;
    }

    public JSONObject move(HttpServletRequest request) throws IOException {

        String oldFilePath = request.getParameter("old").replace("//", "/");
        String newFilePath = request.getParameter("new").replace("//", "/");

        File fileFrom = getFile(oldFilePath);
        String filename = fileFrom.getName();
        File fileTo = getFile(newFilePath + filename);
        boolean isDirectory = fileFrom.isDirectory();
        JSONObject result = new JSONObject(FileManagerHelper.getDefaultItem());
        result.put("Old Path", oldFilePath);
        result.put("New Path", newFilePath );
        result.put("Old Name", filename);
        result.put("New Name", filename);

        Files.move(fileFrom.toPath(), fileTo.toPath());


        return result;

    }

    private JSONObject delete(HttpServletRequest request) throws IOException {

        JSONObject array = null;
        String path = request.getParameter("path").replace("//", "/");
        File file = new File(this.documentRoot +path);
        //array = new JSONObject(FileManagerHelper.getDefaultItem());


        array= new JSONObject();
        array.put("Path", path);
        array.put("Error", "");
        array.put("Code", 0);

        if(file.isDirectory()) {
            FileManagerHelper.removeRecursive(file.toPath());
        } else if(file.exists()) {
            if (!file.delete()){
                return errorResponse(String.format(translation.getString("ERROR_DELETING FILE"), path));
            }
        } else {
            return errorResponse(translation.getString("INVALID_DIRECTORY_OR_FILE"));
        }
        return array;
    }

    public JSONObject errorResponse(String msg) {
        JSONObject errorInfo = new JSONObject();
        Map<String, String> properties = FileManagerHelper.getDefaultProperties();
        try {
            errorInfo.put("Error", msg);
            errorInfo.put("Code", "-1");
            errorInfo.put("Properties", properties);
        } catch (JSONException e) {
            logger.severe("JSONObject error");
        }
        return errorInfo;
    }

    public JSONObject getInfo(HttpServletRequest request) throws JSONException, IOException {

        String path = request.getParameter("path").replace("//", "/");

        File file = new File(this.documentRoot + path);

        if (!file.canRead()) {
            return errorResponse(translation.getString("NOT_ALLOWED_SYSTEM"));
        }

        // TO DO - check if file is allowed regarding the security Policy settings

        return new JSONObject(getFileInfo(path, false));
    }


    public JSONObject addFolder(HttpServletRequest request) throws IOException {
        Slugify slugify = new Slugify();
        String path = request.getParameter("path").replace("//", "/");
        String name = request.getParameter("name");
        JSONObject array = null;

        String filename = slugify.slugify(name);

        if (filename.length() == 0) // the name existed of only special characters
            return errorResponse(String.format(translation.getString("UNABLE_TO_CREATE_DIRECTORY"), name));
        else {
            File file = new File(this.documentRoot + path + filename);
            if(file.isDirectory()) {
                return errorResponse(String.format(translation.getString("DIRECTORY_ALREADY_EXISTS"), filename));
            }
            else if (!file.mkdir()){
                return errorResponse(String.format(translation.getString("UNABLE_TO_CREATE_DIRECTORY"), filename));
            }
            else {
                array = new JSONObject(FileManagerHelper.getDefaultItem());
                array.put("Path", path);
                array.put("Parent", path);
                array.put("Name", filename);
                return  array;

            }
        }
    }

    private Map getFileInfo(String path, boolean generateThumbnail) throws IOException {
        Map item = FileManagerHelper.getDefaultItem();
        Map props = FileManagerHelper.getDefaultProperties();
        Set<String> allowedImageExt = new HashSet<String>(Arrays.asList(config.getProperty("images").split(",")));

        // default
        String publicIconDirectory = publicFileManagerRoot + config.getProperty("icons-path");
        String serverIconDirectory = fileManagerRoot + config.getProperty("icons-path");

        // get file
        File file = getFile(path);
        Path pathFile = file.toPath();
        BasicFileAttributes attr = Files.readAttributes(pathFile, BasicFileAttributes.class);

        String filename = file.getName();

        int fileProtected = file.canRead() && file.canWrite() ? 0 : 1;
        item.put("Path", path);
        item.put("Protected", fileProtected);
        item.put("Filename", filename);
        item.put("Thumbnail", publicIconDirectory + "/" + config.getProperty("icons-default"));
        props.put("Date Created", dt.format(new Date(attr.creationTime().toMillis())));
        props.put("Date Modified", dt.format(new Date(file.lastModified())));
        props.put("filemtime", file.lastModified());

        String protectedIcon = "";
        if (fileProtected == 1) {
            protectedIcon = "locked_";
        }

        if (file.isDirectory()) {
            item.put("File Type", "dir");
            item.put("Path", item.get("Path") + "/");
            item.put("Thumbnail", publicIconDirectory + "/" + protectedIcon + config.getProperty("icons-directory"));
        } else {
            String fileExt = filename.substring(filename.lastIndexOf(".") + 1);
            if(!generateThumbnail) {
                item.put("Preview", "/../../../public" + path);
            }
            item.put("File Type", fileExt);
            props.put("Size", "" + file.length());

            File icon = new File(serverIconDirectory + "/" + (fileExt.toLowerCase() + ".png"));
            if (icon.exists()) {
                item.put("Thumbnail", publicIconDirectory + "/" + (fileExt.toLowerCase() + ".png"));
            }
            if (allowedImageExt.contains(fileExt)) {
                Dimension dim = FileManagerHelper.getImageSize(documentRoot + path);
                String thumbnailUrl = connectorUrl + "?mode=getimage&path=" + path + "&time=" + System.currentTimeMillis();
                if(generateThumbnail)
                    thumbnailUrl+= "&thumbnail=true";

                item.put("Thumbnail", thumbnailUrl);
                props.put("Height", "" + dim.height);
                props.put("Width", "" + dim.width);

            }
            if (fileProtected == 1) {
                item.put("Thumbnail", publicIconDirectory + "/" + protectedIcon + config.getProperty("icons-default"));
            }
        }

        item.put("Properties", props);
        return item;
    }


    public JSONObject getFolder(HttpServletRequest request) throws IOException {
        JSONObject array = null;
        String path = request.getParameter("path").replace("//", "/");
        String type = request.getParameter("type");
        File dir = null;

        dir = getFile(path);

        File file = null;
        Set<String> unallowedFiles = new HashSet<String>(Arrays.asList(config.getProperty("unallowed_files").split(",")));
        Set<String> unallowedDirs = new HashSet<String>(Arrays.asList(config.getProperty("unallowed_dirs").split(",")));
        Set<String> allowedImageExt = new HashSet<String>(Arrays.asList(config.getProperty("images").split(",")));


        if (!dir.isDirectory()) {
            return errorResponse(String.format(translation.getString("DIRECTORY_NOT_EXIST"), path));
        } else {
            if (!dir.canRead()) {
                return errorResponse(String.format(translation.getString("UNABLE_TO_OPEN_DIRECTORY"), path));
            } else {
                array = new JSONObject();
                String[] files = dir.list();
                String filePath;
                for (int i = 0; i < files.length; i++) {

                    file = new File(documentRoot + path + files[i]);
                    filePath = path + files[i];
                    String filename = file.getName();
                    if (file.isDirectory() && !unallowedDirs.contains(files[i])) {
                        array.put(filePath, getFileInfo(filePath, true));
                    } else if (file.isFile() && !unallowedFiles.contains(files[i])) {

                        String fileExt = filename.substring(filename.lastIndexOf(".") + 1);
                        if (type == null || type.equals("Image") && allowedImageExt.contains(fileExt)) {
                            array.put(filePath, getFileInfo(filePath, true));
                        }
                    }
                }
            }
        }
        return array;
    }


    private JSONObject getImage(Boolean thumbnail, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getParameter("path").replace("//", "/");
        File file = getFile(path);
        if (!file.exists()) {
            return errorResponse(translation.getString("FILE_DOES_NOT_EXIST"));
        }
        String filename = file.getName();
        String fileExt = filename.substring(filename.lastIndexOf(".") + 1);
        String mimeType = (!StringUtils.isEmpty(FileManagerHelper.mimetypes.get(fileExt))) ? FileManagerHelper.mimetypes.get(fileExt) : "application/octet-stream";
        InputStream is = null;

        if (thumbnail) {
            BufferedImage image = ImageIO.read(file);
            BufferedImage resizedImage = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 100, 100, Scalr.OP_ANTIALIAS);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, fileExt, os);
            is = new ByteArrayInputStream(os.toByteArray());
            response.setContentLength(os.toByteArray().length);
        }
        else{
            is = new FileInputStream(file);
            response.setContentLength((int) file.length());
        }

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

        FileCopyUtils.copy(new BufferedInputStream(is), response.getOutputStream());
        return null;
    }


    public JSONObject download(boolean force, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getParameter("path").replace("//", "/");
        File file = getFile(path);
        String filename = file.getName();

        if (!file.exists()) {
            return errorResponse(translation.getString("FILE_DOES_NOT_EXIST"));
        }

        if (!force) {
            JSONObject ajax = new JSONObject(FileManagerHelper.getDefaultItem());
            ajax.put("Path", path);
            return ajax;
        }

        String fileExt = filename.substring(filename.lastIndexOf(".") + 1);

        String mimeType = (!StringUtils.isEmpty(FileManagerHelper.mimetypes.get(fileExt))) ? FileManagerHelper.mimetypes.get(fileExt) : "application/octet-stream";
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength((int) file.length());
        FileCopyUtils.copy(new BufferedInputStream(new FileInputStream(file)), response.getOutputStream());
        return null;
    }


    private File getFile(String path) {
        return new File(this.documentRoot + path);
    }
    
}
