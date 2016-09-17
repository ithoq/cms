package be.ttime.core.handler;

import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.service.IApplicationService;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;

/**
 * {@link LocaleResolver} implementation that uses a cookie sent back to the user
 * in case of a custom setting, with a fallback to the specified default locale
 * or the request's accept-header locale.
 * <p>
 * <p>This is particularly useful for stateless applications without user sessions.
 * <p>
 * <p>Custom controllers can thus override the user's locale by calling
 * {@link #setLocale(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.Locale)},
 * for example responding to a certain locale change request.
 */
public class UrlLocaleResolver extends CookieGenerator implements LocaleResolver {

    /**
     * The name of the request attribute that holds the locale.
     *
     * @see org.springframework.web.servlet.support.RequestContext#getLocale
     */
    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = UrlLocaleResolver.class.getName() + ".LOCALE";
    /**
     * The default cookie name used if none is explicitly set.
     */
    public static final String DEFAULT_COOKIE_PUBLIC_NAME = "lang";
    public static final String DEFAULT_COOKIE_ADMIN_NAME = "admin-lang";
    @Autowired
    private IApplicationService appService;
    private boolean isAdmin;
    private ApplicationConfigEntity appConfig;
    private Map<String, Locale> langMap;
    private Locale defautLocale;


    public UrlLocaleResolver() {
    }

    private void initResolver(HttpServletRequest request) {
        isAdmin = request.getRequestURI().startsWith("/admin/");
        this.setCookieName(isAdmin ? DEFAULT_COOKIE_ADMIN_NAME : DEFAULT_COOKIE_PUBLIC_NAME);

        appConfig = appService.getApplicationConfig();
        langMap = (isAdmin ? appService.getAdminlanguagesMap() : appService.getSiteLanguagesMap());
        if (isAdmin) {
            defautLocale = LocaleUtils.toLocale(appConfig.getDefaultAdminLang().getLocale());
        } else {
            defautLocale = LocaleUtils.toLocale(appConfig.getDefaultPublicLang().getLocale());
        }

    }

    public Locale resolveLocale(HttpServletRequest request) {
        initResolver(request);
        // Check request for pre-parsed or preset locale.
        Locale locale = (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (locale != null && langMap.containsKey(locale.toString())) {
            return locale;
        }

        return determineLocale(request);
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        initResolver(request);

        if (locale != null && langMap.containsKey(locale.toString())) {
            // Set request attribute and add cookie.
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
            addCookie(response, locale.toString());
        } else {
            // Set request attribute to fallback locale and remove cookie.
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, defautLocale);
            removeCookie(response);
        }
    }

    protected Locale determineLocale(HttpServletRequest request) {

        Locale locale;

        locale = getCookieLocale(request);

        // check if locale is in session
        if (locale == null) {
            locale = getSessionLocale(request);
        }

        // check the browser locale
        if (locale == null) {
            locale = getRequestLocale(request);
        }

        // set the default locale
        if (locale == null) {
            locale = defautLocale;
        }

        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
        return locale;
    }

    @Override
    public String getCookieName() {
        return isAdmin ? DEFAULT_COOKIE_ADMIN_NAME : DEFAULT_COOKIE_PUBLIC_NAME;
    }

    private Locale getCookieLocale(HttpServletRequest request) {
        // Retrieve and parse cookie value.
        Cookie cookie = WebUtils.getCookie(request, getCookieName());
        if (cookie != null) {
            Locale l = StringUtils.parseLocaleString(cookie.getValue());
            if (langMap.containsKey(l.toString()))
                return l;
            // remove wrong cookie
            //cookie.setPath("/");
            //cookie.setValue(null);
            //cookie.setMaxAge(0);
            //response.addCookie(cookie);
        }
        return null;
    }


    private Locale getRequestLocale(HttpServletRequest request) {
        return (langMap.containsKey(request.getLocale().toString())) ? request.getLocale() : null;
    }

    private Locale getSessionLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object langSession = session.getAttribute(this.getCookieName());
            if (langSession != null) {
                String langStr = (String) langSession;
                if (langMap.containsKey(langStr)) {
                    return LocaleUtils.toLocale(langStr);
                }
            }
        }
        return null;
    }
}
