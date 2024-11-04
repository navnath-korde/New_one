package com.dsa360.api.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dsa360.api.constants.ErrorMessage;
import com.dsa360.api.exceptions.SomethingWentWrongException;

@Service
public class FileStorageUtility {
	Logger logger = LoggerFactory.getLogger(FileStorageUtility.class);

	private final Path rootLocation;
	@Value("${file.upload}")
	private String uploadDir;

	@Autowired
	public FileStorageUtility(@Value("${file.upload}") String uploadDir) {
		this.uploadDir = uploadDir;
		this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
	}

	public List<Path> storeFiles(String dsaApplicationId, MultipartFile... files) {
		Path targetDir = this.rootLocation.resolve(dsaApplicationId);
		List<Path> storedFilePaths = new ArrayList<>();

		try {
			// Delete the directory if it exists; re submit
			if (Files.exists(targetDir)) {
				try (Stream<Path> paths = Files.walk(targetDir).sorted(Comparator.reverseOrder())) {
					paths.map(Path::toFile).forEach(File::delete);
				} catch (IOException e) {
					logger.error(ErrorMessage.DELETE_FILE_ERROR.getValue());
					throw new SomethingWentWrongException(ErrorMessage.DELETE_FILE_ERROR.getValue());
				}
			}

			// Create a new directory
			Files.createDirectories(targetDir);

			// Save each file
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				if (fileName != null && fileName.contains("..")) {
					throw new SomethingWentWrongException("Invalid path sequence in file name: " + fileName);
				}

				Path targetPath = targetDir.resolve(fileName).normalize();
				Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				storedFilePaths.add(targetPath);
			}
		} catch (IOException e) {
			// Rollback if any error occurs
			for (Path path : storedFilePaths) {
				try {
					Files.deleteIfExists(path);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			logger.error("Failed to store files. Transaction rolled back:  {}",e);
			throw new SomethingWentWrongException("Failed to store files. Transaction rolled back.", e);
		}
		return storedFilePaths;
	}
}
