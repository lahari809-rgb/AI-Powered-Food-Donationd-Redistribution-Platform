-- ====================================================================
-- FreshConnect - Seed Data (MySQL 8.0+)
-- ====================================================================

USE freshconnect_db;

-- Insert Users
INSERT INTO users (id, name, email, password, role, phone, address, city, organization_name, organization_type, description, active) VALUES
(1, 'Admin FreshConnect', 'admin@freshconnect.com', 'admin123', 'ADMIN', '9876543200', '100 Tech Park, MG Road', 'Bengaluru', 'FreshConnect Platform', 'admin', 'Platform administrator account', true),
(2, 'John Miller', 'john@johnsbistro.com', 'password123', 'DONOR', '9876543210', '45 Indiranagar 100ft Rd', 'Bengaluru', 'John\'s Bistro', 'restaurant', 'Popular casual dining bistro specializing in gourmet meals', true),
(3, 'Rajesh Kumar', 'rajesh@tajhotel.com', 'password123', 'DONOR', '9876543211', 'MG Road', 'Bengaluru', 'Taj Hotel', 'hotel', '5-star hotel with surplus banquet and buffet food', true),
(4, 'Priya Sharma', 'priya@freshbakes.com', 'password123', 'DONOR', '9876543212', 'Koramangala 4th Block', 'Bengaluru', 'Fresh Bakes Bakery', 'bakery', 'Artisan bakery with daily surplus fresh bread and pastries', true),
(5, 'Amit Patel', 'amit@greenmart.com', 'password123', 'DONOR', '9876543213', 'HSR Layout Sector 2', 'Bengaluru', 'GreenMart Supermarket', 'supermarket', 'Fresh organic produce and grocery supermarket', true),
(6, 'Anita Desai', 'anita@hopefoundation.org', 'password123', 'NGO', '9876543220', 'Whitefield Main Rd', 'Bengaluru', 'Hope Foundation', NULL, 'Serves nutritious meals to 850+ underprivileged people daily', true),
(7, 'Vikram Singh', 'vikram@akshayaseva.org', 'password123', 'NGO', '9876543221', 'Jayanagar 4th Block', 'Bengaluru', 'Akshaya Seva Trust', NULL, 'Dedicated to hunger relief for urban slums and shelter homes', true),
(8, 'Sneha Roy', 'sneha@careshare.org', 'password123', 'NGO', '9876543222', 'Malleshwaram', 'Bengaluru', 'Care & Share Society', NULL, 'Community food bank helping elderly care homes and orphanages', true),
(9, 'Ramesh Kumar', 'ramesh@community.org', 'password123', 'BENEFICIARY', '9876543230', 'Slum Settlement Sector 4', 'Bengaluru', 'Family Recipient', 'individual', 'Seeking daily lunch meal support for family of 4', true),
(10, 'Fatima Begum', 'fatima@community.org', 'password123', 'BENEFICIARY', '9876543231', 'Shivaji Nagar Community', 'Bengaluru', 'Senior Citizen', 'individual', 'Senior citizen requiring hot nutritional meal assistance', true);

-- Insert Food Donations
INSERT INTO food_donations (id, donor_id, food_name, category, quantity, unit, food_type, preparation_time, expiry_time, pickup_address, pickup_city, available_from, available_until, instructions, status, urgency_score) VALUES
(1, 2, 'Veg Meal Packets', 'meals', 100, 'packets', 'vegetarian', NOW() - INTERVAL 1 HOUR, NOW() + INTERVAL 4 HOUR, 'John\'s Bistro, 45 Indiranagar', 'Bengaluru', NOW(), NOW() + INTERVAL 4 HOUR, 'Cooked fresh at lunch, packed in thermal safe containers', 'AVAILABLE', 88.0),
(2, 4, 'Bread Rolls', 'bakery', 100, 'pieces', 'vegetarian', NOW() - INTERVAL 5 HOUR, NOW() + INTERVAL 1 HOUR, 'Fresh Bakes, Koramangala', 'Bengaluru', NOW(), NOW() + INTERVAL 1 HOUR, 'Fresh morning bake, best consumed today', 'AVAILABLE', 92.0),
(3, 3, 'Gourmet Buffet Rice & Curry', 'meals', 80, 'meals', 'vegetarian', NOW() - INTERVAL 2 HOUR, NOW() + INTERVAL 5 HOUR, 'Taj Hotel, MG Road', 'Bengaluru', NOW(), NOW() + INTERVAL 5 HOUR, 'Surplus banquet lunch spread', 'AVAILABLE', 75.0),
(4, 5, 'Fresh Farm Apples & Bananas', 'fruits', 40, 'kg', 'vegan', NOW() - INTERVAL 12 HOUR, NOW() + INTERVAL 24 HOUR, 'GreenMart, HSR Layout', 'Bengaluru', NOW(), NOW() + INTERVAL 24 HOUR, 'Ripe fresh produce', 'AVAILABLE', 45.0),
(5, 5, 'Mixed Seasonal Vegetables', 'vegetables', 60, 'kg', 'vegan', NOW() - INTERVAL 8 HOUR, NOW() + INTERVAL 18 HOUR, 'GreenMart, HSR Layout', 'Bengaluru', NOW(), NOW() + INTERVAL 18 HOUR, 'Carrots, beans, spinach, and potatoes', 'AVAILABLE', 55.0),
(6, 2, 'North Indian Dinner Thali', 'meals', 50, 'packets', 'vegetarian', NOW() - INTERVAL 24 HOUR, NOW() - INTERVAL 20 HOUR, 'John\'s Bistro, Indiranagar', 'Bengaluru', NOW() - INTERVAL 24 HOUR, NOW() - INTERVAL 20 HOUR, 'Completed delivery to Hope Foundation', 'COMPLETED', 20.0),
(7, 4, 'Assorted Sandwich Loaves', 'bakery', 60, 'pieces', 'vegetarian', NOW() - INTERVAL 48 HOUR, NOW() - INTERVAL 42 HOUR, 'Fresh Bakes, Koramangala', 'Bengaluru', NOW() - INTERVAL 48 HOUR, NOW() - INTERVAL 42 HOUR, 'Distributed to Akshaya Seva', 'COMPLETED', 15.0);

-- Insert Food Requirements (NGO Demands)
INSERT INTO food_requirements (id, ngo_id, food_category, food_type, quantity, unit, beneficiaries, description, urgency, status, needed_by) VALUES
(1, 6, 'meals', 'vegetarian', 500, 'packets', 500, 'Need dinner meals for local urban shelter residents', 'high', 'ACTIVE', NOW() + INTERVAL 1 DAY),
(2, 6, 'bakery', 'vegetarian', 100, 'pieces', 150, 'Breakfast rolls for children care center', 'medium', 'ACTIVE', NOW() + INTERVAL 2 DAY),
(3, 7, 'vegetables', 'vegan', 80, 'kg', 250, 'Fresh vegetables for community soup kitchen', 'high', 'ACTIVE', NOW() + INTERVAL 1 DAY);

-- Insert Requests
INSERT INTO donation_requests (id, donation_id, ngo_id, requested_quantity, status, message, created_at, accepted_at, collected_at, completed_at) VALUES
(1, 1, 6, 100, 'PENDING', 'We have a volunteer van ready for collection at 2 PM.', NOW() - INTERVAL 30 MINUTE, NULL, NULL, NULL),
(2, 2, 7, 50, 'ACCEPTED', 'Collection team dispatched.', NOW() - INTERVAL 45 MINUTE, NOW() - INTERVAL 20 MINUTE, NULL, NULL),
(3, 6, 6, 50, 'COMPLETED', 'Successfully distributed to 100 individuals.', NOW() - INTERVAL 25 HOUR, NOW() - INTERVAL 24 HOUR, NOW() - INTERVAL 22 HOUR, NOW() - INTERVAL 21 HOUR);

-- Insert History Log
INSERT INTO donation_history (id, donation_id, request_id, user_id, action, details, timestamp) VALUES
(1, 6, 3, 2, 'COMPLETED', '100 Veg Meal Packets distributed to Hope Foundation', NOW() - INTERVAL 21 HOUR),
(2, 2, 2, 7, 'ACCEPTED', '100 Bread Rolls accepted by Helping Hands / Care Trust', NOW() - INTERVAL 5 HOUR),
(3, 1, 1, 6, 'REQUESTED', '50 Meal Packets requested by Hope Foundation', NOW() - INTERVAL 3 HOUR),
(4, 1, NULL, 2, 'CREATED', 'New donation: 100 Meal Packets listed by John\'s Bistro', NOW() - INTERVAL 1 HOUR);

