
-- 게임 테이블
CREATE TABLE IF NOT EXISTS games (
    game_id VARCHAR(36) PRIMARY KEY
    , game_type_id INT
    , channel_name VARCHAR(50)
    , started_at DATETIME
    , ended_at DATETIME
    , cancel_yn VARCHAR(1)
)

-- 게임 타입 테이블
CREATE TABLE IF NOT EXISTS game_types (
    game_type_id INT PRIMARY KEY
    , type_name VARCHAR(20)
)

-- 게임 참여자 테이블
CREATE TABLE IF NOT EXISTS game_participants (
    game_id VARCHAR(36)
    , player_uuid VARCHAR(36)
    , PRIMARY KEY(game_id, player_uuid)
)

-- 게임 킬 기록 테이블
CREATE TABLE IF NOT EXISTS game_kills (
    game_id VARCHAR(36)
    , killer_player_uuid VARCHAR(36)
    , dead_player_uuid VARCHAR(36)
    , killed_at DATETIME
)

-- 게임 우승자 테이블
CREATE TABLE IF NOT EXISTS game_winners (
    game_id VARCHAR(36)
    , winner_player_uuid VARCHAR(36)
    , PRIMARY KEY(game_id, winner_player_uuid)
)

-- 게임별 전적 테이블
CREATE TABLE IF NOT EXISTS game_player_stats (
    player_uuid VARCHAR(36)
    , game_type_id INT
    , kill_count INT
    , death_count INT
    , assist_count INT
    , win_count INT
    , PRIMARY KEY(player_uuid, game_type_id)
)

--=======================================================================================================================

-- 진행 중인 게임 (끝난 시간이 없거나 3시간 안지난 게임)
SELECT game_id FROM games WHERE end_time IS NULL OR end_time >= DATE_SUB(NOW(), INTERVAL 60 * 60 * 3 SECOND)

-- 플레이어가 참여 중인 게임 ID
SELECT *
FROM game, game_participants participant
WHERE (game.end_time IS NULL OR game.end_time >= DATE_SUB(NOW(), INTERVAL 60 * 60 * 3 SECOND)
    AND game.game_id = participant.game_id
    AND participant.player_uuid = #{playerUuid}