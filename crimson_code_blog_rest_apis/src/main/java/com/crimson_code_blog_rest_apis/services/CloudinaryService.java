package com.crimson_code_blog_rest_apis.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

	String uploadFile (MultipartFile file, String folder, String publicId) throws IOException;
	boolean deleteFile (String publicId);
}
