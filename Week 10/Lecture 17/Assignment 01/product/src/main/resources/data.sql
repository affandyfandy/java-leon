-- Initialize table with DDLs
-- Create `Product` table
CREATE TABLE Product (
    ID VARCHAR(36) PRIMARY KEY,    -- Use VARCHAR for UUIDs
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    status VARCHAR(50) NOT NULL,   -- Use VARCHAR instead of ENUM
    quantity INT,
    createdAt TIMESTAMP,
    updatedAt TIMESTAMP
);

-- Create `CustomerProduct` table
CREATE TABLE CustomerProduct (
    id VARCHAR(36) PRIMARY KEY, -- Unique identifier for each record
    customerId VARCHAR(36) NOT NULL, -- ID of the customer
    productId VARCHAR(36) NOT NULL, -- ID of the product
    quantity INT NOT NULL, -- Quantity of the product purchased
    purchasedAt TIMESTAMP, -- Timestamp of purchase
    FOREIGN KEY (customerId) REFERENCES Customer(ID)
);

-- Insert 20 products
INSERT INTO Product (ID, name, price, status, quantity, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Product A', 100, 'ACTIVE', 50, NOW(), NOW()),
('22222222-2222-2222-2222-222222222222', 'Product B', 200, 'ACTIVE', 40, NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', 'Product C', 300, 'ACTIVE', 30, NOW(), NOW()),
('44444444-4444-4444-4444-444444444444', 'Product D', 400, 'ACTIVE', 20, NOW(), NOW()),
('55555555-5555-5555-5555-555555555555', 'Product E', 500, 'ACTIVE', 10, NOW(), NOW()),
('66666666-6666-6666-6666-666666666666', 'Product F', 150, 'ACTIVE', 60, NOW(), NOW()),
('77777777-7777-7777-7777-777777777777', 'Product G', 250, 'ACTIVE', 70, NOW(), NOW()),
('88888888-8888-8888-8888-888888888888', 'Product H', 350, 'ACTIVE', 80, NOW(), NOW()),
('99999999-9999-9999-9999-999999999999', 'Product I', 450, 'ACTIVE', 90, NOW(), NOW()),
('00000000-0000-0000-0000-000000000000', 'Product J', 550, 'ACTIVE', 100, NOW(), NOW()),
('11112222-3333-4444-5555-666677778888', 'Product K', 120, 'ACTIVE', 110, NOW(), NOW()),
('22223333-4444-5555-6666-777788889999', 'Product L', 220, 'ACTIVE', 120, NOW(), NOW()),
('33334444-5555-6666-7777-888899990000', 'Product M', 320, 'ACTIVE', 130, NOW(), NOW()),
('44445555-6666-7777-8888-999900001111', 'Product N', 420, 'ACTIVE', 140, NOW(), NOW()),
('55556666-7777-8888-9999-000011112222', 'Product O', 520, 'ACTIVE', 150, NOW(), NOW()),
('66667777-8888-9999-0000-111122223333', 'Product P', 170, 'ACTIVE', 160, NOW(), NOW()),
('77778888-9999-0000-1111-222233334444', 'Product Q', 270, 'ACTIVE', 170, NOW(), NOW()),
('88889999-0000-1111-2222-333344445555', 'Product R', 370, 'ACTIVE', 180, NOW(), NOW()),
('99990000-1111-2222-3333-444455556666', 'Product S', 470, 'ACTIVE', 190, NOW(), NOW()),
('00001111-2222-3333-4444-555566667777', 'Product T', 570, 'ACTIVE', 200, NOW(), NOW());

-- Insert 10 CustomerProduct
INSERT INTO customer_product (id, customer_id, product_id, quantity, purchase_date) VALUES
('e1f2g3h4-i5j6-7890-k1lm-n23456789012', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', '11111111-1111-1111-1111-111111111111', 1, NOW()),
('e2f3g4h5-j6k7-8901-l2mn-o34567890123', 'a2b3c4d5-e6f7-8901-bcde-f12345678901', '22222222-2222-2222-2222-222222222222', 2, NOW()),
('e3f4g5h6-k7l8-9012-m3no-p45678901234', 'a3b4c5d6-e7f8-9012-cdef-123456789012', '33333333-3333-3333-3333-333333333333', 3, NOW()),
('e4f5g6h7-l8m9-0123-n4op-q56789012345', 'a4b5c6d7-e8f9-0123-def0-234567890123', '44444444-4444-4444-4444-444444444444', 4, NOW()),
('e5f6g7h8-m9n0-1234-o5pq-r67890123456', 'a5b6c7d8-e9f0-1234-ef01-345678901234', '55555555-5555-5555-5555-555555555555', 5, NOW()),
('e6f7g8h9-n0o1-2345-p6qr-s78901234567', 'a6b7c8d9-f0a1-2345-f012-456789012345', '66666666-6666-6666-6666-666666666666', 1, NOW()),
('e7f8g9h0-o1p2-3456-q7rs-t89012345678', 'a7b8c9d0-0a1b-3456-0123-567890123456', '77777777-7777-7777-7777-777777777777', 2, NOW()),
('e8f9g0h1-p2q3-4567-r8st-u90123456789', 'a8b9c0d1-1a2b-4567-1234-678901234567', '88888888-8888-8888-8888-888888888888', 3, NOW()),
('e9f0g1h2-q3r4-5678-s9tu-v01234567890', 'a9b0c1d2-2a3b-5678-2345-789012345678', '99999999-9999-9999-9999-999999999999', 4, NOW()),
('f0g1h2i3-r4s5-6789-t0uv-w12345678901', 'b0c1d2e3-3a4b-6789-3456-890123456789', '00000000-0000-0000-0000-000000000000', 5, NOW());