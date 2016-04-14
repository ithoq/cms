package be.ttime.core.controller;

import be.ttime.core.persistence.model.PageBlockEntity;
import be.ttime.core.persistence.service.IPageBlockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/admin/block")
public class AdminBlockController {

    private final static String VIEWPATH = "admin/block/";

    @Autowired
    private IPageBlockService pageBlockRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {

        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteBlock(@PathVariable("id") Long id, HttpServletResponse response) {

        if (id == null) {
            response.setStatus(500);
        }
        try {
            pageBlockRepository.delete(id);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public void addBlock(PageBlockEntity block, HttpServletResponse response) {
        pageBlockRepository.save(block);
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String deleteBlock(HttpServletResponse response) {

        return pageBlockRepository.jsonBlockArray();
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PageBlockEntity getBlock(@PathVariable("id") Long id, HttpServletResponse response) {

        PageBlockEntity block = pageBlockRepository.find(id);
        if (block == null) {
            response.setStatus(500);
            return null;
        }
        return pageBlockRepository.find(id);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String setBlock(Long id, String name, String content, PageBlockEntity.BlockType blockType, boolean cacheable, HttpServletResponse response) {

        PageBlockEntity page = pageBlockRepository.find(id);

        if (page == null || StringUtils.isEmpty(name)) {
            response.setStatus(200);
            return null;
        }
        page.setName(name);
        page.setContent(content);
        if (page.getBlockType() != PageBlockEntity.BlockType.System
                && page.getBlockType() != PageBlockEntity.BlockType.PageTemplate
                && page.getBlockType() != PageBlockEntity.BlockType.FieldSet) {
            page.setBlockType(blockType);
        }
        page.setCacheable(cacheable);
        pageBlockRepository.save(page);
        return "OK";
    }

    @RequestMapping(value = "/toggle/dynamic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PageBlockEntity setType(long id, HttpServletResponse response) {

        PageBlockEntity page = pageBlockRepository.find(id);
        if (page == null) {
            response.setStatus(200);
            return null;
        }

        page.setDynamic(!page.isDynamic());
        pageBlockRepository.save(page);
        return page;
    }
}
