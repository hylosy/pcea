
CREATE TABLE events (
    id BIGINT PRIMARY KEY,
    eventCategoryId BIGINT NOT NULL,
    eventDate DATE NOT NULL,
    shopId BIGINT,
    prefectureName VARCHAR(100) NOT NULL
);
