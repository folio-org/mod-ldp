CREATE TABLE IF NOT EXISTS public.user_users(
    id VARCHAR(36) NOT NULL,
    active BOOL,
    barcode VARCHAR(19),
    created_date TIMESTAMP,
    enrollment_date TIMESTAMP,
    expiration_date TIMESTAMP,
    patron_group VARCHAR(36),
    "type" VARCHAR(6),
    updated_date TIMESTAMP,
    username VARCHAR(23),
    data JSON
);


