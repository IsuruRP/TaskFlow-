package com.taskflow.backend.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	@Query("select distinct p from Project p left join ProjectMember pm on pm.project = p " +
			"where p.owner.id = :userId or pm.user.id = :userId")
	Page<Project> findAccessible(@Param("userId") Long userId, Pageable pageable);

	@Query("select distinct p from Project p left join ProjectMember pm on pm.project = p " +
			"where (p.owner.id = :userId or pm.user.id = :userId) and lower(p.name) like lower(concat('%', :query, '%'))")
	Page<Project> searchAccessible(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);
}
