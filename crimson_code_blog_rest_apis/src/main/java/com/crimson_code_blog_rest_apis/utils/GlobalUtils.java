package com.crimson_code_blog_rest_apis.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;

public class GlobalUtils {
	
	public static void saveImage(MultipartFile file, String fileName, String uploadDir) {
		Path uploadPath = Paths.get("uploads/" + uploadDir + fileName);
	
		if (!List.of("image/jpeg", "image/png", "image/webp").contains(file.getContentType()))  {
			throw new CrimsonCodeGlobalException("Invalid content type.");
		}

		try {
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException e) {
			throw new CrimsonCodeGlobalException("Failed to store the picture");
		}
	}
}
