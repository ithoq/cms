package be.ttime.core.controller;

import be.fabriceci.fmc.util.FileManagerUtils;
import be.ttime.core.model.form.AdminFileUploadForm;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.FileEntity;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IFileService;
import be.ttime.core.util.CmsUtils;
import com.google.common.collect.Iterators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Handles requests for the application file upload requests
 */
@Controller
@Slf4j
@RequestMapping(value = "/admin/file")
public class AdminFileController {

    @Value("${page.file.directory}")
    private String fileDirectory;

    @Autowired
    private IContentService contentService;

    @Autowired
    private Validator validator;

    @Autowired
    private IFileService fileService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN_FILE_DELETE')")
    public String deleteFile(@PathVariable("id") long urlId, HttpServletResponse response) throws Exception {

        if (urlId == 0) {
            response.setStatus(500);
            return "";
        }
        try {
            fileService.delete(urlId);
        } catch (Exception e) {
            response.setStatus(500);
            return "";
        }
        return "OK";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    public String editFile(long id, String name, String description, String group, HttpServletResponse response) throws Exception {

        if (id == 0 || StringUtils.isEmpty(name)) {
            response.setStatus(500);
            return "";
        }

        FileEntity file = fileService.findOne(id);
        file.setName(name);
        file.setDescription(description);
        file.setFileGroup(group);
        fileService.save(file);
        return "OK";

    }

    @RequestMapping(value = "/getJson/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String filesListJson(@PathVariable("id") long urlId, HttpServletResponse response, String type) {

        return fileService.getFilesListJson(urlId, type);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces="application/json")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    public Map<String, String> upload(MultipartHttpServletRequest request, HttpServletResponse response, Locale locale) throws IOException {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("status", "error");
        response.setStatus(500);
        Iterator<String> itr = request.getFileNames();
        int size = Iterators.size(itr);
        if (size > 0) {
            MultipartFile[] files = new MultipartFile[size];
            itr = request.getFileNames(); // reset iterator
            // List<MultipartFile> list = new ArrayList<>();
            int i = 0;
            while (itr.hasNext()) {
                //list.add(request.getFile(itr.next()));
                files[i] = request.getFile(itr.next());
                i++;
            }

            AdminFileUploadForm uploadForm = new AdminFileUploadForm();
            uploadForm.setFiles(files);
            String longId = request.getParameter("contentId");
            Long contentId = Long.parseLong(longId);
            uploadForm.setPageId(contentId);
            Map<String, String> map = new HashMap<>();
            MapBindingResult errors = new MapBindingResult(map, AdminFileUploadForm.class.getName());
            validator.validate(uploadForm, errors);

            // if errors
            if (errors.hasErrors()) {
                response.setStatus(500);
                StringBuilder sb = new StringBuilder();
                if(errors.getErrorCount() > 1){
                    ObjectError error = errors.getAllErrors().get(0);
                    jsonResponse.put("message",  messageSource.getMessage(error.getCode(), null, locale));
                }
                return jsonResponse;
                //return sb.toString();
            }

            List<FileEntity> pageFiles = new ArrayList<>();
            MultipartFile file;
            for (i = 0; i < uploadForm.getFiles().length; i++) {
                file = uploadForm.getFiles()[i];
                if (!file.isEmpty()) {
                    String name = file.getOriginalFilename();
                    String ext = FilenameUtils.getExtension(name);
                    try {

                        // Upload the file
                        File result = CmsUtils.uploadFile(file, true);

                        FileEntity pageFile = new FileEntity();
                        pageFile.setName(FilenameUtils.getName(name));
                        pageFile.setServerName(result.getName());
                        pageFile.setExtension(ext);
                        pageFile.setSize(Math.round(file.getSize()));
                        pageFile.setMimeType(FileManagerUtils.getMimeTypeByExt(ext));
                        if(uploadForm.getPageId() != null){
                            ContentDataEntity c = contentService.findContentData(uploadForm.getPageId());
                            pageFile.setContentDataEntity(c);
                        }

                        pageFile.setFileType(request.getParameter("type"));

                        pageFiles.add(pageFile);
                    } catch (IOException e) {
                        log.error("You failed to upload " + name + " => " + e.getMessage());
                        return jsonResponse;
                    }
                } else {
                    log.info("You failed to upload because the file was empty");
                    jsonResponse.put("message", "empty file");
                    return jsonResponse;
                }
            }

            fileService.save(pageFiles);
            response.setStatus(200);
            jsonResponse.put("status", "success");
            return jsonResponse;
        }
        return jsonResponse;
    }
}
