package be.ttime.core.controller;

import be.ttime.core.model.RedirectMessage;
import be.ttime.core.model.form.InstallCmsForm;
import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/admin/backinfo")
@PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
@Slf4j
public class AdminBackinfoController {

    // 1,23 mb par 1000 adminContent
    private final static String VIEWPATH = "admin/backinfo/";

    @Autowired
    private net.sf.ehcache.CacheManager cacheManager;

    @Autowired
    private IApplicationService applicationService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {

        List<Locale> result = new ArrayList<>();

        Locale[] locales = Arrays.copyOfRange(Locale.getAvailableLocales(), 1, Locale.getAvailableLocales().length);
        Arrays.sort(locales, (l1, l2) -> l1.getDisplayName().compareTo(l2.getDisplayName()));
        // remove special locale
        for (Locale locale : locales) {
            if(locale.toString().length() <= 5){
                result.add(locale);
            }
        }

        /* Total number of processors or cores available to the JVM */
        //System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " + Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */


        /* Maximum amount of memory the JVM will attempt to use */
       // System.out.println("Maximum memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        //System.out.println("Total memory (bytes): " + Runtime.getRuntime().totalMemory());

        model.put("processors", Runtime.getRuntime().availableProcessors());
        long allocatedMemory      = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        model.put("totalMemory", FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory()));
        model.put("maxMemory", FileUtils.byteCountToDisplaySize(Runtime.getRuntime().maxMemory()));
        model.put("allocatedMemory", FileUtils.byteCountToDisplaySize(allocatedMemory));
        model.put("presumableFreeMemory", FileUtils.byteCountToDisplaySize(Runtime.getRuntime().maxMemory() - allocatedMemory));

        /* Get a list of all filesystem roots on this system */
        long userfolderSize = CmsUtils.size(new File("webdata/upload/public/user").toPath());
        model.put("userFolderSize", FileUtils.byteCountToDisplaySize(userfolderSize));
        model.put("redirectMessage", redirectMessage);

        model.put("locales", result);
        model.put("defaultAdminLang", applicationService.getDefaultAdminLang());
        model.put("defaultSiteLang", applicationService.getDefaultSiteLang());
        model.put("adminLocales", applicationService.getAdminlanguages());
        model.put("appConfig", applicationService.getApplicationConfig());
        model.put("siteLangMap", applicationService.getLanguagesMap());
        //model.put("adminLang", applicationService.getad());

        Configuration cacheConfig = cacheManager.getConfiguration();
        long maxCacheBytes = cacheConfig.getMaxBytesLocalHeap();
        long usedCacheBytes = getCacheManagerUsage();
        model.put("cacheTotalSize", FileUtils.byteCountToDisplaySize(maxCacheBytes));
        model.put("cacheTotalUsedSize", FileUtils.byteCountToDisplaySize(usedCacheBytes));
        model.put("cacheUsage", getCachePercentageUsage(maxCacheBytes, usedCacheBytes));

        return VIEWPATH + "home";
    }


    private String getCachePercentageUsage(long maxCache, long usedCache){
        double usageInpercent = ((double)usedCache * 100) / maxCache;
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(usageInpercent);
    }
    private long getCacheManagerUsage(){
        long totalUsage = 0;
        for (String cacheName : cacheManager.getCacheNames()) {
            totalUsage += cacheManager.getCache(cacheName).getStatistics().getLocalHeapSizeInBytes();
        }
        return totalUsage;
    }


    @RequestMapping(value = "/clearCache/", method = RequestMethod.DELETE)
    @ResponseBody
    public String clearAllCache(ModelMap model) {

        cacheManager.clearAll();
        return "{ data : \"\" }";
    }

    @RequestMapping(value = "/clearCache/{name}", method = RequestMethod.DELETE)
    @ResponseBody
    public String clearCache(ModelMap model, @PathVariable("name") String name) {

        Cache cache = cacheManager.getCache(name);
        if(cache == null){
            return "{ error : \"cache not found\" }";
        } else {
            cache.removeAll();
        }

        return "{ data : \"\" }";
    }

    @RequestMapping(value = "/cacheJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {


        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()

        for (String cacheName : cacheManager.getCacheNames()) {

            Cache cache = cacheManager.getCache(cacheName);
            StatisticsGateway statistics = cache.getStatistics();

            long maxBytesLocalHeap = cache.getCacheConfiguration().getMaxBytesLocalHeap();
            long localHeapSize = statistics.getLocalHeapSize();
            long localHeapSizeInBytes = statistics.getLocalHeapSizeInBytes();
            String used =  FileUtils.byteCountToDisplaySize(localHeapSizeInBytes);
            row = Json.createObjectBuilder();

            String usageString = FileUtils.byteCountToDisplaySize(localHeapSizeInBytes) + " (" + localHeapSize  + " items)";

            row.add("DT_RowData", Json.createObjectBuilder().add("id", cacheName));
            row.add("name",cacheName);
            row.add("size", maxBytesLocalHeap == 0 ? "/" :  FileUtils.byteCountToDisplaySize(maxBytesLocalHeap));
            row.add("percent", maxBytesLocalHeap == 0 ? "/" :  getCachePercentageUsage(maxBytesLocalHeap, localHeapSize) + "%");
            row.add("used", usageString);
            /*row.add("type", CmsUtils.emptyStringIfnull(block.getBlockType().getName()));
            row.add("dynamic", block.isDynamic());
            row.add("deletable", block.isDeletable());*/
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @RequestMapping(value = "/appConfig", method = RequestMethod.POST)
    public String config(@Valid InstallCmsForm form, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {


        Map<String, ApplicationLanguageEntity> langMap = applicationService.getApplicationLanguagesMap();

        // Admin Lang
        ApplicationLanguageEntity adminLang = langMap.get(form.getAdminLocale());

        if(adminLang == null) {
            throw new Exception("The administration locale " + form.getAdminLocale() + " not exist");
        }

        ApplicationConfigEntity appConfig = applicationService.getApplicationConfig();

        adminLang.setEnabledForAdmin(true);
        applicationService.saveApplicationLanguage(adminLang);

        // Extra Lang
        if(form.getExtraLocale() != null) {
            List<ApplicationLanguageEntity> extraLang =
                    form.getExtraLocale()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(s -> !s.equals(form.getSiteLocale()))
                            .map(this::publicLanguage)
                            .collect(Collectors.toList());
            applicationService.saveApplicationLanguage(extraLang);
        }
        appConfig.setSiteName(form.getSitename());
        appConfig.setUrl(form.getUrl());
        appConfig.setSeoDescription(form.getDescription());
        appConfig.setDefaultAdminLang(adminLang);
        appConfig.setAlreadyInstall(true);
        appConfig.setMaintenance(form.isMaintenance());
        appConfig.setForcedLangInUrl(form.isLangInUrl());
        appConfig.setUseMember(form.isUseMember());
        applicationService.saveApplicationConfig(appConfig);

        RedirectMessage redirectMessage = new RedirectMessage();
        redirectMessage.setType(RedirectMessage.SUCCESS);
        redirectMessage.setMessage("Application config saved succesfully");
        redirectAttributes.addFlashAttribute("redirectMessage", redirectMessage);

        return "redirect:/admin/backinfo";
    }

    private ApplicationLanguageEntity publicLanguage(String locale){
        ApplicationLanguageEntity lang = applicationService.getApplicationLanguagesMap().get(locale);
        if(lang == null){
            lang = new ApplicationLanguageEntity();
            lang.setLocale(locale);
        }
        lang.setEnabledForPublic(true);
        return lang;
    }

}
