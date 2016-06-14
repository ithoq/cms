package be.ttime.core.util;

import be.ttime.core.model.field.PageData;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.model.UserEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibatis.common.jdbc.ScriptRunner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

    public final static String BLOCK_PAGE_MASTER = "PAGE_MASTER";
    public final static String BLOCK_PAGE_LOGIN = "PAGE_LOGIN";
    public final static String BLOCK_FIELD_TEXT = "FIELD_TEXT";
    public final static String BLOCK_FIELD_TINYMCE = "FIELD_TINYMCE";
    public final static String BLOCK_FIELD_DATEPICKER = "FIELD_DATEPICKER";

    public final static String HEADER_VALIDATION_FAILED = "Validation-Failed";

    public static void fillModelMap(ModelMap model, HttpServletRequest request) {
        model.put("attr", CmsUtils.getAttributes(request));
        model.put("get", CmsUtils.getParameters(request));
        model.put("csrf", CmsUtils.getCsrfInput(request));
        model.put("session", request.getSession(false));
        model.put("user", CmsUtils.getCurrentUser());
        model.put("locale", LocaleContextHolder.getLocale().toString());
    }

    public static void fillModelAndView(ModelAndView model, HttpServletRequest request) {
        model.addObject("attr", CmsUtils.getAttributes(request));
        model.addObject("get", CmsUtils.getParameters(request));
        model.addObject("csrf", CmsUtils.getCsrfInput(request));
        model.addObject("session", request.getSession(false));
        model.addObject("user", CmsUtils.getCurrentUser());
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

    public static String computeSlug(final ContentEntity content, final ContentDataEntity contentData, final String locale) {
        return StringUtils.trimToEmpty(computeSlugWithSlashes(content, contentData, locale))
                .replaceAll("/+", "/")
                .replaceAll("/+$", "");
    }

    private static String computeSlugWithSlashes(final ContentEntity content, final ContentDataEntity contentData, final String locale) {
        final ContentEntity parent = content.getContentParent();
        if (parent == null) {
            return contentData.getSlug();
        } else {
            final ContentDataEntity parentContentData = parent.getDataList().get(locale);

//            Lazy init exception below
//
//            return computeSlugWithSlashes(parent, parentContentData, locale) + "/" + contentData.getSlug();

            return parentContentData.getComputedSlug() + "/" + contentData.getSlug();
        }
    }

}
