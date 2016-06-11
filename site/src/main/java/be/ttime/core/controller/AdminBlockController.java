package be.ttime.core.controller;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.BlockTypeEntity;
import be.ttime.core.persistence.repository.IBlockTypeRepository;
import be.ttime.core.persistence.service.IBlockService;
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
    private IBlockService pageBlockRepository;

    @Autowired
    private IBlockTypeRepository blockTypeRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {

        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
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
    public void addBlock(BlockEntity block, HttpServletResponse response) {
        block.setBlockType(new BlockTypeEntity("CONTENT"));
        pageBlockRepository.save(block);
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return pageBlockRepository.jsonBlockArray("all");
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BlockEntity getBlock(@PathVariable("id") String id, HttpServletResponse response) {

        BlockEntity block = pageBlockRepository.find(id);
        if (block == null) {
            response.setStatus(500);
            return null;
        }
        return pageBlockRepository.find(id);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String setBlock(String id, String name, String content, String blockType, boolean cacheable, HttpServletResponse response) throws Exception {

        BlockEntity page = pageBlockRepository.find(id);

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
