-- Step 1: Create Table

CREATE TABLE tb_url_alias (

                              id              BIGINT GENERATED ALWAYS AS IDENTITY,

                              url             VARCHAR(2048) NOT NULL,

                              short_key       VARCHAR(20) NOT NULL,

                              expired_at      TIMESTAMPTZ,

                              status          VARCHAR(10) NOT NULL DEFAULT '100',

                              version         BIGINT NOT NULL DEFAULT 0,

                              creation_ts     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              last_updated_ts TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,



    -- Table Constraints

                              CONSTRAINT pk_tb_url_alias PRIMARY KEY (id),

                              CONSTRAINT uk_tb_url_alias_short_key UNIQUE (short_key),

                              CONSTRAINT chk_tb_url_alias_status CHECK (status IN ('100', '999'))

);



-- Step 2: Indexes for Query Performance

-- Speed up exact short code lookup on redirects

CREATE INDEX idx_tb_url_alias_short_key ON tb_url_alias(short_key);



-- Partial index for cleanup jobs scanning expired links

CREATE INDEX idx_tb_url_alias_expired_at ON tb_url_alias(expired_at)

    WHERE expired_at IS NOT NULL;