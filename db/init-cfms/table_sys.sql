
CREATE TABLE sys_Areas
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	AreaName             VARCHAR(256) NULL,
	AreaCode             VARCHAR(32) NULL,
	AreaRome             VARCHAR(128) NULL,
	AreaRomeShort        VARCHAR(32) NULL,
	TelephoneCode        VARCHAR(32) NULL,
	MobileCode           VARCHAR(32) NULL,
	ParentId             BIGINT NULL,
	AreaOrder            INTEGER NULL,
	ZipCode              VARCHAR(32) NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Data_Dictionary
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	DataValue            VARCHAR(256) NULL,
	DataName             VARCHAR(256) NULL,
	ParentId             BIGINT NULL,
	Description          VARCHAR(512) NULL,
	DispType             INTEGER NULL,
	DispOrder            INTEGER NULL,
	ExtendData           VARCHAR(256) NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Departments
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	ParentId             BIGINT NULL,
	DepartmentName       VARCHAR(256) NULL,
	DepartmentDesc       VARCHAR(512) NULL,
	DispOrder            INTEGER NULL,
	CreateDate           DATE NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Files
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	FileType             VARCHAR(16) NULL,
	Memo                 VARCHAR(512) NULL,
	FileName             VARCHAR(256) NULL,
	Filepath             VARCHAR(512) NULL,
	UploadTime           DATETIME NULL,
	FileSize             BIGINT NULL,
	FileIdentify         VARCHAR(32) NULL,
	UserID               BIGINT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Group_Menu_Rights
(
	MenuRightId          BIGINT NOT NULL,
	GroupMenuId          BIGINT NOT NULL,
	PRIMARY KEY (GroupMenuId,MenuRightId)
);

CREATE TABLE sys_Group_Menus
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	GroupId              BIGINT NOT NULL,
	MenuId               BIGINT NOT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Groups
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	GroupName            VARCHAR(256) NULL,
	Memo                 VARCHAR(512) NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Log_Items
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	Data                 VARCHAR(512) NULL,
	Description          VARCHAR(512) NULL,
	OriginalData         VARCHAR(512) NULL,
	LogId                BIGINT NOT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Logs
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	ServiceName          VARCHAR(256) NULL,
	MsgCode              VARCHAR(128) NULL,
	Status               VARCHAR(32) NULL,
	ServiceTime          DATETIME NULL,
	MsgDesc              VARCHAR(512) NULL,
	ServiceDesc          VARCHAR(512) NULL,
	IP                   VARCHAR(64) NULL,
	Bak1                 VARCHAR(64) NULL,
	Bak2                 VARCHAR(64) NULL,
	Bak3                 VARCHAR(64) NULL,
	LoginName            VARCHAR(256) NULL,
	Agent                VARCHAR(256) NULL,
	UserID               BIGINT NULL,
	UserType             INT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Menu_Rights
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	MenuId               BIGINT NOT NULL,
	RightsValue          VARCHAR(32) NULL,
	RightsName           VARCHAR(256) NULL,
	RightsUrl            VARCHAR(256) NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Menus
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	ParentId             BIGINT NULL,
	MenuName             VARCHAR(256) NULL,
	URL                  VARCHAR(256) NULL,
	TranCode             VARCHAR(32) NULL,
	MenuOrder            INTEGER NULL,
	IconClass            VARCHAR(64) NULL,
	IsHidden             INTEGER NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Positions
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	ParentId             BIGINT NULL,
	PositionName         VARCHAR(256) NULL,
	PositionDesc         VARCHAR(512) NULL,
	PositionIdentify     VARCHAR(64) NULL,
	DispOrder            INTEGER NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Toolbar
(
	ElementID            VARCHAR(64) NOT NULL,
	UserID               BIGINT NOT NULL,
	MenuID               BIGINT NOT NULL,
	IconClass            VARCHAR(128) NULL,
	PRIMARY KEY (ElementID,UserID)
);

CREATE TABLE sys_User_Groups
(
	UserId               BIGINT NOT NULL,
	GroupId              BIGINT NOT NULL,
	PRIMARY KEY (GroupId,UserId)
);

CREATE TABLE sys_User_Params
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	ParentID             BIGINT NULL,
	ParamName            VARCHAR(64) NULL,
	ParamValue           VARCHAR(64) NULL,
	ParamDesc            VARCHAR(512) NULL,
	ParamType            VARCHAR(32) NULL,
	UserID               BIGINT NULL,
	PRIMARY KEY (ID)
);

CREATE TABLE sys_Users
(
	ID                   BIGINT NOT NULL AUTO_INCREMENT,
	UserName             VARCHAR(256) NULL,
	StaffId              VARCHAR(32) NULL,
	LoginName            VARCHAR(256) NULL,
	Password             VARCHAR(512) NULL,
	Memo                 VARCHAR(512) NULL,
	LastLoginDate        DATETIME NULL,
	Status               VARCHAR(32) NULL,
	DispOrder            INTEGER NULL,
	LoginType            INTEGER NULL,
	EntryDate            DATE NULL,
	LeaveDate            DATE NULL,
	CreateDate           DATE NULL,
	PhoneNo              VARCHAR(32) NULL,
	Email                VARCHAR(128) NULL,
	Birthday             DATE NULL,
	CardID               VARCHAR(32) NULL,
	isdel                INTEGER NULL,
	DepartID             BIGINT NULL,
	PositID              BIGINT NULL,
	PRIMARY KEY (ID)
);

ALTER TABLE sys_Areas
ADD FOREIGN KEY R_112 (ParentId) REFERENCES sys_Areas (ID)
		ON DELETE CASCADE;

ALTER TABLE sys_Data_Dictionary
ADD FOREIGN KEY R_105 (ParentId) REFERENCES sys_Data_Dictionary (ID)
		ON DELETE CASCADE;

ALTER TABLE sys_Departments
ADD FOREIGN KEY R_118 (ParentId) REFERENCES sys_Departments (ID)
		ON DELETE CASCADE;

ALTER TABLE sys_Files
ADD FOREIGN KEY R_116 (UserID) REFERENCES sys_Users (ID);

ALTER TABLE sys_Group_Menu_Rights
ADD FOREIGN KEY R_108 (MenuRightId) REFERENCES sys_Menu_Rights (ID);

ALTER TABLE sys_Group_Menu_Rights
ADD FOREIGN KEY R_109 (GroupMenuId) REFERENCES sys_Group_Menus (ID);

ALTER TABLE sys_Group_Menus
ADD FOREIGN KEY R_107 (GroupId) REFERENCES sys_Groups (ID);

ALTER TABLE sys_Group_Menus
ADD FOREIGN KEY R_106 (MenuId) REFERENCES sys_Menus (ID);

ALTER TABLE sys_Log_Items
ADD FOREIGN KEY R_104 (LogId) REFERENCES sys_Logs (ID);

-- ALTER TABLE sys_Logs
-- ADD FOREIGN KEY R_115 (UserID) REFERENCES sys_Users (ID);

ALTER TABLE sys_Menu_Rights
ADD FOREIGN KEY R_100 (MenuId) REFERENCES sys_Menus (ID);

ALTER TABLE sys_Menus
ADD FOREIGN KEY R_103 (ParentId) REFERENCES sys_Menus (ID)
		ON DELETE CASCADE;

ALTER TABLE sys_Positions
ADD FOREIGN KEY R_117 (ParentId) REFERENCES sys_Positions (ID)
		ON DELETE CASCADE;

ALTER TABLE sys_Toolbar
ADD FOREIGN KEY R_110 (UserID) REFERENCES sys_Users (ID);

ALTER TABLE sys_Toolbar
ADD FOREIGN KEY R_111 (MenuID) REFERENCES sys_Menus (ID);

ALTER TABLE sys_User_Groups
ADD FOREIGN KEY R_101 (UserId) REFERENCES sys_Users (ID);

ALTER TABLE sys_User_Groups
ADD FOREIGN KEY R_102 (GroupId) REFERENCES sys_Groups (ID);

ALTER TABLE sys_User_Params
ADD FOREIGN KEY R_113 (UserID) REFERENCES sys_Users (ID);

ALTER TABLE sys_User_Params
ADD FOREIGN KEY R_114 (ParentID) REFERENCES sys_User_Params (ID)
		ON DELETE CASCADE;

ALTER TABLE sys_Users
ADD FOREIGN KEY R_119 (DepartID) REFERENCES sys_Departments (ID);

ALTER TABLE sys_Users
ADD FOREIGN KEY R_120 (PositID) REFERENCES sys_Positions (ID);
