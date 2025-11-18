
-- Users
INSERT INTO users (id, user_type, email, password, nickname, birth_date, gender, profile_image_url) VALUES
-- GENERAL users
(1, 'GENERAL', 'user1@example.com', '$2a$10$n84oRbh1Vs/I1UDUBOD.bOBVLHORMmOkptOwJvkQn3hqWmHfrPs3m', 'GeneralUser1', '1990-01-01', 'MALE', 'images/5d286eb1-ae8f-4605-abc4-3252f35a319a'),
(2, 'GENERAL', 'general2@example.com', '$2a$10$n84oRbh1Vs/I1UDUBOD.bOBVLHORMmOkptOwJvkQn3hqWmHfrPs3m', 'GeneralUser2', '1992-02-02', 'FEMALE', 'images/general2.jpg'),
(3, 'GENERAL', 'general3@example.com', '$2a$10$n84oRbh1Vs/I1UDUBOD.bOBVLHORMmOkptOwJvkQn3hqWmHfrPs3m', 'GeneralUser3', '1993-03-03', 'MALE', 'images/general3.jpg'),
(4, 'GENERAL', 'general4@example.com', '$2a$10$n84oRbh1Vs/I1UDUBOD.bOBVLHORMmOkptOwJvkQn3hqWmHfrPs3m', 'GeneralUser4', '1994-04-04', 'FEMALE', 'images/general4.jpg'),
(5, 'GENERAL', 'general5@example.com', '$2a$10$n84oRbh1Vs/I1UDUBOD.bOBVLHORMmOkptOwJvkQn3hqWmHfrPs3m', 'GeneralUser5', '1995-05-05', 'MALE', 'images/general5.jpg'),

-- ARTIST users
(6, 'ARTIST', 'user2@example.com', '$2a$10$rN6RbbxGwZF1UQwEnLjflu8vlbWL33cRxbhMuSkdo9aHxZVfFoOK6', 'ArtistUser1', '1990-06-06', 'FEMALE', 'images/1754c46f-847c-4bae-bc4b-41779f5cac07'),
(7, 'ARTIST', 'artist2@example.com', '$2a$10$rN6RbbxGwZF1UQwEnLjflu8vlbWL33cRxbhMuSkdo9aHxZVfFoOK6', 'ArtistUser2', '1991-07-07', 'MALE', 'images/artist2.jpg'),
(8, 'ARTIST', 'artist3@example.com', '$2a$10$rN6RbbxGwZF1UQwEnLjflu8vlbWL33cRxbhMuSkdo9aHxZVfFoOK6', 'ArtistUser3', '1992-08-08', 'FEMALE', 'images/artist3.jpg'),
(9, 'ARTIST', 'artist4@example.com', '$2a$10$rN6RbbxGwZF1UQwEnLjflu8vlbWL33cRxbhMuSkdo9aHxZVfFoOK6', 'ArtistUser4', '1993-09-09', 'MALE', 'images/artist4.jpg'),
(10, 'ARTIST', 'artist5@example.com', '$2a$10$rN6RbbxGwZF1UQwEnLjflu8vlbWL33cRxbhMuSkdo9aHxZVfFoOK6', 'ArtistUser5', '1994-10-10', 'FEMALE', 'images/artist5.jpg'),

-- CAFE_OWNER users
(11, 'CAFE_OWNER', 'user3@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner1', '1985-11-11', 'MALE', 'images/6b135b17-9a5b-4179-9191-9658e2d046eb'),
(12, 'CAFE_OWNER', 'cafeowner2@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner2', '1986-12-12', 'FEMALE', 'images/cafeowner2.jpg'),
(13, 'CAFE_OWNER', 'cafeowner3@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner3', '1987-01-13', 'MALE', 'images/cafeowner3.jpg'),
(14, 'CAFE_OWNER', 'cafeowner4@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner4', '1988-02-14', 'FEMALE', 'images/cafeowner4.jpg'),
(15, 'CAFE_OWNER', 'cafeowner5@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner5', '1989-03-15', 'MALE', 'images/cafeowner5.jpg'),
(16, 'CAFE_OWNER', 'cafeowner6@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner6', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(17, 'CAFE_OWNER', 'cafeowner7@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner7', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(18, 'CAFE_OWNER', 'cafeowner8@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner8', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(19, 'CAFE_OWNER', 'cafeowner9@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner9', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(20, 'CAFE_OWNER', 'cafeowner10@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner10', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(21, 'CAFE_OWNER', 'cafeowner11@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner11', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(22, 'CAFE_OWNER', 'cafeowner12@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner12', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(23, 'CAFE_OWNER', 'cafeowner13@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner13', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(24, 'CAFE_OWNER', 'cafeowner14@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner14', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(25, 'CAFE_OWNER', 'cafeowner15@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner15', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(26, 'CAFE_OWNER', 'cafeowner16@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner16', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(27, 'CAFE_OWNER', 'cafeowner17@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner17', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(28, 'CAFE_OWNER', 'cafeowner18@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner18', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(29, 'CAFE_OWNER', 'cafeowner19@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner19', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(30, 'CAFE_OWNER', 'cafeowner20@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner20', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(31, 'CAFE_OWNER', 'cafeowner21@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner21', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(32, 'CAFE_OWNER', 'cafeowner22@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner22', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(33, 'CAFE_OWNER', 'cafeowner23@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner23', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(34, 'CAFE_OWNER', 'cafeowner24@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner24', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(35, 'CAFE_OWNER', 'cafeowner25@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner25', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(36, 'CAFE_OWNER', 'cafeowner26@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner26', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(37, 'CAFE_OWNER', 'cafeowner27@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner27', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(38, 'CAFE_OWNER', 'cafeowner28@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner28', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(39, 'CAFE_OWNER', 'cafeowner29@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner29', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(40, 'CAFE_OWNER', 'cafeowner30@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner30', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(41, 'CAFE_OWNER', 'cafeowner31@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner31', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(42, 'CAFE_OWNER', 'cafeowner32@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner32', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(43, 'CAFE_OWNER', 'cafeowner33@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner33', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(44, 'CAFE_OWNER', 'cafeowner34@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner34', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(45, 'CAFE_OWNER', 'cafeowner35@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner35', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(46, 'CAFE_OWNER', 'cafeowner36@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner36', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(47, 'CAFE_OWNER', 'cafeowner37@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner37', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(48, 'CAFE_OWNER', 'cafeowner48@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner48', '1990-01-01', 'MALE', 'images/cafe_owner.jpg'),
(49, 'CAFE_OWNER', 'cafeowner49@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner49', '1990-01-01', 'FEMALE', 'images/cafe_owner.jpg'),
(50, 'CAFE_OWNER', 'cafeowner50@example.com', '$2a$10$JxZS.CDhTnMDPdV2cEBKnevOfI7fs9MEnhkm6ujNnjqbudwLwZHpq', 'CafeOwner50', '1990-01-01', 'MALE', 'images/cafe_owner.jpg');

-- Artists
INSERT INTO artist (id, introduction, category) VALUES
(6, 'Introduction for Artist 1', 'LANDSCAPE'),
(7, 'Introduction for Artist 2', 'PORTRAIT'),
(8, 'Introduction for Artist 3', 'STILL_LIFE'),
(9, 'Introduction for Artist 4', 'HISTORY'),
(10, 'Introduction for Artist 5', 'GENRE');

-- Cafe Categories
INSERT INTO cafe_category (id, category_name) VALUES
(1, '모던'),
(2, '빈티지'),
(3, '미니멀'),
(4, '인더스트리얼'),
(5, '클래식'),
(6, '레트로'),
(7, '스칸디나비아'),
(8, '내추럴');

-- Cafes
INSERT INTO cafe (id, cafe_name, address, latitude, longitude, description, contact, business_start_time, business_end_time, daily_visitors, view_count, bookmark_count) VALUES
(11, '너와숲', '경북 구미시 너와숲로 1', 36.11512, 128.42045, '너와숲입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 3),
(12, '벨빌베이크하우스 금오산점', '경북 구미시 벨빌베이크하우스 금오산점로 2', 36.10275, 128.40790, '벨빌베이크하우스 금오산점입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 2),
(13, '페트리코', '경북 구미시 페트리코로 3', 36.10975, 128.42700, '페트리코입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 1),
(14, '민다방', '경북 구미시 민다방로 4', 36.09540, 128.42230, '민다방입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 1),
(15, '언유주얼커피바', '경북 구미시 언유주얼커피바로 5', 36.11800, 128.41010, '언유주얼커피바입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 2),
(16, '매일그대와', '경북 구미시 매일그대와로 6', 36.10890, 128.39980, '매일그대와입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 1),
(17, '어니스트밀로', '경북 구미시 어니스트밀로로 7', 36.12020, 128.41670, '어니스트밀로입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 0),
(18, '금오당', '경북 구미시 금오당로 8', 36.10150, 128.42620, '금오당입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 1),
(19, '런던벨', '경북 구미시 런던벨로 9', 36.11230, 128.40380, '런던벨입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 1),
(20, '도로시홈카페', '경북 구미시 도로시홈카페로 10', 36.10560, 128.42990, '도로시홈카페입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 1),
(21, '커피우드', '경북 구미시 커피우드로 11', 36.09380, 128.41250, '커피우드입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 1),
(22, '빅스타루프탑', '경북 구미시 빅스타루프탑로 12', 36.11750, 128.42410, '빅스타루프탑입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 1),
(23, '구옥희커피', '경북 구미시 구옥희커피로 13', 36.11120, 128.43080, '구옥희커피입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 0),
(24, '모미지', '경북 구미시 모미지로 14', 36.09850, 128.39990, '모미지입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 0),
(25, '파네룬', '경북 구미시 파네룬로 15', 36.10700, 128.41500, '파네룬입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 0),
(26, '카페숨', '경북 구미시 카페숨로 16', 36.10040, 128.41820, '카페숨입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 0),
(27, '호두당', '경북 구미시 호두당로 17', 36.11300, 128.41990, '호두당입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 0),
(28, '카페 호', '경북 구미시 카페 호로 18', 36.11480, 128.40740, '카페 호입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 0),
(29, '스테이550', '경북 구미시 스테이550로 19', 36.09690, 128.43000, '스테이550입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 0),
(30, '돌멩', '경북 구미시 돌멩로 20', 36.10410, 128.40330, '돌멩입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 0),
(31, '스코비하우스', '경북 구미시 스코비하우스로 21', 36.10900, 128.42120, '스코비하우스입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 0),
(32, '여기봉산리', '경북 구미시 여기봉산리로 22', 36.11850, 128.41330, '여기봉산리입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 0),
(33, '언젠가', '경북 구미시 언젠가로 23', 36.11640, 128.42650, '언젠가입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 0),
(34, '브라운핸즈 구미점', '경북 구미시 브라운핸즈 구미점로 24', 36.10200, 128.41380, '브라운핸즈 구미점입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 0),
(35, '올리부치', '경북 구미시 올리부치로 25', 36.10850, 128.40300, '올리부치입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 0),
(36, '오드카페', '경북 구미시 오드카페로 26', 36.09410, 128.41970, '오드카페입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 0),
(37, '보나카페', '경북 구미시 보나카페로 27', 36.09920, 128.42840, '보나카페입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 0),
(38, '시크릿가든카페', '경북 구미시 시크릿가든카페로 28', 36.11570, 128.40900, '시크릿가든카페입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 0),
(39, '카페 코너', '경북 구미시 카페 코너로 29', 36.10500, 128.43050, '카페 코너입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 0),
(40, '어벤더치 카페', '경북 구미시 어벤더치 카페로 30', 36.11090, 128.40420, '어벤더치 카페입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 0),
(41, '카페 STL', '경북 구미시 카페 STL로 31', 36.09980, 128.40560, '카페 STL입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 0),
(42, '상상 커피', '경북 구미시 상상 커피로 32', 36.11280, 128.43120, '상상 커피입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 0),
(43, '카페 십일월삼십일', '경북 구미시 카페 십일월삼십일로 33', 36.09750, 128.40680, '카페 십일월삼십일입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 0),
(44, '머뭄', '경북 구미시 머뭄로 34', 36.11790, 128.41880, '머뭄입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 0),
(45, '카페 무이', '경북 구미시 카페 무이로 35', 36.10330, 128.40020, '카페 무이입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 0),
(46, 'WOOZ', '경북 구미시 WOOZ로 36', 36.10620, 128.42600, 'WOOZ입니다.', '010-1234-5678', '09:00', '22:00', '100-150', 0, 0),
(47, '화담', '경북 구미시 화담로 37', 36.11420, 128.42760, '화담입니다.', '010-1234-5678', '09:00', '22:00', '150-200', 0, 0),
(48, '카페 우드', '경북 구미시 카페 우드로 38', 36.09500, 128.41710, '카페 우드입니다.', '010-1234-5678', '09:00', '22:00', '200-250', 0, 0),
(49, '20도씨', '경북 구미시 20도씨로 39', 36.11000, 128.42270, '20도씨입니다.', '010-1234-5678', '09:00', '22:00', '250-300', 0, 0),
(50, '오드커피', '경북 구미시 오드커피로 40', 36.10110, 128.41120, '오드커피입니다.', '010-1234-5678', '09:00', '22:00', '300+', 0, 0);

-- Cafe Images
INSERT INTO cafe_image (cafe_id, image_url, display_order) VALUES
(11, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(11, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 1),
(11, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 2),
(11, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 3),
(11, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 4),
(12, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(13, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(14, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(15, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(16, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(17, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(18, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(19, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(20, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(21, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(22, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(23, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(24, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(25, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(26, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(27, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(28, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(29, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(30, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(31, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(32, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(33, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(34, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(35, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(36, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(37, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(38, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(39, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(40, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(41, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(42, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(43, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(44, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(45, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(46, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(47, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(48, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(49, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0),
(50, 'cafe/92b23211-e7df-449a-8f9d-881c1dda7fb4', 0);

-- Cafe Category Tags
INSERT INTO cafe_category_tag (cafe_id, category_id) VALUES
(11, 7),
(11, 3),
(12, 7),
(12, 1),
(13, 6),
(13, 4),
(14, 5),
(14, 8),
(15, 5),
(15, 7),
(16, 6),
(16, 5),
(17, 5),
(17, 1),
(18, 4),
(18, 3),
(19, 8),
(19, 5),
(20, 3),
(20, 2),
(21, 6),
(21, 4),
(22, 8),
(22, 1),
(23, 4),
(23, 3),
(24, 2),
(24, 7),
(25, 7),
(25, 4),
(26, 4),
(26, 3),
(27, 3),
(27, 2),
(28, 1),
(28, 5),
(29, 8),
(29, 4),
(30, 1),
(30, 8),
(31, 5),
(31, 7),
(32, 4),
(32, 1),
(33, 5),
(33, 8),
(34, 7),
(34, 3),
(35, 7),
(35, 1),
(36, 4),
(36, 7),
(37, 8),
(37, 1),
(38, 7),
(38, 2),
(39, 6),
(39, 1),
(40, 3),
(40, 7),
(41, 6),
(41, 3),
(42, 7),
(42, 1),
(43, 4),
(43, 1),
(44, 3),
(44, 4),
(45, 5),
(45, 6),
(46, 4),
(46, 7),
(47, 4),
(47, 3),
(48, 7),
(48, 3),
(49, 8),
(49, 1),
(50, 7),
(50, 5);

-- Display Walls
INSERT INTO display_wall (id, cafe_id, wall_name, start_x, start_y, end_x, end_y, width_cm, height_cm, status) VALUES
(101, 11, '복도 벽', 0.24, 0.11, 0.34, 0.35, 683, 272, false),
(102, 11, '안쪽 공간 벽 2', 0.22, 0.17, 0.46, 0.59, 300, 296, true),
(103, 11, '창문 아래 벽', 0.35, 0.33, 0.68, 0.84, 636, 275, true),
(104, 11, '화장실 옆 작은 벽', 0.7, 0.4, 0.26, 0.48, 484, 292, true),
(105, 12, '안쪽 공간 벽 1', 0.81, 0.26, 0.15, 0.79, 625, 208, false),
(106, 12, '창가 긴 벽', 0.8, 0.87, 0.28, 0.29, 763, 292, true),
(107, 12, '룸 2 벽', 0.22, 0.12, 0.72, 0.11, 585, 200, true),
(108, 13, '메인 홀 벽', 0.53, 0.41, 0.7, 0.53, 589, 214, true),
(109, 13, '메인 홀 벽', 0.11, 0.52, 0.34, 0.73, 118, 268, true),
(110, 13, '기둥 옆 벽', 0.15, 0.5, 0.2, 0.28, 800, 300, false),
(111, 13, '입구 정면 벽', 0.69, 0.57, 0.62, 0.38, 550, 192, true),
(112, 14, '입구 정면 벽', 0.53, 0.37, 0.75, 0.48, 225, 232, true),
(113, 14, '입구 좌측 벽', 0.23, 0.27, 0.52, 0.48, 321, 246, true),
(114, 14, '계단 옆 벽', 0.68, 0.25, 0.47, 0.24, 578, 290, true),
(115, 14, '안쪽 공간 벽 1', 0.65, 0.11, 0.3, 0.46, 418, 180, false),
(116, 15, '화장실 옆 작은 벽', 0.74, 0.72, 0.4, 0.17, 765, 234, false),
(117, 15, '룸 2 벽', 0.19, 0.81, 0.69, 0.67, 566, 210, false),
(118, 15, '복도 벽', 0.19, 0.44, 0.34, 0.34, 465, 279, true),
(119, 15, '룸 2 벽', 0.21, 0.71, 0.32, 0.66, 168, 237, true),
(120, 16, '메인 홀 벽', 0.17, 0.84, 0.45, 0.35, 211, 272, true),
(121, 16, '창가 긴 벽', 0.51, 0.21, 0.5, 0.45, 474, 284, true),
(122, 16, '안쪽 공간 벽 2', 0.82, 0.54, 0.68, 0.35, 337, 192, true),
(123, 17, '입구 좌측 벽', 0.74, 0.85, 0.55, 0.37, 280, 216, true),
(124, 17, '기둥 옆 벽', 0.45, 0.74, 0.12, 0.13, 136, 296, true),
(125, 17, '안쪽 공간 벽 2', 0.29, 0.26, 0.3, 0.6, 105, 284, true),
(126, 17, '룸 2 벽', 0.44, 0.4, 0.33, 0.56, 149, 208, true),
(127, 17, '계단 옆 벽', 0.28, 0.18, 0.86, 0.67, 669, 241, true),
(128, 18, '안쪽 공간 벽 2', 0.89, 0.14, 0.48, 0.46, 197, 197, true),
(129, 18, '룸 2 벽', 0.19, 0.31, 0.5, 0.56, 264, 271, false),
(130, 18, '안쪽 공간 벽 2', 0.45, 0.48, 0.76, 0.36, 109, 208, false),
(131, 18, '계단 옆 벽', 0.79, 0.27, 0.18, 0.69, 353, 235, false),
(132, 19, '화장실 옆 작은 벽', 0.24, 0.67, 0.59, 0.13, 161, 193, true),
(133, 19, '화장실 옆 작은 벽', 0.8, 0.1, 0.12, 0.82, 561, 266, true),
(134, 19, '입구 좌측 벽', 0.42, 0.45, 0.63, 0.11, 475, 285, true),
(135, 20, '입구 정면 벽', 0.78, 0.14, 0.48, 0.57, 752, 273, true),
(136, 20, '계단 옆 벽', 0.39, 0.69, 0.75, 0.46, 771, 183, true),
(137, 20, '룸 1 벽', 0.82, 0.66, 0.32, 0.64, 183, 276, false),
(138, 20, '계단 옆 벽', 0.67, 0.64, 0.29, 0.87, 449, 193, true),
(139, 20, '입구 좌측 벽', 0.37, 0.47, 0.62, 0.29, 553, 266, false),
(140, 21, '창가 긴 벽', 0.86, 0.58, 0.71, 0.19, 107, 268, true),
(141, 21, '창가 긴 벽', 0.56, 0.7, 0.61, 0.87, 178, 244, true),
(142, 21, '메인 홀 벽', 0.84, 0.35, 0.71, 0.52, 207, 234, true),
(143, 21, '창문 아래 벽', 0.73, 0.45, 0.64, 0.43, 148, 209, true),
(144, 21, '안쪽 공간 벽 1', 0.44, 0.24, 0.51, 0.76, 723, 202, true),
(145, 22, '메인 홀 벽', 0.35, 0.82, 0.54, 0.73, 627, 185, false),
(146, 22, '입구 좌측 벽', 0.89, 0.89, 0.74, 0.25, 654, 271, true),
(147, 22, '안쪽 공간 벽 1', 0.28, 0.26, 0.71, 0.42, 222, 281, true),
(148, 22, '화장실 옆 작은 벽', 0.29, 0.74, 0.86, 0.27, 574, 245, false),
(149, 22, '안쪽 공간 벽 2', 0.85, 0.66, 0.79, 0.31, 747, 253, true),
(150, 23, '안쪽 공간 벽 2', 0.72, 0.49, 0.2, 0.41, 685, 240, true),
(151, 23, '테라스 벽', 0.88, 0.6, 0.57, 0.32, 349, 188, true),
(152, 23, '창가 긴 벽', 0.58, 0.61, 0.17, 0.51, 218, 186, false),
(153, 23, '입구 좌측 벽', 0.42, 0.79, 0.35, 0.85, 377, 269, true),
(154, 24, '화장실 옆 작은 벽', 0.34, 0.37, 0.31, 0.64, 353, 245, true),
(155, 24, '입구 정면 벽', 0.53, 0.21, 0.36, 0.24, 361, 229, true),
(156, 24, '룸 1 벽', 0.45, 0.55, 0.45, 0.28, 101, 267, true),
(157, 25, '계단 옆 벽', 0.59, 0.15, 0.24, 0.69, 633, 287, true),
(158, 25, '안쪽 공간 벽 2', 0.52, 0.41, 0.44, 0.73, 784, 267, true),
(159, 25, '계단 옆 벽', 0.78, 0.66, 0.13, 0.72, 282, 249, true),
(160, 25, '룸 2 벽', 0.75, 0.33, 0.57, 0.5, 361, 220, false);

-- Display Slots
INSERT INTO display_slot (id, is_exhibiting, commission_rate, requirements) VALUES
(102, true, 12.0, '모던 아트, 추상화 환영'),
(103, true, 7.0, '풍경화, 정물화 선호'),
(104, true, 15.0, '베이커리 카페 분위기에 맞는 따뜻한 느낌의 작품'),
(106, false, 0.0, NULL),
(107, false, 10.0, '계단 벽으로 세로로 긴 작품이나 여러 작품 설치 가능'),
(108, false, 15.0, '테라스와 어울리는 자연주의 작품'),
(109, false, 0.0, NULL),
(111, false, 10.0, '미니멀리즘, 인더스트리얼 작품 선호'),
(112, true, 15.0, '바 테이블 뒷벽으로 작은 사이즈 작품만 가능'),
(113, false, 10.0, '레트로 컨셉의 작품'),
(114, true, 13.0, '긴 벽으로 대형 작품 설치 가능'),
(118, false, 0.0, NULL),
(119, true, 5.0, 'LP 바 컨셉에 맞는 작품 (음악, 영화 등)'),
(120, false, 8.0, '모던 아트, 미니멀리즘 작품 선호'),
(121, true, 15.0, '인물화 가능'),
(122, false, 7.0, '따뜻한 분위기 작품'),
(123, true, 12.0, '추상화 환영'),
(124, false, 3.0, '자연주의 작품'),
(125, false, 9.0, '모던 아트, 감성적인 작품'),
(126, false, 0.0, NULL),
(127, false, 15.0, '풍경화, 대형 작품 가능'),
(128, false, 5.0, '미니멀리즘, 소형 작품 선호'),
(132, false, 2.0, '따뜻한 분위기 작품'),
(133, false, 10.0, '다양한 장르 환영'),
(134, false, 1.0, '조용한 분위기 작품'),
(135, false, 10.0, '따뜻한 분위기 작품'),
(136, false, 8.0, '모던 아트 선호'),
(138, false, 3.0, '따뜻한 분위기 작품'),
(140, false, 4.0, '조용한 분위기 작품'),
(141, false, 9.0, '모던 아트 선호'),
(142, false, 11.0, '대형 작품 가능'),
(143, false, 14.0, '감성적인 작품'),
(144, false, 10.0, '따뜻한 분위기 작품'),
(146, false, 14.0, '미니멀리즘 작품'),
(147, false, 0.0, '다양한 장르 환영'),
(149, false, 7.0, '담백한 작품'),
(150, false, 3.0, '감성적인 작품'),
(151, false, 2.0, '따뜻한 분위기 작품'),
(153, false, 6.0, '미니멀리즘 작품'),
(154, false, 4.0, '자연주의 작품'),
(155, false, 11.0, '따뜻한 분위기 작품'),
(156, false, 2.0, '따뜻한 분위기 작품'),
(157, false, 1.0, '따뜻한 분위기 작품'),
(158, false, 1.0, '따뜻한 분위기 작품'),
(159, false, 1.0, '따뜻한 분위기 작품');

-- Artworks
INSERT INTO artwork (id, artist_id, title, description, art_category, price, width_cm, height_cm, weight_kg, production_year, is_exhibiting, view_count, bookmark_count, created_at) VALUES
(1, 6, '고요한 풍경', '고요한 아침의 호수를 그린 작품입니다.', 'LANDSCAPE', 1500000, 80.3, 60.5, 5.2, 2022, true, 0, 3, now()),
(2, 6, '도시의 밤', '활기찬 도시의 밤 풍경을 담았습니다.', 'LANDSCAPE', 2000000, 100.0, 70.0, 7.0, 2023, true, 0, 2, now()),
(3, 7, '어머니의 초상', '따뜻한 어머니의 모습을 그린 초상화입니다.', 'PORTRAIT', 2500000, 50.0, 70.8, 4.5, 2021, false, 0, 1, now()),
(4, 7, '생각하는 사람', '고뇌에 잠긴 한 남자의 모습을 표현했습니다.', 'PORTRAIT', 3000000, 60.0, 80.0, 6.5, 2022, true, 0, 1, now()),
(5, 8, '사과와 와인', '테이블 위의 사과와 와인병을 그린 정물화입니다.', 'STILL_LIFE', 1200000, 45.5, 35.0, 3.0, 2023, false, 0, 1, now()),
(6, 8, '오래된 책들', '서재에 꽂힌 오래된 책들을 담았습니다.', 'STILL_LIFE', 1800000, 60.0, 40.0, 4.0, 2023, true, 0, 2, now()),
(7, 9, '역사의 한 장면', '중요한 역사적 순간을 재해석하여 그렸습니다.', 'HISTORY', 5000000, 150.0, 100.0, 12.0, 2020, false, 0, 2, now()),
(8, 9, '왕의 행차', '장엄한 왕의 행차를 그린 작품입니다.', 'HISTORY', 7000000, 200.0, 120.0, 15.0, 2021, true, 0, 2, now()),
(9, 10, '시장의 사람들', '활기찬 시장의 모습을 담은 장르화입니다.', 'GENRE', 2200000, 90.0, 60.0, 8.0, 2022, true, 0, 1, now()),
(10, 10, '아이들의 놀이', '해맑게 놀고 있는 아이들의 모습을 그렸습니다.', 'GENRE', 2800000, 100.0, 80.0, 9.5, 2023, true, 0, 1, now()),
(11, 6, '겨울 산', '눈 덮인 겨울 산의 풍경', 'LANDSCAPE', 1800000, 90.0, 65.0, 6.0, 2022, true, 0, 1, now()),
(12, 6, '여름 바다', '푸른 여름 바다의 시원함', 'LANDSCAPE', 1900000, 95.0, 70.0, 6.5, 2022, false, 0, 1, now()),
(13, 6, '가을 단풍', '붉게 물든 가을 단풍의 아름다움', 'LANDSCAPE', 1700000, 85.0, 60.0, 5.5, 2021, false, 0, 1, now()),
(14, 6, '봄의 왈츠', '봄꽃이 만개한 들판', 'LANDSCAPE', 2100000, 100.0, 75.0, 7.2, 2023, false, 0, 1, now()),
(15, 6, '안개 속의 숲', '신비로운 안개 속 숲의 모습', 'LANDSCAPE', 2300000, 110.0, 80.0, 8.0, 2023, false, 0, 0, now()),
(16, 6, '노을 지는 강', '노을 빛으로 물든 강가', 'LANDSCAPE', 1950000, 98.0, 72.0, 6.8, 2022, false, 0, 0, now()),
(17, 6, '별이 빛나는 밤', '수많은 별들이 빛나는 밤하늘', 'LANDSCAPE', 2500000, 120.0, 85.0, 9.0, 2023, false, 0, 1, now()),
(18, 6, '비 오는 거리', '비에 젖은 도시의 거리 풍경', 'LANDSCAPE', 1600000, 80.0, 55.0, 5.0, 2021, false, 0, 1, now()),
(19, 6, '새벽의 여명', '동이 트는 새벽의 순간', 'LANDSCAPE', 2200000, 105.0, 78.0, 7.5, 2023, false, 0, 1, now()),
(20, 6, '초원의 말', '자유롭게 달리는 말들의 모습', 'LANDSCAPE', 3000000, 130.0, 90.0, 10.0, 2022, false, 0, 1, now()),
(21, 6, '해바라기 밭', '태양을 향해 피어있는 해바라기들', 'LANDSCAPE', 1750000, 88.0, 62.0, 5.8, 2021, false, 0, 1, now()),
(22, 6, '오로라', '밤하늘을 수놓은 오로라의 향연', 'LANDSCAPE', 3500000, 140.0, 95.0, 11.0, 2023, false, 0, 0, now()),
(23, 6, '사막의 모래언덕', '바람이 만든 사막의 모래언덕', 'LANDSCAPE', 2800000, 125.0, 88.0, 9.5, 2022, false, 0, 0, now()),
(24, 6, '폭포수', '시원하게 떨어지는 폭포수', 'LANDSCAPE', 2600000, 115.0, 82.0, 8.5, 2023, false, 0, 0, now()),
(25, 6, '대나무 숲', '고요하고 평화로운 대나무 숲', 'LANDSCAPE', 1900000, 92.0, 68.0, 6.2, 2021, false, 0, 0, now()),
(26, 6, '화산', '용암이 분출하는 화산의 모습', 'LANDSCAPE', 4000000, 150.0, 100.0, 12.5, 2023, false, 0, 0, now()),
(27, 6, '빙하', '거대한 빙하의 장엄함', 'LANDSCAPE', 3800000, 145.0, 98.0, 11.8, 2022, false, 0, 0, now()),
(28, 6, '열대우림', '생명이 넘치는 열대우림', 'LANDSCAPE', 3200000, 135.0, 92.0, 10.5, 2023, false, 0, 0, now());

-- Artwork Images
INSERT INTO artwork_image (artwork_id, image_url, display_order) VALUES
(1, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(2, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(3, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(4, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(5, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(6, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(7, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(8, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(9, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(10, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(11, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(12, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(13, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(14, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(15, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(16, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(17, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(18, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(19, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(20, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(21, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(22, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(23, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(24, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(25, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(26, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(27, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0),
(28, 'artwork/86422fac-f18a-41f6-b1d0-62fc1d1db2aa', 0);

-- Artwork Bookmarks
INSERT INTO artwork_bookmark (user_id, artwork_id, created_at) VALUES
-- User 1
(1, 1, NOW()), (1, 2, NOW()), (1, 3, NOW()), (1, 4, NOW()), (1, 5, NOW()),
-- User 2
(2, 6, NOW()), (2, 7, NOW()), (2, 8, NOW()), (2, 9, NOW()), (2, 10, NOW()),
-- User 3
(3, 1, NOW()), (3, 6, NOW()), (3, 11, NOW()), (3, 12, NOW()), (3, 17, NOW()),
-- User 6
(6, 2, NOW()), (6, 7, NOW()), (6, 13, NOW()), (6, 18, NOW()), (6, 19, NOW()),
-- User 11
(11, 1, NOW()), (11, 8, NOW()), (11, 14, NOW()), (11, 20, NOW()), (11, 21, NOW());

-- Cafe Bookmarks
INSERT INTO cafe_bookmark (user_id, cafe_id, created_at) VALUES
-- User 1
(1, 11, NOW()), (1, 15, NOW()), (1, 20, NOW()),
-- User 2
(2, 12, NOW()), (2, 16, NOW()), (2, 21, NOW()),
-- User 6
(6, 11, NOW()), (6, 13, NOW()), (6, 18, NOW()),
-- User 8
(8, 11, NOW()), (8, 15, NOW()), (8, 22, NOW()),
-- User 11
(11, 12, NOW()), (11, 14, NOW()), (11, 19, NOW());

-- Exhibitions
INSERT INTO exhibition (cafe_id, display_slot_id, artwork_id, start_date, end_date, commission_rate, requirements, state, cafe_agreed, artist_agreed, agreed_at, created_at, updated_at) VALUES
-- ONGOING Exhibitions (3)
(11, 102, 1, '2025-11-05', '2025-11-30', 15.0, '모던 아트, 추상화 환영', 'ONGOING', true, true, '2025-10-20 10:00:00', '2025-10-15 09:00:00', '2025-11-05 00:00:00'),
(11, 103, 2, '2025-11-10', '2025-11-30', 15.0, '풍경화, 정물화 선호', 'ONGOING', true, true, '2025-10-25 10:00:00', '2025-10-20 09:00:00', '2025-11-10 00:00:00'),
(11, 104, 11, '2025-11-01', '2025-11-30', 12.0, '베이커리 카페 분위기에 맞는 따뜻한 느낌의 작품', 'ONGOING', true, true, '2025-10-15 10:00:00', '2025-10-10 09:00:00', '2025-11-01 00:00:00'),
-- COMPLETED Exhibitions (10)
(12, 107, 12, '2025-10-21', '2025-11-04', 10.0, '계단 벽으로 세로로 긴 작품이나 여러 작품 설치 가능', 'COMPLETED', true, true, '2025-10-05 10:00:00', '2025-10-01 09:00:00', '2025-11-04 23:59:59'),
(12, 106, 13, '2025-10-14', '2025-10-28', 15.0, '테라스와 어울리는 자연주의 작품', 'COMPLETED', true, true, '2025-09-30 10:00:00', '2025-09-25 09:00:00', '2025-10-28 23:59:59'),
(11, 102, 1, '2025-10-07', '2025-10-21', 15.0, '모던 아트, 추상화 환영', 'COMPLETED', true, true, '2025-09-22 10:00:00', '2025-09-18 09:00:00', '2025-10-21 23:59:59'),
(11, 103, 2, '2025-09-30', '2025-10-14', 15.0, '풍경화, 정물화 선호', 'COMPLETED', true, true, '2025-09-15 10:00:00', '2025-09-10 09:00:00', '2025-10-14 23:59:59'),
(11, 104, 11, '2025-09-23', '2025-10-07', 12.0, '베이커리 카페 분위기에 맞는 따뜻한 느낌의 작품', 'COMPLETED', true, true, '2025-09-08 10:00:00', '2025-09-01 09:00:00', '2025-10-07 23:59:59'),
(12, 107, 12, '2025-09-16', '2025-09-30', 10.0, '계단 벽으로 세로로 긴 작품이나 여러 작품 설치 가능', 'COMPLETED', true, true, '2025-09-01 10:00:00', '2025-08-25 09:00:00', '2025-09-30 23:59:59'),
(12, 106, 13, '2025-09-09', '2025-09-23', 15.0, '테라스와 어울리는 자연주의 작품', 'COMPLETED', true, true, '2025-08-25 10:00:00', '2025-08-20 09:00:00', '2025-09-23 23:59:59'),
(11, 102, 1, '2025-09-02', '2025-09-16', 15.0, '모던 아트, 추상화 환영', 'COMPLETED', true, true, '2025-08-18 10:00:00', '2025-08-12 09:00:00', '2025-09-16 23:59:59'),
(11, 103, 2, '2025-08-26', '2025-09-09', 15.0, '풍경화, 정물화 선호', 'COMPLETED', true, true, '2025-08-11 10:00:00', '2025-08-05 09:00:00', '2025-09-09 23:59:59'),
(11, 104, 11, '2025-08-19', '2025-09-02', 12.0, '베이커리 카페 분위기에 맞는 따뜻한 느낌의 작품', 'COMPLETED', true, true, '2025-08-04 10:00:00', '2025-07-30 09:00:00', '2025-09-02 23:59:59'),
-- APPROVED/PENDING Exhibitions
(12, 107, 3, '2025-11-21', '2025-12-04', 10.0, '계단 벽으로 세로로 긴 작품이나 여러 작품 설치 가능', 'APPROVED', true, true, '2025-11-11 12:00:00', '2025-11-01 09:00:00', '2025-11-11 12:00:00'),
(12, 106, 5, '2025-11-24', '2025-12-09', 15.0, '테라스와 어울리는 자연주의 작품', 'PENDING', true, false, NULL, '2025-11-10 15:00:00', '2025-11-10 15:00:00'),
-- New COMPLETED Exhibition for the chat flow
(13, 108, 3, '2025-10-15', '2025-10-29', 10.0, '새로운 전시 제안입니다.', 'COMPLETED', true, true, '2025-10-12 15:00:00', '2025-10-11 10:00:00', '2025-10-29 23:59:59'),
-- New ONGOING Exhibitions (5)
(14, 112, 4, '2025-11-10', '2025-12-24', 15.0, '바 테이블 뒷벽으로 작은 사이즈 작품만 가능', 'ONGOING', true, true, '2025-11-01 10:00:00', '2025-10-28 09:00:00', '2025-11-10 00:00:00'),
(14, 114, 6, '2025-11-12', '2025-12-26', 13.0, '긴 벽으로 대형 작품 설치 가능', 'ONGOING', true, true, '2025-11-02 11:00:00', '2025-10-29 10:00:00', '2025-11-12 00:00:00'),
(15, 119, 8, '2025-11-15', '2025-12-29', 5.0, 'LP 바 컨셉에 맞는 작품 (음악, 영화 등)', 'ONGOING', true, true, '2025-11-05 12:00:00', '2025-11-01 11:00:00', '2025-11-15 00:00:00'),
(16, 121, 9, '2025-11-17', '2025-12-01', 15.0, '인물화 가능', 'ONGOING', true, true, '2025-11-07 13:00:00', '2025-11-02 12:00:00', '2025-11-17 00:00:00'),
(17, 123, 10, '2025-11-18', '2025-12-02', 12.0, '추상화 환영', 'ONGOING', true, true, '2025-11-08 14:00:00', '2025-11-03 13:00:00', '2025-11-18 00:00:00'),
-- New APPROVED/PENDING Exhibitions (5)
(17, 125, 12, '2025-12-01', '2025-12-15', 9.0, '모던 아트, 감성적인 작품', 'APPROVED', true, true, '2025-11-20 10:00:00', '2025-11-15 09:00:00', '2025-11-20 10:00:00'),
(17, 127, 13, '2025-12-05', '2025-12-19', 15.0, '풍경화, 대형 작품 가능', 'APPROVED', true, true, '2025-11-20 10:00:00', '2025-11-22 11:00:00', '2025-11-22 11:00:00'),
(19, 132, 14, '2025-12-10', '2025-12-24', 2.0, '따뜻한 분위기 작품', 'APPROVED', true, true, '2025-11-25 12:00:00', '2025-11-20 09:00:00', '2025-11-25 12:00:00'),
(19, 134, 15, '2025-12-15', '2025-12-29', 1.0, '조용한 분위기 작품', 'APPROVED', true, true, '2025-11-20 10:00:00', '2025-11-28 13:00:00', '2025-11-28 13:00:00'),
(20, 136, 16, '2025-12-20', '2026-01-03', 8.0, '모던 아트 선호', 'APPROVED', true, true, '2025-11-30 14:00:00', '2025-11-25 09:00:00', '2025-11-30 14:00:00');

-- Reviews
INSERT INTO review (reviewer_id, reviewee_id, exhibition_id, rating, comment, created_at) VALUES
-- Exhibition 4
(11, 6, 6, 5, '훌륭한 작품이었습니다. 다음에도 함께하고 싶네요.', '2025-11-05 10:00:00'),
(6, 11, 6, 4, '전시 공간이 정말 멋졌습니다. 관람객들 반응도 좋았어요.', '2025-11-05 11:00:00'),
-- Exhibition 5
(11, 6, 7, 4, '작품의 깊이가 느껴지는 전시였습니다.', '2025-10-29 10:00:00'),
(6, 11, 7, 5, '카페 분위기와 작품이 잘 어울려 시너지가 좋았습니다.', '2025-10-29 11:00:00'),
-- Exhibition 6
(11, 6, 6, 3, '전시 준비 과정에서 소통이 조금 아쉬웠습니다.', '2025-10-22 10:00:00'),
(6, 11, 6, 5, '사장님이 정말 친절하시고, 전시에 많은 도움을 주셨습니다.', '2025-10-22 11:00:00'),
-- Exhibition 7
(11, 6, 7, 5, '다음에 꼭 다시 전시하고 싶은 작가님입니다.', '2025-10-15 10:00:00'),
(6, 11, 7, 4, '위치가 좋아서 많은 분들이 작품을 보고 가셨습니다.', '2025-10-15 11:00:00'),
-- Exhibition 8
(11, 6, 8, 4, '독창적인 스타일이 돋보이는 전시였습니다.', '2025-10-08 10:00:00'),
(6, 11, 8, 5, '모든 것이 완벽한 전시 경험이었습니다. 감사합니다.', '2025-10-08 11:00:00'),
-- Exhibition 9
(11, 6, 8, 5, '작가님의 다음 작품이 기대됩니다.', '2025-10-01 10:00:00'),
(6, 11, 8, 5, '최고의 전시 파트너입니다.', '2025-10-01 11:00:00'),
-- Exhibition 10
(11, 6, 11, 4, '좋은 작품 감사합니다.', '2025-09-24 10:00:00'),
(6, 11, 11, 4, '덕분에 카페 분위기가 살아났습니다.', '2025-09-24 11:00:00'),
-- Exhibition 11
(11, 6, 11, 5, '믿고 맡기는 작가님입니다.', '2025-09-17 10:00:00'),
(6, 11, 11, 5, '다음 시즌에도 함께하고 싶습니다.', '2025-09-17 11:00:00'),
-- Exhibition 12
(11, 6, 12, 2, '작품 관리가 조금 미흡했습니다.', '2025-09-10 10:00:00'),
(6, 11, 12, 3, '전시 공간에 대한 설명이 부족했습니다.', '2025-09-10 11:00:00'),
-- Exhibition 13
(11, 6, 13, 5, '완벽한 전시였습니다.', '2025-09-03 10:00:00'),
(6, 11, 13, 5, '다음에 또 좋은 기회로 만났으면 좋겠습니다.', '2025-09-03 11:00:00');

-- Chat Rooms
INSERT INTO chat_room (id, created_at, updated_at) VALUES
(1, NOW(), NOW()),
(2, NOW(), NOW()),
(3, NOW(), NOW()),
(4, NOW(), NOW()),
(5, NOW(), NOW()),
(6, NOW(), NOW()),
(7, NOW(), NOW()),
(8, NOW(), NOW()),
(9, NOW(), NOW()),
(10, NOW(), NOW()),
(11, NOW(), NOW()),
(12, NOW(), NOW()),
(13, NOW(), NOW()),
(14, NOW(), NOW()),
(15, NOW(), NOW()),
(16, NOW(), NOW()),
(17, NOW(), NOW()),
(18, NOW(), NOW()),
(19, NOW(), NOW()),
(20, NOW(), NOW());

-- Chat Room Members

-- Artist-CafeOwner Rooms
-- Room 1: Artist 6, CafeOwner 11
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (6, 1, NOW()), (11, 1, NOW());
-- Room 2: Artist 6, CafeOwner 12
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (6, 2, NOW()), (12, 2, NOW());
-- Room 3: Artist 7, CafeOwner 11
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (7, 3, NOW()), (11, 3, NOW());
-- Room 4: Artist 7, CafeOwner 13
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (7, 4, NOW()), (13, 4, NOW());
-- Room 5: Artist 8, CafeOwner 14
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (8, 5, NOW()), (14, 5, NOW());
-- Room 6: Artist 8, CafeOwner 15
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (8, 6, NOW()), (15, 6, NOW());
-- Room 7: Artist 9, CafeOwner 16
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (9, 7, NOW()), (16, 7, NOW());
-- Room 8: Artist 9, CafeOwner 17
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (9, 8, NOW()), (17, 8, NOW());
-- Room 9: Artist 10, CafeOwner 18
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (10, 9, NOW()), (18, 9, NOW());
-- Room 10: Artist 10, CafeOwner 19
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (10, 10, NOW()), (19, 10, NOW());

-- Artist-GeneralUser Rooms
-- Room 11: Artist 6, GeneralUser 1
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (6, 11, NOW()), (1, 11, NOW());
-- Room 12: Artist 7, GeneralUser 2
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (7, 12, NOW()), (2, 12, NOW());
-- Room 13: Artist 8, GeneralUser 3
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (8, 13, NOW()), (3, 13, NOW());

-- CafeOwner-GeneralUser Rooms
-- Room 14: CafeOwner 11, GeneralUser 1
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (11, 14, NOW()), (1, 14, NOW());
-- Room 15: CafeOwner 12, GeneralUser 2
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (12, 15, NOW()), (2, 15, NOW());
-- Room 16: CafeOwner 13, GeneralUser 4
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (13, 16, NOW()), (4, 16, NOW());

-- Additional Artist(6)-CafeOwner & Artist-CafeOwner(11) Rooms
-- Room 17: Artist 6, CafeOwner 22
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (6, 17, NOW()), (22, 17, NOW());
-- Room 18: Artist 8, CafeOwner 11
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (8, 18, NOW()), (11, 18, NOW());
-- Room 19: Artist 6, CafeOwner 23
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (6, 19, NOW()), (23, 19, NOW());
-- Room 20: Artist 9, CafeOwner 11
INSERT INTO chat_room_member (user_id, chat_room_id, last_read_at) VALUES (9, 20, NOW()), (11, 20, NOW());

-- Messages
-- Room 1: Artist 6 (ArtistUser1) and CafeOwner 11 (CafeOwner1)
INSERT INTO message (user_id, chat_room_id, content, message_type, is_read, created_at, exhibition_id) VALUES
(6, 1, '사장님, 안녕하세요. ArtistUser1입니다. 제안서 하나 보냅니다. 검토 부탁드립니다.', 'TEXT', true, '2025-10-05 14:30:00', 1),
(11, 1, '작가님, 안녕하세요. 제안서 잘 받았습니다. 긍정적으로 검토해보겠습니다.', 'TEXT', true, '2025-10-05 16:00:00', null),
(11, 1, '작품 실물을 한번 보고 싶은데, 언제가 괜찮으신가요?', 'TEXT', true, '2025-10-06 10:00:00', null),
(6, 1, '네, 좋습니다. 저는 내일 오후 3시 이후로는 다 괜찮습니다.', 'TEXT', true, '2025-10-06 10:05:00', null),
(11, 1, '그럼 내일 3시에 작가님 작업실로 찾아뵙겠습니다.', 'TEXT', true, '2025-10-06 11:20:00', null),
(6, 1, '사장님, 제 작품 ''고요한 풍경''을 전시하고 싶어 제안 드립니다.', 'TEXT', true, '2025-10-07 10:00:00', 1),
(6, 1, null, 'PROPOSAL', true, '2025-10-07 10:00:00', 1),
(11, 1, '작가님, 제안 감사합니다. 저희 카페와 잘 어울릴 것 같네요 :)', 'TEXT', true, '2025-10-07 11:30:00', null),
(11, 1, null, 'AGREED', true, '2025-10-08 15:00:00', 1),
(11, 1, null, 'STARTED', true, '2025-10-10 09:00:00', 1),
(11, 1, null, 'ENDED', true, '2025-10-25 22:00:00', 1);

-- Room 2: Artist 6 (ArtistUser1) and CafeOwner 12 (CafeOwner2)
INSERT INTO message (user_id, chat_room_id, content, message_type, is_read, created_at) VALUES
(6, 2, '사장님, 안녕하세요. 벨빌베이크하우스에 어울릴 만한 제 작품이 몇 점 있는데, 전시 의향이 있으신지 궁금합니다.', 'TEXT', true, '2025-10-10 11:00:00'),
(12, 2, '안녕하세요, 작가님. 저희 카페에 관심을 가져주셔서 감사합니다. 어떤 작품들인지 볼 수 있을까요?', 'TEXT', true, '2025-10-10 11:30:00'),
(6, 2, '네, 제 프로필에 있는 ''겨울 산''과 ''여름 바다''라는 작품입니다. 한번 보시고 의견 주세요.', 'TEXT', true, '2025-10-10 11:32:00');

-- Room 4: Artist 7 (ArtistUser2) and CafeOwner 13 (CafeOwner3) - Exhibition Flow
INSERT INTO message (user_id, chat_room_id, content, message_type, is_read, created_at, exhibition_id) VALUES
(7, 4, '사장님, 안녕하세요. 페트리코 카페의 메인 홀 벽에 제 작품 ''어머니의 초상''을 전시하고 싶어 제안 드립니다.', 'TEXT', true, '2025-10-11 10:00:00', 16),
(7, 4, null, 'PROPOSAL', true, '2025-10-11 10:00:00', 16),
(13, 4, '작가님, 제안 감사합니다. 저희 카페와 잘 어울릴 것 같네요. 긍정적으로 검토 후 연락드리겠습니다.', 'TEXT', true, '2025-10-11 11:30:00', null),
(13, 4, null, 'AGREED', true, '2025-10-12 15:00:00', 16),
(13, 4, null, 'STARTED', true, '2025-10-15 09:00:00', 16),
(13, 4, null, 'ENDED', true, '2025-10-29 22:00:00', 16);


-- Room 11: Artist 6 (ArtistUser1) and GeneralUser 1
INSERT INTO message (user_id, chat_room_id, content, message_type, is_read, created_at) VALUES
(1, 11, '작가님, 팬입니다! 이번에 전시하신 ''도시의 밤'' 작품 너무 멋져요! 혹시 구매할 수 있나요?', 'TEXT', true, '2025-10-20 18:00:00'),
(6, 11, '안녕하세요. 좋게 봐주셨다니 정말 기쁘네요. 감사합니다! 아쉽지만 그 작품은 현재 판매하지 않고 있습니다. ㅠ.ㅠ', 'TEXT', true, '2025-10-21 09:10:00'),
(1, 11, '아쉽네요. 알겠습니다. 다음 작품도 기대할게요!', 'TEXT', true, '2025-10-21 09:15:00');

-- Room 14: CafeOwner 11 (CafeOwner1) and GeneralUser 1
INSERT INTO message (user_id, chat_room_id, content, message_type, is_read, created_at, exhibition_id) VALUES
(1, 14, '사장님, 너와숲 카페에 지금 전시 중인 작품이 있나요?', 'TEXT', true, '2025-10-25 13:00:00', null),
(11, 14, '네, 안녕하세요. 지금 ArtistUser1 작가님의 ''고요한 풍경'' 작품이 전시 중입니다. 이번달 말까지 전시 예정이에요.', 'TEXT', true, '2025-10-25 13:05:00', 1),
(1, 14, '정보 감사합니다! 보러 가야겠어요.', 'TEXT', true, '2025-10-25 13:07:00', null);