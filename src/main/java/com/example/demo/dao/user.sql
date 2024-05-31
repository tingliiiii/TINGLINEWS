-- CREATE DATABASE tingli;

-- DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(25),
    user_email VARCHAR(50) NOT NULL UNIQUE,
    user_password VARCHAR(50) NOT NULL,
    registered_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    birthday DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    donate_id INT,   
    authority_id INT DEFAULT 0
);

ALTER TABLE user AUTO_INCREMENT = 1000;

INSERT INTO user(userName, userEmail, userPassword) VALUES ('王采惟', 'irin@gmail.com', '1234');
INSERT INTO user(userName, userEmail, userPassword) VALUES ('簡晨恩', 'katy@gmail.com', '1234');
INSERT INTO user(userName, userEmail, userPassword) VALUES ('王誠', 'nikolas@gmail.com', '1234');
INSERT INTO user(userName, userEmail, userPassword, birthday, gender, phone) VALUES ('劉允恩', 'baby@gmail.com', '1234', '2022-04-26', 'MALE', '0912-345678');

-- DROP TABLE IF EXISTS authority;

CREATE TABLE IF NOT EXISTS authority (
	authority_id INT PRIMARY KEY,
    authority_name VARCHAR(10)
);

INSERT INTO authority(authorityId, authorityName) VALUES (0, '使用者');
INSERT INTO authority(authorityId, authorityName) VALUES (1, '員工');
INSERT INTO authority(authorityId, authorityName) VALUES (2, '記者');
INSERT INTO authority(authorityId, authorityName) VALUES (3, '編輯');
INSERT INTO authority(authorityId, authorityName) VALUES (4, '管理員');

-- DROP TABLE IF EXISTS donated;

CREATE TABLE IF NOT EXISTS donated (
	donated_id INT AUTO_INCREMENT PRIMARY KEY,
	frequency VARCHAR(10) NOT NULL, -- 每月, 每年, 單筆
	amount INT NOT NULL,
	donated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date DATE,
    donate_status VARCHAR(10), -- 進行中, 已完成
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

INSERT INTO donated(frequency, amount, donateStatus, user_id) VALUES ('每月', 1000, '進行中', 1040);
INSERT INTO donated(frequency, amount, donateStatus, user_id) VALUES ('單筆', 200, '進行中', 1032);

CREATE TABLE IF NOT EXISTS news (
	news_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50),
    content TEXT,
    tag_id INT,
    user_id INT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP
);




CREATE TABLE IF NOT EXISTS saved (
	saved_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    news_id INT,
    saved_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (news_id) REFERENCES user(news_id),
    CONSTRAINT unique_userid_and_newsid UNIQUE(user_id, news_id)
);

CREATE TABLE IF NOT EXISTS tag (
	tag_id INT PRIMARY KEY,
    tag_name VARCHAR(10)
);




