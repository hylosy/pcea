
CREATE TABLE holding_event_results (
  holdingEventId BIGINT NOT NULL,
  `rank` BIGINT NOT NULL,
  point BIGINT NOT NULL,
  deckId VARCHAR(100) NOT NULL,
  playerId VARCHAR(100) NOT NULL,
  PRIMARY KEY(holdingEventId, playerId)
);
