package be.ttime.core.persistence.interceptor;

import be.ttime.core.persistence.dao.FileEntity;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
/**
 * Delete Physical file when we delete a file in DB
 */
public class FileInterceptor extends EmptyInterceptor {

    final static Logger logger = LoggerFactory.getLogger(FileInterceptor.class);
    @Autowired
    private Environment env;

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        String filepath = env.getProperty("page.file.directory");

        if (entity instanceof FileEntity) {
            FileEntity file = (FileEntity) entity;
            Path path = Paths.get(filepath + file.getServerName());
            try {
                Files.delete(path);
            } catch (IOException e) {
                logger.error("Error deleting file : " + file.getName() + " located at : " + filepath + file.getServerName() + " - Cause : " + e);
            }
        }

    }
}