package com.bezkoder.springjwt.utils;

import com.bezkoder.springjwt.errors.GenericCustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceUtil {

    private static final Logger logger = LogManager.getLogger(FileServiceUtil.class);

    @Value("${payout.fileupload.path}")
    private String uploadPath;

//    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            logger.error("Could not create upload folder ", e);
            throw new GenericCustomException("Could not create upload folder ", new Date());
        }
    }

    public Pair<String, String> save(MultipartFile file) {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }
            String filename = new Date().getTime() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            logger.info(" Payout file saved successfully on defined upload file path ");
            return new Pair<>(root.toString(), filename);
        } catch (Exception e) {
            logger.error("Could not store the payout file. Error: ", e);
            throw new GenericCustomException("Could not store the payout file. Error: ", new Date());
        }
    }

    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                logger.error("Could not read the payout file!");
                throw new GenericCustomException("Could not read the payout file!", new Date());
            }
        } catch (MalformedURLException e) {
            logger.error("Error while loading payout file: ", e);
            throw new GenericCustomException("Error while loading payout file: ", new Date());
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(uploadPath).toFile());
    }

    public List<Path> loadAll() {
        try {
            Path root = Paths.get(uploadPath);
            if (Files.exists(root)) {
                return Files.walk(root, 1)
                        .filter(path -> !path.equals(root))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            logger.error("Could not list the files! ", e);
            throw new GenericCustomException("Could not list the files! ", new Date());
        }
    }
}