package be.ttime.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
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
public class ForceLocalUrlFilter extends OncePerRequestFilter {

    /**
     * Default name of the locale specification parameter: "locale".
     */

    private static final Pattern DEFAULT_LOCALE_URL_PATTERN = Pattern.compile("^/([a-z]{2})\\b");
    // ISO 639-1: two-letter codes, one per language
    private static final boolean DEFAULT_ISO_FORMAT_ISO_639_1 = true;
    private Pattern localePattern = DEFAULT_LOCALE_URL_PATTERN;
    private boolean useIso639 = DEFAULT_ISO_FORMAT_ISO_639_1;
    private Map<String, Locale> listSupportedLocale = new HashMap<>();
    private Locale defaultLocale;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public ForceLocalUrlFilter() {
        if (!useIso639) {
            localePattern = Pattern.compile("^/([a-z]{2,3}_[A-Z]{2})\\b");
        }
        // TODO : User applicationLanguages instead
        listSupportedLocale.put("fr", new Locale("fr"));
        listSupportedLocale.put("en", new Locale("en"));
        defaultLocale = new Locale("en");
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public boolean isUseIso639() {
        return useIso639;
    }

    public void setUseIso639(boolean useIso639) {
        this.useIso639 = useIso639;
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Locale locale = null;
        final String requestURI = request.getServletPath();
        boolean isFile = false;
        try {
            char extension3letter = requestURI.charAt(requestURI.length() - 4);
            char extension2letter = requestURI.charAt(requestURI.length() - 4);
            if (extension2letter == '.' || extension3letter == '.') isFile = true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        if (requestURI != null && !isFile) {

            // Locale explicitly expressed in the URL has precedence
            Matcher matcher = matchLocalePattern(requestURI);

            String UrlLocale = (matcher != null) ? matcher.group(1) : null;
            locale = (UrlLocale != null) ? StringUtils.parseLocaleString(UrlLocale) : null;

            if (locale == null || !listSupportedLocale.containsKey(locale.toString())) {
                // Need a redirection (lang not in URL)

                // check local in cookie
                locale = getCockieLocale(request, response);
                // check local in request (browser)
                if (locale == null) locale = getRequestLocale(request);
                // set default locale
                if (locale == null) locale = defaultLocale;

                // Redirection if is not ajax
                if (!isAjax(request)) {
                    String b = "/" +
                            locale.toString() +
                            requestURI;

                    redirectStrategy.sendRedirect(request, response, b);
                    return;
                }
            }
        }
        request.setAttribute(UrlLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
        filterChain.doFilter(request, response);
    }

    private Matcher matchLocalePattern(String requestURI) {

        if (requestURI != null) {
            final Matcher matcher = localePattern.matcher(requestURI);
            if (matcher.find()) {
                return matcher;
            } else {
                log.debug("Could not match {} against {}", localePattern, requestURI);
            }
        }
        return null;
    }

    private Locale getRequestLocale(HttpServletRequest request) {

        if (useIso639)
            return (listSupportedLocale.containsKey(request.getLocale().getLanguage())) ? new Locale(request.getLocale().getLanguage()) : null;

        return (listSupportedLocale.containsKey(request.getLocale().toString())) ? request.getLocale() : null;


    }

    private Locale getCockieLocale(HttpServletRequest request, HttpServletResponse response) {
        // Retrieve and parse cookie value.
        Cookie cookie = WebUtils.getCookie(request, UrlLocaleResolver.DEFAULT_COOKIE_NAME);
        if (cookie != null) {
            Locale l = StringUtils.parseLocaleString(cookie.getValue());
            if (listSupportedLocale.containsKey(l.toString()))
                return l;
            // remove wrong cookie
            cookie.setPath("/");
            cookie.setValue(null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return null;
    }
}