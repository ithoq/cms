package be.ttime.core.handler;

import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.service.IApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet Filter that allows one to specify a character encoding for requests.
 * This is useful because current browsers typically do not set a character
 * encoding even if specified in the HTML page or form.
 * <p>
 * <p>This filter can either apply its encoding if the request does not already
 * specify an encoding, or enforce this filter's encoding in any case
 * ("forceEncoding"="true"). In the latter case, the encoding will also be
 * applied as default response encoding (although this will usually be overridden
 * by a full content type set in the view).
 */
@Slf4j
@Component
public class ForceLocalUrlFilter implements Filter {

    /**
     * Default name of the locale specification parameter: "locale".
     */
    private static final Pattern LOCALE_PATTERN_ISO_639_1 = Pattern.compile("^/([a-z]{2})\\b");
    private static final Pattern LOCALE_PATTERN_LOCALE = Pattern.compile("^/([a-z]{2})\\b");
    private static final String LANG_PARAM_NAME = UrlLocaleResolver.DEFAULT_COOKIE_NAME;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private ServletContext sc;
    private Map<String, Locale> langMap;
    private ApplicationContext appContext;
    private IApplicationService appService;
    private ApplicationConfigEntity appConfig;

    public static final String ALREADY_FILTERED_ATTRIBUTE = ".FILTERED";

    private static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sc = filterConfig.getServletContext();
        appContext = WebApplicationContextUtils.getWebApplicationContext(sc);
        appService = (IApplicationService)appContext.getBean("applicationService");
        appConfig = appService.getApplicationConfig();
    }

    @Override
    public void destroy() {

    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("OncePerRequestFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        boolean hasAlreadyFilteredAttribute = request.getAttribute(ALREADY_FILTERED_ATTRIBUTE) != null;

        if (hasAlreadyFilteredAttribute || skipDispatch(httpRequest)) {
            // Proceed without invoking this filter...
            filterChain.doFilter(request, response);
        }
        else {
            // Do invoke this filter...
            request.setAttribute(ALREADY_FILTERED_ATTRIBUTE, Boolean.TRUE);
            try {
                doFilterInternal(httpRequest, httpResponse, filterChain);
            }
            finally {
                // Remove the "already filtered" request attribute for this request.
                request.removeAttribute(ALREADY_FILTERED_ATTRIBUTE);
            }
        }
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        final String requestURI = request.getRequestURI();
        langMap = appService.getLanguagesMap();
        if(requestURI.startsWith("/resources/") || requestURI.equals("/favicon.ico") || langMap.isEmpty()){
            chain.doFilter(request, response);
            return;
        }

        boolean isAdmin = requestURI.startsWith("/admin/");
        Locale locale = null;

        // Check if locale is in the url
        if (requestURI != null && !isAdmin) {

            // Check if locale is in the url
            Matcher matcher = matchLocalePattern(requestURI);
            String urlLocale = (matcher != null) ? matcher.group(1) : null;
            if (urlLocale != null && langMap.containsKey(urlLocale)) {
                locale = langMap.get(urlLocale);
            } else {
                // redirection
                if(appConfig.isForcedLangInUrl() && !isAdmin && isredirectablePath(requestURI)) {
                    // Redirection if is not ajax
                    if (!isAjax(request) && "GET".equals(request.getMethod())) {
                        String redirectUrl = "/" +
                                getDefaultLocaleUrlString(isAdmin) +
                                requestURI;

                        redirectStrategy.sendRedirect(request, response, redirectUrl);
                        return;
                    }
                }
            }
        }
        // check if locale is in param
        if (locale == null){
            String langParam = request.getParameter(LANG_PARAM_NAME);
            if(langParam != null && langMap.containsKey(langParam)){
                locale = langMap.get(langParam);
            }
        }

        // check if locale is in session
        if (locale == null){
            locale = getSessionLocale(request);
        }

        // check if locale is in cookie
        if (locale == null){
            locale = getCookieLocale(request, response);
        }

        // check the browser locale
        if (locale == null){
            locale = getRequestLocale(request);
        }

        // set the default locale
        if (locale == null) {
            locale = getDefaultLocale(isAdmin);
        }

        request.setAttribute(UrlLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME, locale);

        chain.doFilter(request, response);
    }

    private boolean isredirectablePath(String requestURI) {
        if(requestURI.startsWith("/logout")
            || requestURI.startsWith("/download")
        ) { return false; }
        return true;
    }

    private Matcher matchLocalePattern(String requestURI) {

        Pattern localePattern;
        localePattern = appConfig.isIsoTwoLetter() ? LOCALE_PATTERN_ISO_639_1 : LOCALE_PATTERN_LOCALE;
        final Matcher matcher = localePattern.matcher(requestURI) ;
        if (matcher.find()) {
            return matcher;
        } else {
            log.debug("Could not match {} against {}", localePattern, requestURI);
        }

        return null;
    }

    private Locale getRequestLocale(HttpServletRequest request) {
        return (langMap.containsKey(request.getLocale().toString())) ? request.getLocale() : null;
    }

    private Locale getSessionLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null){
            Object langSession = session.getAttribute(LANG_PARAM_NAME);
            if(langSession != null){
                String langStr = (String)langSession;
                if(langMap.containsKey(langStr)){
                    return LocaleUtils.toLocale(langStr);
                }
            }
        }
        return null;
    }

    private Locale getDefaultLocale(boolean isAdmin){
        Locale locale;
        if (isAdmin) {
            locale = LocaleUtils.toLocale(appConfig.getDefaultAdminLang().getLocale());
        } else {
            locale = LocaleUtils.toLocale(appConfig.getDefaultPublicLang().getLocale());
        }
        return locale;
    }

    private String getDefaultLocaleUrlString(boolean isAdmin){
        Locale locale = getDefaultLocale(isAdmin);
        return appConfig.isIsoTwoLetter() ? locale.getLanguage() : locale.toString();
    }

    private Locale getCookieLocale(HttpServletRequest request, HttpServletResponse response) {
        // Retrieve and parse cookie value.
        Cookie cookie = WebUtils.getCookie(request, UrlLocaleResolver.DEFAULT_COOKIE_NAME);
        if (cookie != null) {
            Locale l = StringUtils.parseLocaleString(cookie.getValue());
            if (langMap.containsKey(l.toString()))
                return l;
            // remove wrong cookie
            cookie.setPath("/");
            cookie.setValue(null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return null;
    }

    private boolean skipDispatch(HttpServletRequest request) {
        if (isAsyncDispatch(request)) {
            return true;
        }
        if (request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) != null) {
            return true;
        }
        return false;
    }

    /**
     * The dispatcher type {@code javax.servlet.DispatcherType.ASYNC} introduced
     * in Servlet 3.0 means a filter can be invoked in more than one thread over
     * the course of a single request. This method returns {@code true} if the
     * filter is currently executing within an asynchronous dispatch.
     * @param request the current request
     * @since 3.2
     * @see WebAsyncManager#hasConcurrentResult()
     */
    protected boolean isAsyncDispatch(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager(request).hasConcurrentResult();
    }

}