package com.crimson_code_blog_rest_apis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crimson_code_blog_rest_apis.entity.PostEntity;

import jakarta.transaction.Transactional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

	@EntityGraph(attributePaths = {"user", "category", "tags"})
	Optional<PostEntity> findById(long id);
	
	@Override
	@EntityGraph(attributePaths = {"user", "category", "tags"})
	Page<PostEntity> findAll(Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "category", "tags"})
	Page<PostEntity> findAllByUserId(long userId, Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "category", "tags"})
	Page<PostEntity> findAllByCategoryNameIgnoreCase(String categoryName, Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "category", "tags"})
	@Query("SELECT post FROM PostEntity post JOIN post.tags tag WHERE LOWER(tag.name) = LOWER(:tagName)") // using JPQL Query
	Page<PostEntity> findAllByTagName(@Param("tagName") String tagName, Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "category", "tags"})
	@Query("""
		    SELECT DISTINCT post 
		    FROM PostEntity post 
		    LEFT JOIN post.tags tag 
		    WHERE LOWER(post.title) LIKE LOWER(CONCAT('%', :title, '%')) 
		       OR LOWER(tag.name) IN :tags
		""")
	Page<PostEntity> searchByTitleOrTags(@Param("title") String title, @Param("tags") List<String> tags, Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "category", "tags"})
	@Query("SELECT DISTINCT post FROM PostEntity post LEFT JOIN post.tags tag WHERE LOWER(tag.name) IN :tags")
	Page<PostEntity> searchByTags(@Param("tags") List<String> tags, Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM PostEntity post WHERE post.user.id = :userId")
	void deleteByUserId(@Param("userId") long userId);
	
	@Query("SELECT post.id FROM PostEntity post WHERE post.user.id = :userId")
	List<Long> findPostIdsByUserId(@Param("userId") Long userId);
}
