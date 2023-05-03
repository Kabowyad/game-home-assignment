CREATE TABLE players (
  id BIGINT AUTO_INCREMENT NOT NULL,
   login VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   last_auth_date datetime NULL,
   registration_date datetime NOT NULL,
   CONSTRAINT pk_players PRIMARY KEY (id)
);

ALTER TABLE players ADD CONSTRAINT uc_players_login UNIQUE (login);

CREATE TABLE games (
  id BIGINT AUTO_INCREMENT NOT NULL,
   player_id BIGINT NOT NULL,
   in_progress BIT(1) NOT NULL,
   game_result VARCHAR(255) NULL,
   timer_started datetime NULL,
   moves_passed INT NULL,
   player_points INT NULL,
   time_left INT NULL,
   CONSTRAINT pk_games PRIMARY KEY (id)
);

ALTER TABLE games ADD CONSTRAINT FK_GAMES_ON_PLAYER FOREIGN KEY (player_id) REFERENCES players (id);
