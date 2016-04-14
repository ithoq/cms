package be.ttime.core.handler;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

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
     * <p>Only used for overriding a cookie value if the locale has been
     * changed in the course of the current request! Use
     * {@link org.springframework.web.servlet.support.RequestContext#getLocale}
     * to retrieve the current locale in controllers or views.
     *
     * @see org.springframework.web.servlet.support.RequestContext#getLocale
     */
    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = UrlLocaleResolver.class.getName() + ".LOCALE";

    /**
     * The default cookie name used if none is explicitly set.
     */
    public static final String DEFAULT_COOKIE_NAME = "lang"; //UrlLocaleResolver.class.getNames() + ".LOCALE";


    private Locale defaultLocale;


    /**
     * Creates a new instance of the {@link CookieLocaleResolver} class
     * using the {@link #DEFAULT_COOKIE_NAME default cookie name}.
     */
    public UrlLocaleResolver() {
        setCookieName(DEFAULT_COOKIE_NAME);
    }

    /**
     * Return the fixed Locale that this resolver will return if no cookie found,
     * if any.
     */
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    /**
     * Set a fixed Locale that this resolver will return if no cookie found.
     */
    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Locale resolveLocale(HttpServletRequest request) {
        // Check request for pre-parsed or preset locale.
        Locale locale = (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (locale != null) {
            return locale;
        }

        return determineDefaultLocale(request);
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale != null) {
            // Set request attribute and add cookie.
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
            addCookie(response, locale.toString());
        } else {
            // Set request attribute to fallback locale and remove cookie.
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, determineDefaultLocale(request));
            removeCookie(response);
        }
    }

    /**
     * Determine the default locale for the given request,
     * Called if no locale cookie has been found.
     * <p>The default implementation returns the specified default locale,
     * if any, else falls back to the request's accept-header locale.
     *
     * @param request the request to resolve the locale for
     * @return the default locale (never <code>null</code>)
     * @see #setDefaultLocale
     * @see javax.servlet.http.HttpServletRequest#getLocale()
     */
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
        return defaultLocale;
    }
}