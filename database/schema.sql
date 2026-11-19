CREATE DATABASE IF NOT EXISTS lab_inventory;
USE lab_inventory;

-- Drop tables if they exist to apply new schema without conflicts
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS components;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS admin_users;

CREATE TABLE admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    usn VARCHAR(20) NOT NULL UNIQUE,
    branch VARCHAR(50) NOT NULL,
    qr_code_text VARCHAR(255) UNIQUE
);

CREATE TABLE components (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    qr_code_text VARCHAR(255) UNIQUE
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_usn VARCHAR(20) NOT NULL,
    component_id VARCHAR(50) NOT NULL,
    issue_time DATETIME NOT NULL,
    return_time DATETIME,
    status VARCHAR(20) NOT NULL, -- 'ISSUED', 'RETURNED'
    FOREIGN KEY (student_usn) REFERENCES students(usn),
    FOREIGN KEY (component_id) REFERENCES components(component_id)
);

-- Insert default admin (password: admin)
INSERT IGNORE INTO admin_users (username, password) VALUES ('admin', 'admin');

-- Insert sample students
INSERT IGNORE INTO students (name, usn, branch, qr_code_text) VALUES
('Alice Smith', '1DS20EC001', 'ECE', 'STD_1DS20EC001'),
('Bob Jones', '1DS20EC002', 'ECE', 'STD_1DS20EC002'),
('Charlie Brown', '1DS20EC003', 'ECE', 'STD_1DS20EC003'),
('Diana Prince', '1DS20EC004', 'ECE', 'STD_1DS20EC004'),
('Eve Davis', '1DS20EC005', 'ECE', 'STD_1DS20EC005');

-- Insert sample components
INSERT IGNORE INTO components (component_id, name, quantity, qr_code_text) VALUES
('COMP001', 'Arduino Uno', 10, 'COMP_001'),
('COMP002', 'Raspberry Pi 4', 5, 'COMP_002'),
('COMP003', 'Breadboard', 20, 'COMP_003'),
('COMP004', 'Multimeter', 15, 'COMP_004'),
('COMP005', 'Oscilloscope', 2, 'COMP_005');
