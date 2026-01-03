
CREATE TABLE cards_in_deck (
  deckId VARCHAR(100) NOT NULL,
  cardId BIGINT NOT NULL,
  count INT NOT NULL,
  PRIMARY KEY(deckId, cardId)
);
