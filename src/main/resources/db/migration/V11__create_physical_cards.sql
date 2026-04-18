
CREATE TABLE physical_cards (
  id          BIGINT        NOT NULL,
  name        VARCHAR(100)  NOT NULL,
  cardType    INT           NOT NULL,
  number      VARCHAR(20)   NOT NULL,
  rarity      VARCHAR(50)   NULL,
  illustrator VARCHAR(100)  NULL,
  expansionId VARCHAR(50)   NOT NULL,
  imageUrl    VARCHAR(500)  NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_physical_cards_expansion FOREIGN KEY (expansionId) REFERENCES expansions(id)
);
