package be.ttime.core.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AdminWebContentForm {

    private Long contentId;
    private Long contentDataId;
    private String contentType;
    private String selectLanguage;
    private String dateBegin;
    private String dateTimeBegin;
    private String dateEnd;
    private String dateTimeEnd;
    private String categories;
    private String tags;
    private String previousFile;
    private MultipartFile thumbnail;
    private String title;
    @NotEmpty
    private String slug;
    private String intro;
    private String content;
    private String devIncludeTop;
    private String devIncludeBot;

    private String seoH1;
    private String seoTag;
    private String seoDescription;

    private boolean contentActive;
}