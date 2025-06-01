package com.crimson_code_blog_rest_apis.services.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.crimson_code_blog_rest_apis.exceptions.CrimsonCodeGlobalException;
import com.crimson_code_blog_rest_apis.services.CloudinaryService;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

	Cloudinary cloudinary;

	@Autowired
	public CloudinaryServiceImpl(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	@Override
	public String uploadFile(MultipartFile file, String folder, String publicId) throws IOException {

		Map uploadResult = cloudinary.uploader().upload(
				file.getBytes(),
				ObjectUtils.asMap(
						"asset_folder", "crimson_code/" + folder,
						"public_id", publicId,
						"overwrite", true)
				);

		return uploadResult.get("secure_url").toString();

	}

	@Override
	public boolean deleteFile(String publicId) {
		try {
			Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
			return "ok".equals(result.get("result"));
		} catch (IOException e) {
			return false;
		}
	}
}
