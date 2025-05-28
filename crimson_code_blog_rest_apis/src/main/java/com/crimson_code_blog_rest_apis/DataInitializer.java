package com.crimson_code_blog_rest_apis;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crimson_code_blog_rest_apis.entity.CategoryEntity;
import com.crimson_code_blog_rest_apis.entity.RoleEntity;
import com.crimson_code_blog_rest_apis.entity.TagEntity;
import com.crimson_code_blog_rest_apis.entity.UserEntity;
import com.crimson_code_blog_rest_apis.repository.CategoryRepository;
import com.crimson_code_blog_rest_apis.repository.RoleRepository;
import com.crimson_code_blog_rest_apis.repository.TagRepository;
import com.crimson_code_blog_rest_apis.repository.UserRepository;
import com.crimson_code_blog_rest_apis.utils.DefaultCategory;
import com.crimson_code_blog_rest_apis.utils.DefaultTag;
import com.crimson_code_blog_rest_apis.utils.UserRoles;


@Service
public class DataInitializer implements CommandLineRunner {

	private CategoryRepository categoryRepository;
	private UserRepository userRepository;
	private TagRepository tagRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public DataInitializer(CategoryRepository categoryRepository, UserRepository userRepository,
			TagRepository tagRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.tagRepository = tagRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {
		initialCategories();
		initialTags();
		initialUsers();
	}
	
	private void initialCategories() {
		for (DefaultCategory category : DefaultCategory.values()) {
			categoryRepository.findByNameIgnoreCase(category.getDisplayName())
			
			.orElseGet(() -> categoryRepository.save(new CategoryEntity(category.getDisplayName())));
		}
	}
	
	private void initialTags() {
		for (DefaultTag tag : DefaultTag.values()) {
			tagRepository.findByNameIgnoreCase(tag.getDisplayName().toLowerCase())
			.orElseGet(() -> tagRepository.save(new TagEntity(tag.getDisplayName().toLowerCase())));
		}
	}
	
	private void initialUsers() {
		RoleEntity adminRole = roleRepository.findByName(UserRoles.ROLE_ADMIN.name())
				.orElseGet(() -> roleRepository.save(new RoleEntity(UserRoles.ROLE_ADMIN.name())));
		
		RoleEntity userRole = roleRepository.findByName(UserRoles.ROLE_USER.name())
				.orElseGet(() -> roleRepository.save(new RoleEntity(UserRoles.ROLE_USER.name())));
		
		UserEntity adminUser = userRepository.findByEmail("admin@crimsoncode.com")
				.orElseGet(() -> {
					UserEntity newAdminUser = new UserEntity();
					
					newAdminUser.setEmail("admin@crimsoncode.com");
					newAdminUser.setPassword(passwordEncoder.encode("admin"));
					newAdminUser.setPublicId(UUID.randomUUID().toString());
					newAdminUser.setFirstName("Admin");
					newAdminUser.setLastName("Admin");
					newAdminUser.setJoinedAt(OffsetDateTime.now(ZoneOffset.UTC));
					newAdminUser.setIsEmailVerified(true);
					return newAdminUser;
				});
		
		adminUser.setRoles(List.of(userRole, adminRole));
		userRepository.save(adminUser);
	}

}
