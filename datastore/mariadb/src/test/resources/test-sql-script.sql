SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS dpp_data;

SET FOREIGN_KEY_CHECKS = 1;

-- Crea tabella
CREATE TABLE IF NOT EXISTS dpp_data
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    upi         VARCHAR(36)   UNIQUE NOT NULL,
    live_url    VARCHAR(1000),
    search_data JSON          NOT NULL
    );

-- Insert
INSERT INTO dpp_data (upi, live_url, search_data) VALUES ('123456','http://localhost:8080/dpp1','{"strField":"text1","intField":10,"doubleField":9.5,"boolField":true}');
INSERT INTO dpp_data (upi, live_url, search_data) VALUES ('234567','http://localhost:8080/dpp2','{"strField":"text2","intField":20,"doubleField":23.1,"boolField":false}');
INSERT INTO dpp_data (upi, live_url, search_data) VALUES ('345678','http://localhost:8080/dpp3','{"strField":"text3","intField":12,"doubleField":45.7,"boolField":true}');
INSERT INTO dpp_data (upi, live_url, search_data) VALUES ('456789','http://localhost:8080/dpp4','{"strField":"text4","intField":21,"doubleField":11.2,"boolField":false}');
INSERT INTO dpp_data (upi, live_url, search_data) VALUES ('567890','http://localhost:8080/dpp5','{"strField":"text5","intField":34,"doubleField":0.3,"boolField":true}');