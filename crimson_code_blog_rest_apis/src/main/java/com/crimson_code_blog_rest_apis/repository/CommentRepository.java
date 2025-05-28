package com.crimson_code_blog_rest_apis.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crimson_code_blog_rest_apis.entity.CommentEntity;

import jakarta.transaction.Transactional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>{

	@EntityGraph(attributePaths = {"user"})
	Page<CommentEntity> findAllByPostId(long postId, Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM CommentEntity comment WHERE comment.post.id = :postId")
	void deleteByPostId(@Param("postId") long postId);
	
	@Modifying
	@Query("DELETE FROM CommentEntity comment WHERE comment.user.id = :userId")
	void deleteByUserId(@Param("userId") long userId);
	
	@Modifying
	@Query("DELETE FROM CommentEntity comment WHERE comment.post.id in :postIds")
	void deleteByPostIds(@Param("postIds") List<Long> postIds);
}
