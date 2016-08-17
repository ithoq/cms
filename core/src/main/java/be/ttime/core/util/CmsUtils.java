package be.ttime.core.util;

import be.ttime.core.model.field.PageData;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.service.IApplicationService;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibatis.common.jdbc.ScriptRunner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class CmsUtils {

    //public final String csrfParameterName = "_csrf";
    public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String CONTENT_TYPE_PAGE = "PAGE";
    public final static String CONTENT_TYPE_PAGE_LINK = "PAGE_LINK";
    public final static String CONTENT_TYPE_NEWS = "NEWS";
    public final static String CONTENT_TYPE_ARTICLE = "ARTICLE";

    public final static String BLOCK_TYPE_CONTENT = "CONTENT";
    public final static String BLOCK_TYPE_NAVIGATION = "NAVIGATION";
    public final static String BLOCK_TYPE_CONTENT_TEMPLATE = "PAGE_TEMPLATE";
    public final static String BLOCK_TYPE_SYSTEM = "SYSTEM";
    public final static String BLOCK_TYPE_FIELDSET = "FIELDSET";

    public final static String BLOCK_TEMPLATE_BASIC_PAGE = "TEMPLATE_BASIC_PAGE";
    public final static String BLOCK_TEMPLATE_WEBCONTENT = "TEMPLATE_WEBCONTENT";

    public final static String BLOCK_PAGE_MASTER = "PAGE_MASTER";
    public final static String BLOCK_PAGE_LOGIN = "PAGE_LOGIN";

    public final static String BLOCK_FIELD_TEXT = "FIELD_TEXT";
    public final static String BLOCK_FIELD_TEXTAREA = "FIELD_TEXTAREA";
    public final static String BLOCK_FIELD_TINYMCE = "FIELD_TINYMCE";
    public final static String BLOCK_FIELD_DATEPICKER = "FIELD_DATEPICKER";

    public final static String UPLOAD_DIRECTORY_PRIVATE = "webdata/upload/pages/";
    public final static String UPLOAD_DIRECTORY_PUBLIC = "webdata/upload/public/";

    public final static String HEADER_VALIDATION_FAILED = "Validation-Failed";

    public final static String GROUP_SUPER_ADMIN = "GROUP_SUPER_ADMIN";

    public final static String ROLE_MEMBER = "ROLE_MEMBER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";
    public final static String ROLE_ADMIN_SEO = "ROLE_ADMIN_SEO";
    public final static String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public final static String ROLE_ADMIN_CMS = "ROLE_ADMIN_CMS";
    public final static String ROLE_ADMIN_CMS_FILE = "ROLE_ADMIN_CMS_FILE";
    public final static String ROLE_ADMIN_CMS_DELETE = "ROLE_ADMIN_CMS_DELETE";
    public final static String ROLE_ADMIN_WEBCONTENT = "ROLE_ADMIN_WEBCONTENT";
    public final static String ROLE_ADMIN_WEBCONTENT_DELETE = "ROLE_ADMIN_WEBCONTENT_DELETE";
    public final static String ROLE_ADMIN_USER = "ROLE_ADMIN_USER";
    public final static String ROLE_ADMIN_USER_DELETE = "ROLE_ADMIN_USER_DELETE";
    public final static String ROLE_ADMIN_BLOCK = "ROLE_ADMIN_BLOCK";
    public final static String ROLE_ADMIN_BLOCK_DELETE = "ROLE_ADMIN_BLOCK_DELETE";
    public final static String ROLE_ADMIN_GROUP = "ROLE_ADMIN_GROUP";
    public final static String ROLE_ADMIN_GROUP_DELETE = "ROLE_ADMIN_GROUP_DELETE";

    public static List<GrantedAuthority> fullPrivilegeList;

    public static void setFullPrivilegeList(List<GrantedAuthority> list) {
        fullPrivilegeList = list;
    }

    public static void fillModelMap(ModelMap model, HttpServletRequest request, IApplicationService applicationService) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        model.put("attr", CmsUtils.getAttributes(request));
        model.put("get", CmsUtils.getParameters(request));
        model.put("csrf", CmsUtils.getCsrfInput(request));
        model.put("session", request.getSession(false));
        model.put("user", CmsUtils.getCurrentUser());
        model.put("locale", LocaleContextHolder.getLocale().toString());
        model.put("now", now);
        model.put("now_year", cal.get(Calendar.YEAR));
        model.put("uri", request.getRequestURI());
        model.put("defaultSiteLang", applicationService.getDefaultSiteLang());
        model.put("siteLang", applicationService.getSiteLanguages());
    }

    public static String capitalizeFirstLetter(String original) {
        String trimedString = original.trim();
        if (trimedString == null || trimedString.length() == 0) {
            return original;
        }
        return trimedString.substring(0, 1).toUpperCase() + trimedString.substring(1).toLowerCase();
    }

    public static String emptyStringIfnull(String value) {
        return StringUtils.isEmpty(value) ? "" : value;
    }

    public static void fillModelAndView(ModelAndView model, HttpServletRequest request, IApplicationService applicationService) {
        fillModelMap(model.getModelMap(), request, applicationService);
    }

    public static String getCsrfInput(HttpServletRequest request) {
        Object param = request.getAttribute("_csrf");
        CsrfToken csrf = (param instanceof CsrfToken ? (CsrfToken) param : null);
        String csrfName = csrf != null ? csrf.getParameterName() : "";
        String csrfValue = csrf != null ? csrf.getToken() : "";
        return "<input type=\"hidden\" name=\"" + csrfName + "\" value=\"" + csrfValue + "\" />";
    }

    public static String alert(@NonNull String type, @NonNull String message, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"alert alert-").append(type).append("\" role=\"alert\">");
        if (title != null) {
            sb.append("<strong>").append(title).append("</strong> - ");
        }
        sb.append(message);
        sb.append("</div>");
        return sb.toString();
    }

    public static Map<String, Object> getAttributes(HttpServletRequest request) {
        Map<String, Object> attr = new HashMap<>();
        Enumeration params = request.getAttributeNames();
        if (params != null) {
            while (params.hasMoreElements()) {
                String paramName = (String) params.nextElement();
                attr.put(paramName, request.getAttribute(paramName));
            }
        }
        return attr;
    }

    public static Map<String, Object> getParameters(HttpServletRequest request) {

        Map<String, Object> get = new HashMap<>();
        Enumeration params = request.getParameterNames();
        if (params != null) {
            while (params.hasMoreElements()) {
                String paramName = (String) params.nextElement();
                get.put(paramName, request.getParameter(paramName));
            }
        }
        return get;
    }

    public static UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity custom = null;
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            try {
                custom = (UserEntity) auth.getPrincipal();
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return custom;
    }

    public static boolean hasGroup(String role) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return false;

        for (GroupEntity groupEntity : user.getGroups()) {
            if (groupEntity.getName().equals(role))
                return true;
        }
        return false;
    }

    public static boolean isSuperAdmin() {
        return hasGroup(CmsUtils.GROUP_SUPER_ADMIN);
    }

    public static boolean hasGroup(UserEntity user, String role) {
        if (user == null)
            return false;

        for (GroupEntity groupEntity : user.getGroups()) {
            if (groupEntity.getName().equals(role))
                return true;
        }
        return false;
    }

    /* need to test */
    public static boolean isLogged(){
        return getCurrentUser() != null ? true : false;
    }

    public static boolean hasRole(String role) {
        // get security context from thread local
        UserEntity currentUser = getCurrentUser();
        if(currentUser != null && currentUser.getAuthorities().contains(new SimpleGrantedAuthority(role))){
                return true;
        }

        return false;
    }

    public static boolean hasAnyRole(Collection<String> roles) {
        UserEntity currentUser = getCurrentUser();

        if(currentUser != null) {
            for (String role : roles) {
                if(currentUser.getAuthorities().contains(new SimpleGrantedAuthority(role)))
                    return true;
            }
        }
        return false;
    }

    private static Collection<SimpleGrantedAuthority> getSimpleGrantedAuthorityList(Collection<String> roles){
        Collection<SimpleGrantedAuthority> result = new ArrayList<>();
        for (String role : roles) {
            result.add(new SimpleGrantedAuthority(role));
        }
        return result;
    }

    public static boolean hasRoles(Collection<String> roles) {
        UserEntity currentUser = getCurrentUser();

        if(currentUser != null)
            return currentUser.getAuthorities().containsAll(getSimpleGrantedAuthorityList(roles));

        return false;
    }


    public static Path getResourceFilePath(String resourceName) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(resourceName).toURI());
    }

    public static String getResourceFileContent(String resourceName) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(getResourceFilePath(resourceName)));
    }

    public static void executeResourceFileScript(EntityManager em, String scriptLocation) {

        Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try {
                String sqlScript = getResourceFileContent(scriptLocation);
                ScriptRunner sr = new ScriptRunner(connection, false, false);
                sr.runScript(new StringReader(sqlScript));
            } catch (Exception e) {
                log.error("Error executing script : " + e.toString());
            }
        });
    }

    public static Date LocalDateTimeToDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static HashMap<String, Object> parseData(String pageDataString) {
        PageData pageData = parseStringToPageDate(pageDataString);
        HashMap<String, Object> data = new HashMap<>();
        data.putAll(pageData.getDataBoolean());
        data.putAll(pageData.getDataBooleanArray());
        data.putAll(pageData.getDataDate());
        data.putAll(pageData.getDataDateArray());
        data.putAll(pageData.getDataDouble());
        data.putAll(pageData.getDataDoubleArray());
        data.putAll(pageData.getDataInteger());
        data.putAll(pageData.getDataIntegerArray());
        data.putAll(pageData.getDataString());
        data.putAll(pageData.getDataStringArray());

        return data;
    }

    public static PageData parseStringToPageDate(String pageDataString) {
        Gson gson = new GsonBuilder().setDateFormat(CmsUtils.DATETIME_FORMAT).create();
        return gson.fromJson(pageDataString, PageData.class);
    }

    public static String computeSlug(final ContentEntity content, final ContentDataEntity contentData, final String locale, final boolean forceLang) {
        if(StringUtils.isEmpty(contentData.getSlug())){
            throw new IllegalArgumentException("Slug should not be empty!");
        }
        String slug = StringUtils.trimToEmpty(computeSlugWithSlashes(content, contentData, locale, forceLang)).replaceAll("/+", "/");
        return (slug.length() > 1) ? slug.replaceAll("/+$", "") : slug;
    }

    private static String computeSlugWithSlashes(final ContentEntity content, final ContentDataEntity contentData, final String locale, final boolean forceLang) {
        final ContentEntity parent = content.getContentParent();
        boolean malFormed = contentData.getSlug().charAt(0) != '/';
        if(malFormed){
            log.error("Slug malformed (missing /) : contentData with id : " + contentData.getId());
        }
        String slug = malFormed ? '/' + contentData.getSlug() : contentData.getSlug();
        if (parent == null) {
            if (forceLang) {
                return "/" + locale.toString() + contentData.getSlug();
            } else {
                return contentData.getSlug();
            }

        } else {
            final ContentDataEntity parentContentData = parent.getContentDataList().get(locale);

            return parentContentData.getComputedSlug() + "/" + contentData.getSlug();
        }
    }

    public static String getFilePath(File file, String limit) {

        File parent = file.getParentFile();
        if (limit.equals(parent.getName())) {
            return "";
        } else {
            return getFilePath(parent, limit) + "/" + parent.getName();
        }
    }

    public static File uploadFile(MultipartFile uploadFile, boolean isPrivate) throws IOException {
        return uploadFile(uploadFile, isPrivate, null);
    }

    public static File uploadFile(MultipartFile uploadFile, boolean isPrivate, String prePath) throws IOException {
        Slugify slg = null;
        try {
            slg = new Slugify();
        } catch (IOException e) {
            log.error("Impossible to create instance Slugify", e);
        }
        String ext = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
        String baseName = FilenameUtils.getBaseName(uploadFile.getOriginalFilename());
        String prefix = slg.slugify(baseName);
        File serverFile = null;
        if (isPrivate) {
            if (prefix.length() < 3) {
                prefix += "___";
            }
            serverFile = File.createTempFile(prefix, "." + ext, getUploadDirectory(isPrivate));
        } else {
            String filePath = getUploadDirectory(false).getAbsolutePath();
            if (!StringUtils.isEmpty(prePath)) {
                filePath += "/" + prePath;
            }
            filePath += "/" + prefix + "." + ext;
            serverFile = new File(filePath);
        }
        serverFile.getParentFile().mkdirs();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(uploadFile.getBytes());
        stream.close();
        return serverFile;
    }

    public static File getUploadDirectory(boolean isPrivate) {
        // check if upload path exist
        File fileDir = new File((isPrivate) ? UPLOAD_DIRECTORY_PRIVATE : UPLOAD_DIRECTORY_PUBLIC);

        if (!fileDir.exists()) {
            try {
                FileUtils.forceMkdir(fileDir);
            } catch (IOException e) {
                log.error("Impossible to create the upload directory : " + fileDir.getPath(), e);
                return null;
            }
        }
        return fileDir;
    }

    public static Date parseDate(String date, String time) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        if (StringUtils.isEmpty(time)) {
            time = "00:00:00";
        } else {
            // il manque le premier 0
            if (time.length() == 4) {
                time = "0" + time;
            }
            // il manque les secondes
            if (time.length() == 5) {
                time += ":00";
            }
        }
        String dateString = date + " " + time;
        SimpleDateFormat df = new SimpleDateFormat(CmsUtils.DATETIME_FORMAT);
        Date dateBegin;

        try {
            dateBegin = df.parse(dateString);
        } catch (ParseException e) {
            dateBegin = null;
        }
        return dateBegin;
    }

    public static PageData fillData(PageData pageData, List<ContentTemplateFieldsetEntity> contentTemplateFieldset, HttpServletRequest request) throws IOException, ParseException {

        Slugify slg = new Slugify();
        if (pageData == null) {
            pageData = new PageData();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat(CmsUtils.DATE_FORMAT);
        for (ContentTemplateFieldsetEntity ctf : contentTemplateFieldset) {

            FieldsetEntity fieldset = ctf.getFieldset();
            Map<String, Object> inputsMap = new HashMap<>();
            for (InputDataEntity inputDataEntity : ctf.getDataEntities()) {
                String finalName = ctf.getNamespace() + "_" + slg.slugify(inputDataEntity.getInputDefinition().getName());
                String type = inputDataEntity.getInputDefinition().getType();
                boolean isArray = ctf.isArray() && fieldset.isArray();
                if (type.equals("date")) {

                    if (isArray) {
                        String[] stringDateArray = request.getParameter(finalName).split(",");
                        Date[] dateArray = new Date[stringDateArray.length];
                        for (int i = 0; i < stringDateArray.length; i++) {
                            dateArray[i] = dateFormatter.parse(stringDateArray[i]);
                        }
                        pageData.getDataDateArray().put(finalName, dateArray);
                    } else {
                        pageData.getDataDate().put(finalName, dateFormatter.parse(request.getParameter(finalName)));
                    }


                } else if (type.equals("file")) {
                    // TODO : Upload and save the name
                    /*
                    if(isArray){
                        pageData.getDataStringArray().put(finalName, request.getParameterValues(finalName));
                    } else{
                        pageData.getDataString().put(finalName, request.getParameter(finalName));
                    }*/
                } else if (type.equals("integer")) {
                    if (isArray) {
                        final String[] stringArray = request.getParameterValues(finalName);
                        final Integer[] ints = new Integer[stringArray.length];
                        for (int i = 0; i < stringArray.length; i++) {
                            ints[i] = Integer.parseInt(stringArray[i]);
                        }
                        pageData.getDataIntegerArray().put(finalName, ints);
                    } else {
                        pageData.getDataInteger().put(finalName, Integer.parseInt(request.getParameter(finalName)));
                    }
                } else if (type.equals("double")) {
                    if (isArray) {
                        final String[] stringArray = request.getParameterValues(finalName);
                        final Double[] doubles = new Double[stringArray.length];
                        for (int i = 0; i < stringArray.length; i++) {
                            doubles[i] = Double.parseDouble(stringArray[i]);
                        }
                        pageData.getDataDoubleArray().put(finalName, doubles);
                    } else {
                        pageData.getDataDouble().put(finalName, Double.parseDouble(request.getParameter(finalName)));
                    }
                } else if (type.equals("boolean")) {
                    if (isArray) {
                        final String[] stringArray = request.getParameterValues(finalName);
                        final Boolean[] booleans = new Boolean[stringArray.length];
                        for (int i = 0; i < stringArray.length; i++) {
                            booleans[i] = Boolean.parseBoolean(stringArray[i]);
                        }
                        pageData.getDataBooleanArray().put(finalName, booleans);
                    } else {
                        pageData.getDataString().put(finalName, request.getParameter(finalName));
                    }
                } else {
                    if (isArray) {
                        pageData.getDataStringArray().put(finalName, request.getParameterValues(finalName));
                    } else {
                        pageData.getDataString().put(finalName, request.getParameter(finalName));
                    }
                }

            }
        }

        return pageData;
    }

    public static PageData fillData(List<ContentTemplateFieldsetEntity> contentTemplateFieldset, HttpServletRequest request) throws IOException, ParseException {
        return fillData(null, contentTemplateFieldset, request);
    }

    public static String twoDigit(int number) {
        return String.format("%02d", number);
    }

    public static UserEntity getPrincipal(SessionRegistry sessionRegistry, UserEntity user) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            final UserEntity loggedUser = (UserEntity) principal;
            if (loggedUser.getId() == user.getId())
                return loggedUser;
        }
        return null;
    }

    public static boolean userIsLogged(SessionRegistry sessionRegistry, UserEntity user){
        return getPrincipal(sessionRegistry, user) != null ? true : false;
    }

    public static void updateSessionUser(UserEntity user){
        Authentication newAuth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public static void expireSession(SessionRegistry sessionRegistry, UserEntity user){
        UserEntity principal = getPrincipal(sessionRegistry, user);
        if(principal != null) {
            List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);
            for (SessionInformation session : allSessions) {
                session.expireNow();
            }
        }
    }

}
