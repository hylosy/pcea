ALTER TABLE `events` RENAME `holding_events`;
ALTER TABLE `holding_events` RENAME COLUMN `eventCategoryId` TO `eventId`;
