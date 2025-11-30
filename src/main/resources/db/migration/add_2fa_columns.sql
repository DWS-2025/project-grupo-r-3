-- SQL Migration Script for 2FA Implementation
-- Run this script if you want to preserve existing data instead of restarting with create-drop

-- Add two_factor_secret column to user table
ALTER TABLE user ADD COLUMN two_factor_secret VARCHAR(255);

-- Add is_two_factor_enabled column to user table
ALTER TABLE user ADD COLUMN is_two_factor_enabled BOOLEAN DEFAULT FALSE;

-- Optional: Set default values for existing users
UPDATE user SET is_two_factor_enabled = FALSE WHERE is_two_factor_enabled IS NULL;
