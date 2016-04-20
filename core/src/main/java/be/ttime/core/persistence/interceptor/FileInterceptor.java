package be.ttime.core.persistence.interceptor;

import be.ttime.core.persistence.model.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Delete Physical file when we delete a file in DB
 */
@Slf4j
public class FileInterceptor extends EmptyInterceptor {

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
                log.error("Error deleting file : " + file.getName() + " located at : " + filepath + file.getServerName() + " - Cause : " + e);
            }
        }

    }
}