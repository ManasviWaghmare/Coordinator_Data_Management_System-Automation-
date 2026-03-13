-- Add full_name and email to students table to simplify management
ALTER TABLE students ADD COLUMN IF NOT EXISTS full_name VARCHAR(100);
ALTER TABLE students ADD COLUMN IF NOT EXISTS email VARCHAR(100);

-- Copy data from users if exists (optional, but good for data integrity)
UPDATE students s SET 
    full_name = u.full_name,
    email = u.email
FROM users u 
WHERE s.user_id = u.id AND s.full_name IS NULL;
