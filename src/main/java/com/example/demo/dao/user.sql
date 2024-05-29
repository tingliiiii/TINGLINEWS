-- CREATE DATABASE tingli;

-- DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(25),
    userEmail VARCHAR(50) NOT NULL UNIQUE,
    userPassword VARCHAR(50) NOT NULL,
    registeredDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    birthday DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    donateId INT,   
    authorityId INT DEFAULT 0
);

ALTER TABLE User AUTO_INCREMENT = 1000;

INSERT INTO user(userName, userEmail, userPassword) VALUES ('王采惟', 'irin@gmail.com', '1234');
INSERT INTO user(userName, userEmail, userPassword) VALUES ('簡晨恩', 'katy@gmail.com', '1234');
INSERT INTO user(userName, userEmail, userPassword) VALUES ('王誠', 'nikolas@gmail.com', '1234');

-- DROP TABLE IF EXISTS authority;

CREATE TABLE IF NOT EXISTS authority (
	authorityId INT PRIMARY KEY,
    authorityName VARCHAR(10)
);

INSERT INTO authority(authorityId, authorityName) VALUES (0, '使用者');
INSERT INTO authority(authorityId, authorityName) VALUES (1, '員工');
INSERT INTO authority(authorityId, authorityName) VALUES (2, '記者');
INSERT INTO authority(authorityId, authorityName) VALUES (3, '編輯');
INSERT INTO authority(authorityId, authorityName) VALUES (4, '管理員');