package com.taskflow.backend.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
	boolean existsByProjectIdAndUserId(Long projectId, Long userId);
	List<ProjectMember> findByProjectId(Long projectId);
	void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
