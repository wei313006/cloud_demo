CREATE DATABASE cd_user

CREATE DATABASE cd_search



CREATE TABLE `user` (
  `id` BIGINT(20) UNSIGNED NOT NULL,
  `username` VARCHAR(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `email` VARCHAR(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `password` VARCHAR(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `sort` TINYINT(3) UNSIGNED DEFAULT 50,
  `logic_delete` TINYINT(3) UNSIGNED DEFAULT 0,
  `access_token` VARCHAR(255) COLLATE utf8mb4_bin DEFAULT '',
  `refresh_token` VARCHAR(255) COLLATE utf8mb4_bin DEFAULT '',
  `status` TINYINT(3) UNSIGNED NOT NULL DEFAULT 1,
  `area` VARCHAR(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `register_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `ip` VARCHAR(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  `avatar` VARCHAR(255) COLLATE utf8mb4_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),UNIQUE KEY `email` (`email`), UNIQUE KEY `username` (`username`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

SELECT * FROM `user`


CREATE TABLE roles(
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	`role` VARCHAR(255) NOT NULL DEFAULT '' ,
	description VARCHAR(255) NOT NULL DEFAULT ''
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

SELECT * FROM `roles`



CREATE TABLE user_roles (
    uid BIGINT UNSIGNED NOT NULL,
    rid INT UNSIGNED NOT NULL,
    FOREIGN KEY (uid) REFERENCES `user`(id),
    FOREIGN KEY (rid) REFERENCES roles(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

SELECT * FROM user_roles


CREATE TABLE permissions (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    resource_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (resource_id) REFERENCES resources(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

SELECT * FROM permissions

INSERT INTO permissions (`name`, description, menu_name, ACTION) VALUES
('user_add', '用户添加', '用户管理', 'ADD'),
('user_edit', '用户编辑', '用户管理', 'EDIT'),
('user_delete', '用户删除', '用户管理', 'DELETE'),
('user_view', '查看用户', '用户管理', 'VIEW'),
('role_add', '角色添加', '角色管理', 'ADD'),
('role_edit', '角色编辑', '角色管理', 'EDIT'),
('role_delete', '角色删除', '角色管理', 'DELETE'),
('role_view', '查看角色', '角色管理', 'VIEW');



CREATE TABLE role_permissions (
    role_id INT UNSIGNED,
    permission_id INT UNSIGNED,
    INDEX (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

SELECT * FROM role_permissions WHERE role_id IN (3)




CREATE TABLE resources (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL DEFAULT '',          
    description VARCHAR(255) NOT NULL DEFAULT '',            
    resource_type VARCHAR(255) NOT NULL DEFAULT '',         
    resource_url VARCHAR(255) NOT NULL DEFAULT '',           
    parent_id INT UNSIGNED DEFAULT NULL,       
    created_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

SELECT * FROM resources

INSERT INTO resources (`name`, description, resource_type, resource_url,created_by) VALUES
('user_add', '用户添加操作', 'API', '/user/admin/add','abing'),
('user_edit', '用户编辑操作', 'API', '/user/admin/edit','abing'),
('user_delete', '用户删除操作', 'API', '/user/admin/delete','abing'),
('user_view', '用户查看操作', 'API', '/user/admin/view','abing'),
('user_page', '用户管理页面', 'PAGE', '/user/admin/page','abing')



CREATE TABLE menu (
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '菜单名称',
	label VARCHAR(100) NOT NULL DEFAULT '' COMMENT '前端显示名称',
	icon VARCHAR(100) NOT NULL DEFAULT '' COMMENT '菜单图标',
	route_path VARCHAR(200) NOT NULL DEFAULT '' COMMENT '路由路径',
	parent_id INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级菜单',
	component VARCHAR(100) NOT NULL DEFAULT '' COMMENT '组件路径',
	perm_id INT UNSIGNED NOT NULL COMMENT '权限id',
	perm_code VARCHAR(100) NOT NULL DEFAULT '' COMMENT '权限代码（保底策略），优先通过id查询',
	is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否逻辑删除',
	create_by VARCHAR(50) DEFAULT '' COMMENT '通过（管理员创建）',
	create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	is_public TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否公开访问',
	sort TINYINT UNSIGNED DEFAULT 50 COMMENT '排序字段'
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT='菜单表';

SELECT * FROM menu



CREATE DATABASE cd_file

CREATE TABLE `file_info` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY ,
  `origin_name` VARCHAR(255) NOT NULL DEFAULT '',
  `storage_name` VARCHAR(255) NOT NULL UNIQUE,
  `file_size` INT UNSIGNED NOT NULL DEFAULT 0,
  `file_type` VARCHAR(255) NOT NULL DEFAULT '',
  `url` TEXT,`storage_type` VARCHAR(50) DEFAULT 'minio' ,
  `bucket_name` VARCHAR(255) NOT NULL  DEFAULT '',
  `create_by` VARCHAR(50) DEFAULT '',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP ,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ,
  `logic_delete` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `is_public` TINYINT UNSIGNED NOT NULL DEFAULT 1
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;












