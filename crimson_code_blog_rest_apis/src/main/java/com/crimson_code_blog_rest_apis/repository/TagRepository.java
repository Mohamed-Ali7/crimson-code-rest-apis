package com.crimson_code_blog_rest_apis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crimson_code_blog_rest_apis.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

	Optional<TagEntity> findByNameIgnoreCase(String name);
	@Query("SELECT tag FROM TagEntity tag WHERE LOWER(tag.name) IN :tagNames")
	List<TagEntity> findAllByNameIn(@Param("tagNames") List<String> tagNames);
}
