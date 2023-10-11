CREATE TABLE IF NOT EXISTS "system_oauth2_access_token" (
    "id" bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    "user_id" bigint NOT NULL,
--     "user_type" tinyint NOT NULL,
    "access_token" varchar NOT NULL,
    "refresh_token" varchar NOT NULL,
--     "client_id" varchar NOT NULL,
--     "scopes" varchar NOT NULL,
--     "approved" bit NOT NULL DEFAULT FALSE,
    "expires_time" datetime NOT NULL,
    "creator" varchar DEFAULT '',
    "create_time" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" varchar DEFAULT '',
    "update_time" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    "deleted" bit NOT NULL DEFAULT FALSE,
--     "tenant_id" bigint NOT NULL,
    PRIMARY KEY ("id")
) COMMENT 'OAuth2 访问令牌';

CREATE TABLE IF NOT EXISTS "system_oauth2_refresh_token" (
    "id" bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    "user_id" bigint NOT NULL,
    "user_type" tinyint NOT NULL,
    "refresh_token" varchar NOT NULL,
    "client_id" varchar NOT NULL,
    "scopes" varchar NOT NULL,
    "approved" bit NOT NULL DEFAULT FALSE,
    "expires_time" datetime NOT NULL,
    "creator" varchar DEFAULT '',
    "create_time" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" varchar DEFAULT '',
    "update_time" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    "deleted" bit NOT NULL DEFAULT FALSE,
    PRIMARY KEY ("id")
) COMMENT 'OAuth2 刷新令牌';