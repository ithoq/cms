package be.ttime.core.controller;

import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IMessageService;
import be.ttime.core.persistence.service.IUserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

@Controller
public class TestController {

    @Autowired
    private IContentService pageService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/en/resetAdmin", method = RequestMethod.GET)
    @ResponseBody
    public String testCsrf(ModelMap model, HttpServletRequest request, Locale localeRequest) throws Exception {

        UserEntity user = userService.findByUsernameOrEmail("fcipolla@ttime.be");
        if(user == null)
            throw new Exception("ADMIN NOT EXIST");

        user.setPassword(bCryptPasswordEncoder.encode("mrshink,1532"));
        userService.save(user);

        return "Admin reset";
    }

    @RequestMapping(value = "/en/monitor", method = RequestMethod.GET)
    @ResponseBody
    public String testScrfPost(ModelMap model, HttpServletRequest request, Locale localeRequest) {

        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + FileUtils.byteCountToDisplaySize(freeMemory) + "<br/>");
        sb.append("allocated memory: " + FileUtils.byteCountToDisplaySize(allocatedMemory) + "<br/>");
        sb.append("max memory: " + FileUtils.byteCountToDisplaySize(maxMemory) + "<br/>");
        sb.append("total free memory: " + FileUtils.byteCountToDisplaySize(freeMemory + (maxMemory - allocatedMemory)) + "<br/>");



        // Total number of processors or cores available to the JVM
        System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

        // Total amount of free memory available to the JVM
        System.out.println("Free memory (Kb): " + FileUtils.byteCountToDisplaySize(Runtime.getRuntime().freeMemory()));

        // This will return Long.MAX_VALUE if there is no preset limit
        maxMemory = Runtime.getRuntime().maxMemory();
        // Maximum amount of memory the JVM will attempt to use
        System.out.println("Maximum memory (Kb): " + (maxMemory == Long.MAX_VALUE ? "no limit" : FileUtils.byteCountToDisplaySize(maxMemory)));

        // Total memory currently in use by the JVM
        System.out.println("Total memory (Kb): " + FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory()));

        // Get a list of all filesystem roots on this system
        File[] roots = File.listRoots();

        // For each filesystem root, print some info
        for (File root : roots) {
            System.out.println("File system root: " + root.getAbsolutePath());
            System.out.println("Total space (kb): " + FileUtils.byteCountToDisplaySize(root.getTotalSpace()));
            System.out.println("Free space (kb): " + FileUtils.byteCountToDisplaySize(root.getFreeSpace()));
            System.out.println("Usable space (kb): " + FileUtils.byteCountToDisplaySize(root.getUsableSpace()));
        }

        return sb.toString();
    }
}