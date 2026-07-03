#游戏表
CREATE TABLE games(
 app_id INT PRIMARY KEY,
 `name` VARCHAR(255) NOT NULL,
 developer VARCHAR(255),
 publisher VARCHAR(255),
 price DECIMAL(10,2) DEFAULT 0,
 release_date VARCHAR(80),
 positive_reviews BIGINT DEFAULT 0,
 negative_reviews BIGINT DEFAULT 0,
 total_reviews BIGINT DEFAULT 0,
 review_score_desc VARCHAR(50)
);


CREATE TABLE tags(
 tag_id INT PRIMARY KEY AUTO_INCREMENT,
 tag_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE games_tags(
 app_id INT NOT NULL,
 tag_id INT NOT NULL,
 PRIMARY KEY (app_id, tag_id),
  CONSTRAINT fk_game_tag_games 
        FOREIGN KEY (app_id) REFERENCES games(app_id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_game_tag_tags 
        FOREIGN KEY (tag_id) REFERENCES tags(tag_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);
																																													
SELECT * FROM games;

--     private List<String> genres;       //类型数组