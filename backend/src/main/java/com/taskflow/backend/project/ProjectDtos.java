package com.taskflow.backend.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ProjectDtos {
	private ProjectDtos() { }
	public record ProjectRequest(@NotBlank @Size(max = 120) String name, @Size(max = 2000) String description,
			LocalDate startDate, LocalDate dueDate, ProjectStatus status) { }
	public record ProjectResponse(Long id, String name, String description, ProjectStatus status,
			Long ownerId, String ownerName, LocalDate startDate, LocalDate dueDate, Instant createdAt) {
		public static ProjectResponse from(Project p) {
			return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getStatus(),
					p.getOwner().getId(), p.getOwner().getFullName(), p.getStartDate(), p.getDueDate(), p.getCreatedAt());
		}
	}
	public record AddMemberRequest(Long userId) { }
	public record MemberResponse(Long userId, String fullName, String email, Instant joinedAt) {
		static MemberResponse from(ProjectMember member) {
			return new MemberResponse(member.getUser().getId(), member.getUser().getFullName(),
					member.getUser().getEmail(), member.getJoinedAt());
		}
	}
	public record ProjectDetailResponse(ProjectResponse project, List<MemberResponse> members) { }
}
