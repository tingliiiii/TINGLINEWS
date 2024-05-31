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
    authority_id INT DEFAULT 0
);

-- ALTER TABLE user RENAME COLUMN authorityId TO authority_id;
-- ALTER TABLE user DROP donateId;
-- ALTER TABLE user AUTO_INCREMENT = 1000;

-- ALTER TABLE user ADD FOREIGN KEY (authority_id) REFERENCES authority(authority_id) ON DELETE CASCADE;

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

DROP TABLE IF EXISTS donated;

CREATE TABLE IF NOT EXISTS donated (
	donated_id INT AUTO_INCREMENT PRIMARY KEY,
	frequency VARCHAR(10) NOT NULL, -- 每月, 每年, 單筆
	amount INT NOT NULL,
	donated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    donate_status VARCHAR(10), -- 進行中, 已完成
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

INSERT INTO donated(frequency, amount, donate_status, user_id) VALUES ('每月', 1000, '進行中', 1040);
INSERT INTO donated(frequency, amount, donate_status, user_id) VALUES ('單筆', 200, '進行中', 1032);

-- ALTER TABLE donated RENAME COLUMN endDate TO end_date;

-- DROP TABLE IF EXISTS tag;

CREATE TABLE IF NOT EXISTS tag (
	tag_id INT PRIMARY KEY,
    tag_name VARCHAR(10)
);

INSERT INTO tag(tag_id, tag_name) VALUES(1, '政治');
INSERT INTO tag(tag_id, tag_name) VALUES(2, '社會');
INSERT INTO tag(tag_id, tag_name) VALUES(3, '國際');
INSERT INTO tag(tag_id, tag_name) VALUES(4, '環境');
INSERT INTO tag(tag_id, tag_name) VALUES(5, '文化');
INSERT INTO tag(tag_id, tag_name) VALUES(6, '生活');
INSERT INTO tag(tag_id, tag_name) VALUES(7, '娛樂');

-- DROP TABLE IF EXISTS news;

CREATE TABLE IF NOT EXISTS news (
	news_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50),
    content TEXT,
    tag_id INT,
    user_id INT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(tag_id) ON DELETE CASCADE
);

INSERT INTO news(title, content, tag_id, user_id) VALUES('大樂透頭獎槓龜 下期獎金保證1億元', '大樂透第113057期今晚開獎。中獎號碼為16、23、32、14、08、19，特別號49；派彩結果，頭獎槓龜，下期頭獎保證新台幣1億元。
本期大樂透加開54組100萬獎項開出情形，54組中開出6組，總中獎注數為7注，其中5組為單注中獎，1組為2注均分，本期尚有48組100萬獎項未開出，將於次期（6月4日）繼續加開。
貳獎1注中獎，獎金新台幣432萬601元。派彩結果及中獎獎號以台彩公布為準。', 6, 1040);

-- ALTER TABLE news AUTO_INCREMENT = 6000;
-- ALTER TABLE news ADD public BOOLEAN DEFAULT FALSE;
-- ALTER TABLE news ADD public_time TIMESTAMP;

CREATE TABLE IF NOT EXISTS saved (
	saved_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    news_id INT,
    saved_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (news_id) REFERENCES news(news_id) ON DELETE CASCADE,
    CONSTRAINT unique_userid_and_newsid UNIQUE(user_id, news_id)
);





