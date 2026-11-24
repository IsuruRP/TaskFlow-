CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    role NVARCHAR(30) NOT NULL,
    enabled BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE projects (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(120) NOT NULL,
    description NVARCHAR(2000),
    status NVARCHAR(30) NOT NULL,
    owner_id BIGINT NOT NULL,
    start_date DATE,
    due_date DATE,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE project_members (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT uk_project_member UNIQUE (project_id, user_id),
    CONSTRAINT fk_members_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tasks (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title NVARCHAR(160) NOT NULL,
    description NVARCHAR(3000),
    status NVARCHAR(30) NOT NULL,
    priority NVARCHAR(30) NOT NULL,
    assignee_id BIGINT,
    due_date DATE,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_assignee FOREIGN KEY (assignee_id) REFERENCES users(id)
);

CREATE TABLE task_comments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    task_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content NVARCHAR(2000) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_comments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message NVARCHAR(500) NOT NULL,
    is_read BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
