-- ====================================================================
-- FreshConnect - Database Schema (MySQL 8.0+ / MariaDB)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS freshconnect_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE freshconnect_db;

-- 1. Users Table (Donors, NGOs, Admins)
DROP TABLE IF EXISTS donation_history;
DROP TABLE IF EXISTS donation_requests;
DROP TABLE IF EXISTS food_requirements;
DROP TABLE IF EXISTS food_donations;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('DONOR', 'NGO', 'ADMIN', 'BENEFICIARY') NOT NULL DEFAULT 'DONOR',
    phone VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(80),
    organization_name VARCHAR(150),
    organization_type VARCHAR(50), -- restaurant, hotel, bakery, supermarket, catering
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- 2. Food Donations Table
CREATE TABLE food_donations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donor_id BIGINT NOT NULL,
    food_name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL, -- meals, bakery, fruits, vegetables, dairy, packaged
    quantity INT NOT NULL,
    unit VARCHAR(30) NOT NULL, -- packets, meals, kg, pieces, boxes, liters
    food_type ENUM('vegetarian', 'non-vegetarian', 'vegan') NOT NULL DEFAULT 'vegetarian',
    preparation_time DATETIME,
    expiry_time DATETIME NOT NULL,
    pickup_address VARCHAR(255) NOT NULL,
    pickup_city VARCHAR(80),
    available_from DATETIME,
    available_until DATETIME,
    image_url VARCHAR(500),
    instructions TEXT,
    status ENUM('AVAILABLE', 'REQUESTED', 'ACCEPTED', 'COLLECTED', 'COMPLETED', 'EXPIRED', 'CANCELLED') NOT NULL DEFAULT 'AVAILABLE',
    urgency_score DOUBLE DEFAULT 50.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_donation_status (status),
    INDEX idx_donation_expiry (expiry_time),
    INDEX idx_donation_category (category)
) ENGINE=InnoDB;

-- 3. Food Requirements Table (NGO Demands)
CREATE TABLE food_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ngo_id BIGINT NOT NULL,
    food_category VARCHAR(50) NOT NULL,
    food_type VARCHAR(30) DEFAULT 'any',
    quantity INT NOT NULL,
    unit VARCHAR(30) NOT NULL,
    beneficiaries INT DEFAULT 50,
    description TEXT,
    urgency ENUM('low', 'medium', 'high') DEFAULT 'medium',
    status ENUM('ACTIVE', 'FULFILLED', 'PARTIALLY_FULFILLED', 'CANCELLED') DEFAULT 'ACTIVE',
    needed_by DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ngo_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_req_ngo (ngo_id),
    INDEX idx_req_status (status)
) ENGINE=InnoDB;

-- 4. Donation Requests Table (NGO Requests to Donors)
CREATE TABLE donation_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_id BIGINT NOT NULL,
    ngo_id BIGINT NOT NULL,
    requested_quantity INT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'COLLECTED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at DATETIME,
    collected_at DATETIME,
    completed_at DATETIME,
    FOREIGN KEY (donation_id) REFERENCES food_donations(id) ON DELETE CASCADE,
    FOREIGN KEY (ngo_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_request_status (status)
) ENGINE=InnoDB;

-- 5. Donation History & Activity Log Table
CREATE TABLE donation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_id BIGINT,
    request_id BIGINT,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL, -- CREATED, REQUESTED, ACCEPTED, REJECTED, COLLECTED, COMPLETED, EXPIRED
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES food_donations(id) ON DELETE SET NULL,
    FOREIGN KEY (request_id) REFERENCES donation_requests(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_history_timestamp (timestamp)
) ENGINE=InnoDB;

