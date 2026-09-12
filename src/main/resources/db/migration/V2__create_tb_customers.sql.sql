-- Create role enum type
CREATE TYPE user_role AS ENUM ('admin', 'customer');

-- Create customers table
CREATE TABLE tb_customers (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           email VARCHAR(255) NOT NULL UNIQUE,
                           username VARCHAR(50) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           role user_role NOT NULL DEFAULT 'customer',
                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index to optimize fetching users by username
CREATE INDEX idx_customers_username ON tb_customers(username);

-- Lowercase index for case-insensitive lookup (recommended for logins)
CREATE INDEX idx_customers_username_lower ON tb_customers(LOWER(username));