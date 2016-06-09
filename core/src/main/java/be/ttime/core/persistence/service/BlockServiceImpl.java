package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.repository.IBlockRepository;
import be.ttime.core.util.PebbleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;

@Service
@Transactional
@Slf4j
public class BlockServiceImpl implements IBlockService {

    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private IBlockRepository blockRepository;

    @Override
    @Cacheable(value = "block", key = "#name")
    public BlockEntity find(String name) {

        return blockRepository.findOne(name);
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
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#block.name"),
            @CacheEvict(value = "blockCompiledTemplate", key = "#block.name"),
            @CacheEvict(value = "blockJson", allEntries = true),
    })
    public BlockEntity save(BlockEntity block) {
        return blockRepository.save(block);
    }


    @Override
    public List<BlockEntity> save(List<BlockEntity> blocks) {

        for (BlockEntity block : blocks) {
            save(block);
        }
        return blocks;
    }


    @Override
    public List<BlockEntity> findAll() {
        return blockRepository.findAll();
    }

    @Override
    @Cacheable(value = "blockJson")
    public String jsonBlockArray(List<String> types) {

        List<BlockEntity> blocks = null;
        if(types == null){
            blocks = blockRepository.findAll();
        } else{
            blocks = blockRepository.findAllByBlockTypeNameIn(types);
        }
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