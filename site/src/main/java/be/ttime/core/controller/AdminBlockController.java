package be.ttime.core.controller;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.BlockTypeEntity;
import be.ttime.core.persistence.repository.IBlockTypeRepository;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.CmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/block")
@PreAuthorize("hasRole('ROLE_ADMIN_BLOCK')")
public class AdminBlockController {

    private final static String VIEWPATH = "admin/block/";

    @Autowired
    private IBlockService pageBlockRepository;

    @Autowired
    private IBlockTypeRepository blockTypeRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {

        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public void deleteBlock(@PathVariable("id") String id, HttpServletResponse response) {

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
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public void addBlock(BlockEntity block, HttpServletResponse response) {
        block.setBlockType(new BlockTypeEntity("CONTENT"));
        pageBlockRepository.save(block);
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return (CmsUtils.isSuperAdmin())? pageBlockRepository.jsonBlockArray("all", true) : pageBlockRepository.jsonBlockArray("CONTENT", false);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getBlock(@PathVariable("id") String id, HttpServletResponse response) {

        BlockEntity block = pageBlockRepository.find(id);
        if (block == null) {
            response.setStatus(500);
            return null;
        }
        BlockEntity e = pageBlockRepository.find(id);
        Map<String,Object> result = new HashMap<>();
        result.put("blockType", e.getBlockType().getName());
        result.put("dynamic", e.isDynamic());
        result.put("content", e.getContent());
        result.put("name", e.getName());
        return result;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String setBlock(String name, String content, String blockType, HttpServletResponse response) throws Exception {

        BlockEntity page = pageBlockRepository.find(name);

        BlockTypeEntity blockTypeEntity = blockTypeRepository.findOne(blockType);

        if(blockTypeEntity == null){
            throw new Exception("blockType " + blockType + " not found!");
        }


        if (page == null || StringUtils.isEmpty(name)) {
            response.setStatus(200);
            return null;
        }
        page.setName(name);
        page.setContent(content);
        //if (page.getBlockType() != PageBlockEntity.BlockType.System
        //       && page.getBlockType() != PageBlockEntity.BlockType.PageTemplate
        //        && page.getBlockType() != PageBlockEntity.BlockType.FieldSet) {
            page.setBlockType(blockTypeEntity);
        //}
        pageBlockRepository.save(page);
        return "OK";
    }

    @RequestMapping(value = "/toggle/dynamic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BlockEntity setType(String id, HttpServletResponse response) {

        BlockEntity block = pageBlockRepository.find(id);
        if (block == null) {
            response.setStatus(200);
            return null;
        }

        block.setDynamic(!block.isDynamic());
        pageBlockRepository.save(block);
        return block;
    }
}
