-- Fix Admin Password to 'admin123'
UPDATE users 
SET password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC' 
WHERE username = 'admin';
