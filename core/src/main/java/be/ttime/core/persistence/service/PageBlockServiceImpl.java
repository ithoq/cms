package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PageBlockEntity;
import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.repository.IPageBlockRepository;
import be.ttime.core.util.PebbleUtils;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class PageBlockServiceImpl implements IPageBlockService {

    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private IPageBlockRepository pageBlockRepository;

    @Override
    @Cacheable(value = "block", key = "#id")
    public PageBlockEntity find(Long id) {

        return pageBlockRepository.findOne(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#id"),
            @CacheEvict(value = "block", key = "'json'"),
            @CacheEvict(value = "blockByName", allEntries = true),
            @CacheEvict(value = "renderedBlock", key = "#id"),
            @CacheEvict(value = "navBlock", key = "#id"),
    })
    public void delete(Long id) throws Exception {
        PageBlockEntity block = pageBlockRepository.findOne(id);
        if (block == null) {
            throw new Exception("Block with id " + id + " is not found!");
        } else if (!block.isDeletable()) {
            String message = "Block with id \" + id + \" is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        pageBlockRepository.delete(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#block.id"),
            @CacheEvict(value = "block", key = "'json'"),
            @CacheEvict(value = "blockByName", allEntries = true),
            @CacheEvict(value = "renderedBlock", key = "#block.id"),
            @CacheEvict(value = "navBlock", key = "#block.id"),
    })
    public PageBlockEntity save(PageBlockEntity block) {
        return pageBlockRepository.save(block);
    }

    @Override
    public List<PageBlockEntity> save(List<PageBlockEntity> blocks) {
        return pageBlockRepository.save(blocks);
    }

    @Override
    public List<PageBlockEntity> findAll() {
        return pageBlockRepository.findAll();
    }


    @Override
    public String render(Long id) throws Exception {

        PageBlockEntity block = pageBlockRepository.findOne(id);
        if (block == null) {
            throw new Exception("Block not found, with id : " + id);
        }
        if (block.getBlockType() == PageBlockEntity.BlockType.Navigation) {
            return renderNavigationBlock(block);
        } else {
            if (block.isDynamic()) {
                if (block.isCacheable()) {
                    return renderCachableBlock(block);
                } else {
                    return renderUncacheableBlock(block);
                }
            } else {
                return block.getContent();
            }
        }
    }

    @Cacheable(value = "renderedBlock", key = "#block.id")
    private String renderCachableBlock(PageBlockEntity block) throws IOException, PebbleException {
        Map<String, Object> map = new HashMap<>();
        return pebbleUtils.parseBlock(block, map);
    }

    @Cacheable(value = "renderedBlock", key = "#block.id")
    private String renderNavigationBlock(PageBlockEntity block) throws IOException, PebbleException {
        Map<String, Object> map = new HashMap<>();
        return pebbleUtils.parseBlock(block, map);

    }

    @Cacheable(value = "navBlock", key = "#block.id")
    private String renderUncacheableBlock(PageBlockEntity block) throws IOException, PebbleException {
        Map<String, Object> map = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserEntity custom = (UserEntity) authentication.getPrincipal();
            map.put("user", custom);
        }

        return pebbleUtils.parseBlock(block, map);
    }

    /**
     * Used to cache the compiledTemplate for uncacheable block
     *
     * @param block - a uncachaeble block to be rendered
     * @return
     * @throws PebbleException
     */
    @Cacheable(value = "renderedBlock", key = "#block.id")
    private PebbleTemplate getUncachableBlockCompiledTemplate(PageBlockEntity block) throws PebbleException {
        return pebbleUtils.getCompiledTemplate(block.getContent());
    }


    @Override
    @Cacheable(value = "block", key = "'json'")
    public String jsonBlockArray() {

        List<PageBlockEntity> blocks = pageBlockRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (PageBlockEntity block : blocks) {
            row = Json.createObjectBuilder();
            //row.add("DT_RowId", "x"); // add an id
            //row.add("DT_RowClass", "x"); // add a class
            row.add("DT_RowData", Json.createObjectBuilder().add("id", block.getId()));
            row.add("name", block.getName());
            row.add("type", block.getBlockType().toString());
            row.add("dynamic", block.isDynamic());
            row.add("cacheable", block.isCacheable());
            row.add("deletable", block.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @Override
    @Cacheable(value = "blockByName", key = "#name")
    public PageBlockEntity findByNameAndBlockType(String name, PageBlockEntity.BlockType type) {
        return pageBlockRepository.findByNameAndBlockType(name, type);
    }
}