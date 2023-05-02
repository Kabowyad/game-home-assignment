CREATE TABLE players (
  id BIGINT AUTO_INCREMENT NOT NULL,
   login VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   last_auth_date date NULL,
   registration_date date NOT NULL,
   CONSTRAINT pk_players PRIMARY KEY (id)
);

ALTER TABLE players ADD CONSTRAINT uc_players_login UNIQUE (login);

CREATE TABLE games (
  id BIGINT AUTO_INCREMENT NOT NULL,
   player_id BIGINT NOT NULL,
   player_choice VARCHAR(255) NULL,
   computer_choice VARCHAR(255) NULL,
   in_progress BIT(1) NOT NULL,
   game_result VARCHAR(255) NULL,
   current_step VARCHAR(255) NOT NULL,
   time_remaining INT NOT NULL,
   CONSTRAINT pk_games PRIMARY KEY (id)
);

ALTER TABLE games ADD CONSTRAINT FK_GAMES_ON_PLAYER FOREIGN KEY (player_id) REFERENCES players (id);
