ALTER TABLE app_user ADD COLUMN username VARCHAR(100);
UPDATE app_user SET username = email WHERE username IS NULL;
ALTER TABLE app_user ALTER COLUMN username SET NOT NULL;

ALTER TABLE app_user ADD COLUMN display_name VARCHAR(100);
UPDATE app_user SET display_name = email WHERE display_name IS NULL;

ALTER TABLE app_user ALTER COLUMN email DROP NOT NULL;
ALTER TABLE app_user ALTER COLUMN password DROP NOT NULL;

CREATE TABLE userconnection (
	userid VARCHAR(255) PRIMARY KEY,
	providerid VARCHAR(255) NOT NULL,
	provideruserid VARCHAR(255) NOT NULL,
	rank INT,
	displayname VARCHAR(255),
	profileurl VARCHAR(512),
	imageurl VARCHAR(512),
	accesstoken VARCHAR(1024),
	secret VARCHAR(255),
	refreshtoken VARCHAR(255),
	expiretime BIGINT
);