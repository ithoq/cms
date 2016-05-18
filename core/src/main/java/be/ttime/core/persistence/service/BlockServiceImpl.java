package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.repository.IBlockRepository;
import be.ttime.core.util.PebbleUtils;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BlockServiceImpl implements IBlockService {

    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private IBlockRepository blockRepository;

    @Override
    public BlockEntity find(String name) {

        return findByNameAndBlockType(name, null);
    }

    @Override
    //@Cacheable(value = "blockByName", key = "#name")
    public BlockEntity findByNameAndBlockType(String name, String blockTypeName) {
        if(blockTypeName == null) {
            return blockRepository.findOne(name);
        } else {
            return blockRepository.findByNameAndBlockTypeName(name, blockTypeName);
        }

    }

    @Override
    public void delete(String name) throws Exception {
        BlockEntity block = blockRepository.findOne(name);
        if (block == null) {
            throw new Exception("Block with name " + name + " is not found!");
        } else if (!block.isDeletable()) {
            String message = "Block with name \" + name + \" is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        blockRepository.delete(name);
    }

    @Override
    public BlockEntity save(BlockEntity block) {
        return blockRepository.save(block);
    }

    @Override
    public List<BlockEntity> save(List<BlockEntity> blocks) {
        return blockRepository.save(blocks);
    }

    @Override
    public List<BlockEntity> findAll() {
        return blockRepository.findAll();
    }


    @Override
    public String render(String name) throws Exception {

        BlockEntity block = blockRepository.findOne(name);
        if (block == null) {
            throw new Exception("Block not found, with name : " + name);
        }
        if (block.getBlockType().getName().equals("NAVIGATION")) {
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

    private String renderCachableBlock(BlockEntity block) throws IOException, PebbleException {
        Map<String, Object> map = new HashMap<>();
        return pebbleUtils.parseBlock(block, map);
    }

    private String renderNavigationBlock(BlockEntity block) throws IOException, PebbleException {
        Map<String, Object> map = new HashMap<>();
        return pebbleUtils.parseBlock(block, map);

    }

    private String renderUncacheableBlock(BlockEntity block) throws IOException, PebbleException {
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
    private PebbleTemplate getUncachableBlockCompiledTemplate(BlockEntity block) throws PebbleException {
        return pebbleUtils.getCompiledTemplate(block.getContent());
    }


    @Override
    //@Cacheable(value = "block", key = "'json'")
    public String jsonBlockArray() {

        List<BlockEntity> blocks = blockRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (BlockEntity block : blocks) {
            row = Json.createObjectBuilder();
            //row.add("DT_RowId", "x"); // add an name
            //row.add("DT_RowClass", "x"); // add a class
            row.add("DT_RowData", Json.createObjectBuilder().add("id", block.getName()));
            row.add("name", block.getDisplayName());
            row.add("type", block.getBlockType().getName());
            row.add("dynamic", block.isDynamic());
            row.add("cacheable", block.isCacheable());
            row.add("deletable", block.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}