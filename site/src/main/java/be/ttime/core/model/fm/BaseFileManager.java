//package be.ttime.core.model.fm;
//
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * Created by fabricecipolla on 31/07/16.
// */
//abstract class BaseFileManager {
//
//    protected static final Map<String, Object> defaultInfo;
//    static {
//        defaultInfo = new HashMap<>();
//        defaultInfo.put("Path", "");
//        defaultInfo.put("Filename", "");
//        defaultInfo.put("Protected", "");
//        defaultInfo.put("Thumbnail", "");
//        defaultInfo.put("Preview", "");
//        defaultInfo.put("Error", "");
//        defaultInfo.put("Code", 0);
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("Date Created", "");
//        properties.put("Date Modified", "");
//        properties.put("filemtime", "");
//        properties.put("Width", "");
//        properties.put("Size", "");
//        defaultInfo.put("Properties", properties);
//    }
//
//    protected JSONObject error = null;
//    protected Map<String, String> get =  new HashMap<String, String>();
//    protected Map<String, String>  properties =  new HashMap<String, String>();
//    protected Map item =  new HashMap();
//    protected Map<String, String>  params =  new HashMap<String, String>();
//    protected static Properties config = null;
//    protected static JSONObject language = null;
//    protected String documentRoot = "";
//    protected String fileManagerRoot = "";
//    protected String referer = "";
//
//    /**
//     * Returns file info - filemanager action
//     * @return array
//     */
//    abstract JSONObject getInfo();
//
//    /**
//     * Open specified folder - filemanager action
//     * @return array
//     */
//    abstract JSONObject getFolder();
//
//    /**
//     * Open and edit file - filemanager action
//     * @return array
//     */
//    abstract JSONObject editFile();
//
//    /**
//     * Save data to file after editing - filemanager action
//     */
//    abstract JSONObject saveFile();
//
//    /**
//     * Rename file or folder - filemanager action
//     */
//    abstract JSONObject rename();
//
//    /**
//     * Move file or folder - filemanager action
//     */
//    abstract JSONObject move();
//
//    /**
//     * Delete existed file or folder - filemanager action
//     */
//    abstract JSONObject delete();
//
//    /**
//     * Replace existed file - filemanager action
//     */
//    abstract JSONObject replace();
//
//    /**
//     * Upload new file - filemanager action
//     */
//    abstract JSONObject add(HttpServletRequest request);
//
//    /**
//     * Create new folder - filemanager action
//     * @return array
//     */
//    abstract JSONObject addFolder();
//
//    /**
//     * Download file - filemanager action
//     * @param force Whether to start download after validation
//     */
//    abstract JSONObject download(boolean force, HttpServletRequest response);
//
//    /**
//     * Returns image file - filemanager action
//     * @param thumbnail Whether to generate image thumbnail
//     */
//    abstract JSONObject getimage(boolean thumbnail);
//
//    /**
//     * Read file data - filemanager action
//     * Intended to read and output file contents when it's not possible to get file by direct URL (e.g. protected file).
//     * Initially implemented for viewing audio/video/docs/pdf and other files hosted on AWS S3 remote server.
//     */
//    abstract JSONObject readfile();
//
//    /**
//     * Retrieves storage summarize info - filemanager action
//     * @return array
//     */
//    abstract JSONObject summarize();
//
//    public JSONObject handleRequest(HttpServletRequest request, HttpServletRequest response){
//
//        final String method = request.getMethod();
//        final String mode = request.getParameter("mode");
//        JSONObject responseData = null;
//
//        if(StringUtils.isEmpty("mode"))
//            return getErrorObject(lang("MODE_ERROR"));
//
//        try {
//            if (method.equals("GET")) {
//                switch (mode) {

//                    case "editfile":
//
//                        break;
//                    case "summarize":
//
//                        break;
//                }
//            } else if (method.equals("POST")) {
//
//                switch (mode) {
//                    case "add":
//                        this.add(request);
//                        break;
//                    case "replace":
//                        break;
//                    case "savefile" :
//
//                        break;
//                }
//            }
//        } catch(GetParamException e) {
//            return FileManagerHelper.getErrorObject("INVALID_VAR");
//        }
//
//
//        return responseData;
//        //return responseData == null ? this.getError().toString() : responseData.toString();
//    }
//
//
//    public boolean setGetVar(String var, String value) throws GetParamException {
//        boolean retval = false;
//        if(null == value || value == "") {
//           throw new GetParamException();
//        } else {
//            this.get.put(var, FileManagerHelper.sanitize(value));
//            retval = true;
//        }
//        return retval;
//    }
//
//
//
//    /**
//     * Get a value of the translation
//     * @param key
//     * @return
//     */
//    public String lang(String key) {
//        String text = "";
//        try {
//            text = language.getString(key);
//        } catch (Exception e) {}
//        if (text == null || text == "")
//            text = "Language string error on " + key;
//        return text;
//    }
//
//    public static boolean isImage(String fileName){
//        boolean isImage = false;
//        String ext = "";
//        int pos = fileName.lastIndexOf(".");
//        if (pos > 1 && pos != fileName.length()){
//            ext = fileName.substring(pos + 1);
//            isImage = FileManagerHelper.contains(config.getProperty("images"), ext);
//        }
//        return isImage;
//    }
//
//
//     * Check if extension is allowed regarding the security Policy / Restrictions settings
//     * @param string $file
//     * @return bool
//
//    public function is_allowed_file_type($file)
//    {
//        $path_parts = pathinfo($file);
//
//        // if there is no extension
//        if (!isset($path_parts['extension'])) {
//            // we check if no extension file are allowed
//            return (bool)$this->config['security']['allowNoExtension'];
//        }
//
//        $exts = array_map('strtolower', $this->config['security']['uploadRestrictions']);
//
//        if($this->config['security']['uploadPolicy'] == 'DISALLOW_ALL') {
//
//            if(!in_array(strtolower($path_parts['extension']), $exts))
//                return false;
//        }
//        if($this->config['security']['uploadPolicy'] == 'ALLOW_ALL') {
//
//            if(in_array(strtolower($path_parts['extension']), $exts))
//                return false;
//        }
//
//        return true;
//    }*/
//}
