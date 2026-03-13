-- Recreate Admin User to ensure clean password hash
SET search_path TO tic_club;

DELETE FROM users WHERE username = 'admin';

INSERT INTO users (username, password, email, full_name, role_id)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC', 'admin@ticclub.com', 'System Admin', 3);
