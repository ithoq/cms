package be.ttime.core.filter;

import be.ttime.core.handler.UrlLocaleResolver;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public static final String ALREADY_FILTERED_ATTRIBUTE = ".FILTERED";
    /**
     * Default name of the locale specification parameter: "locale".
     */
    private static final Pattern localePattern = Pattern.compile("^/([a-z]{2}(?:_[A-Z]{2})?)\\b");
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private LocaleResolver localeResolver;
    private ServletContext sc;
    private Map<String, Locale> langMap;
    private ApplicationContext appContext;
    private IApplicationService appService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sc = filterConfig.getServletContext();
        appContext = WebApplicationContextUtils.getWebApplicationContext(sc);
        appService = (IApplicationService) appContext.getBean("applicationService");
        localeResolver = (LocaleResolver) appContext.getBean("localeResolver");
    }

    @Override
    public void destroy() {

    }

    /**
     * Force only one filter per request
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
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
        } else {
            // Do invoke this filter...
            request.setAttribute(ALREADY_FILTERED_ATTRIBUTE, Boolean.TRUE);
            try {
                doFilterInternal(httpRequest, httpResponse, filterChain);
            } finally {
                // Remove the "already filtered" request attribute for this request.
                request.removeAttribute(ALREADY_FILTERED_ATTRIBUTE);
            }
        }
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        Locale locale = null;
        final String requestURI = request.getRequestURI();

        // remove resources URL
        if (requestURI.startsWith("/resources/") ||
                requestURI.equals("/favicon.ico") ||
                requestURI.startsWith("/public/") ||
                requestURI.startsWith("/download/")){
            chain.doFilter(request, response);
            return;
        }

        // Check if it's an administration URL
        boolean isAdmin = requestURI.startsWith("/admin/") || requestURI.equals("/admin");
        langMap = (isAdmin ? appService.getAdminlanguagesMap() : appService.getSiteLanguagesMap());

        // Check the lang parameter
        String lang = request.getParameter("lang");
        if (lang != null) {
            if (langMap.containsKey(lang)) {
                locale = langMap.get(lang);
                localeResolver.setLocale(request, response, locale);
            }
        }

        // Check if locale is in the url
        if (appService.getApplicationConfig().isForcedLangInUrl() &&  !langMap.isEmpty() && requestURI != null && !isAdmin && "GET".equals(request.getMethod())) {

            // Check if locale is in the url
            Matcher matcher = matchLocalePattern(requestURI);
            String urlLocale = (matcher != null) ? matcher.group(1) : null;
            if (urlLocale != null && langMap.containsKey(urlLocale)) {
                request.setAttribute(UrlLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME, langMap.get(urlLocale));
            } else {
                // redirection
                if (appService.getApplicationConfig().isForcedLangInUrl() && !CmsUtils.isAjax(request) && isredirectablePath(requestURI)) {

                    String localeUrl = (locale != null) ? locale.toString() : getDefaultLocaleUrlString(isAdmin);
                    String redirectUrl = "/" + localeUrl + (requestURI.equals("/") ? "" : requestURI);
                    redirectStrategy.sendRedirect(request, response, redirectUrl);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isredirectablePath(String requestURI) {
        if (requestURI.startsWith("/logout")
                || requestURI.startsWith("/download")
                ) {
            return false;
        }
        return true;
    }

    private Matcher matchLocalePattern(String requestURI) {

        final Matcher matcher = localePattern.matcher(requestURI);
        if (matcher.find()) {
            return matcher;
        } else {
            log.debug("Could not match {} against {}", localePattern, requestURI);
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
     *
     * @param request the current request
     * @see WebAsyncManager#hasConcurrentResult()
     * @since 3.2
     */
    protected boolean isAsyncDispatch(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager(request).hasConcurrentResult();
    }

    private String getDefaultLocaleUrlString(boolean isAdmin) {
        return determineDefaultLocale(isAdmin).toString();
    }

    private Locale determineDefaultLocale(boolean isAdmin) {
        Locale locale;
        if (isAdmin) {
            locale = LocaleUtils.toLocale(appService.getApplicationConfig().getDefaultAdminLang().getLocale());
        } else {
            locale = LocaleUtils.toLocale(appService.getApplicationConfig().getDefaultPublicLang().getLocale());
        }
        return locale;
    }
}
