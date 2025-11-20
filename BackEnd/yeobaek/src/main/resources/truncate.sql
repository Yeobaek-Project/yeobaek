SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE artist;
TRUNCATE TABLE artwork;
TRUNCATE TABLE artwork_bookmark;
TRUNCATE TABLE artwork_image;
TRUNCATE TABLE cafe;
TRUNCATE TABLE cafe_bookmark;
TRUNCATE TABLE cafe_category;
TRUNCATE TABLE cafe_category_tag;
TRUNCATE TABLE cafe_image;
TRUNCATE TABLE chat_room;
TRUNCATE TABLE chat_room_member;
TRUNCATE TABLE display_slot;
TRUNCATE TABLE display_wall;
TRUNCATE TABLE exhibition;
TRUNCATE TABLE message;
TRUNCATE TABLE review;
TRUNCATE TABLE user_behavior_event;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;