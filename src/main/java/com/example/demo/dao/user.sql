-- CREATE DATABASE tingli;

-- DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(25),
    user_email VARCHAR(50) NOT NULL UNIQUE,
    user_password VARCHAR(100) NOT NULL,
    registered_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    birthday DATE,
    gender VARCHAR(10) DEFAULT 'N/A',
    phone VARCHAR(20),   
    authority_id INT DEFAULT 0,
    salt VARCHAR(100)
);

-- ALTER TABLE user ALTER COLUMN gender SET DEFAULT 'N/A';
-- ALTER TABLE user RENAME COLUMN authorityId TO authority_id;
-- ALTER TABLE user DROP donateId;
-- ALTER TABLE user AUTO_INCREMENT = 1000;

INSERT INTO user(user_email) VALUES ('huangtp@gmail.com');
INSERT INTO user(user_name, user_email, user_password) VALUES ('王采惟', 'irin@gmail.com', '1234');
INSERT INTO user(user_name, user_email, user_password) VALUES ('簡晨恩', 'katy@gmail.com', '1234');
INSERT INTO user(user_name, user_email, user_password) VALUES ('王誠', 'nikolas@gmail.com', '1234');
INSERT INTO user(user_name, user_email, user_password, birthday, gender, phone) VALUES ('劉允恩', 'baby@gmail.com', '1234', '2022-04-26', 'MALE', '0912-345678');

-- DROP TABLE IF EXISTS authority;

CREATE TABLE IF NOT EXISTS authority (
	authority_id INT PRIMARY KEY,
    authority_name VARCHAR(10)
);

INSERT INTO authority(authority_id, authority_name) VALUES (0, '使用者');
INSERT INTO authority(authority_id, authority_name) VALUES (1, '員工');
INSERT INTO authority(authority_id, authority_name) VALUES (2, '記者');
INSERT INTO authority(authority_id, authority_name) VALUES (3, '編輯');
INSERT INTO authority(authority_id, authority_name) VALUES (4, '管理員');

-- ALTER TABLE authority RENAME COLUMN authorityName TO authority_name;

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

INSERT INTO donated(frequency, amount, donate_status, user_id) VALUES ('每月', 1000, '進行中', 1040);
INSERT INTO donated(frequency, amount, donate_status, user_id) VALUES ('單筆', 200, '進行中', 1032);

-- ALTER TABLE donated RENAME COLUMN endDate TO end_date;



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

CREATE TABLE IF NOT EXISTS third_party_auth (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);


