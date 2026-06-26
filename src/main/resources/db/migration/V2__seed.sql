-- Plans: one row per billing cycle.
INSERT INTO plans (billing_cycle, price) VALUES
    ('MONTHLY', 299.00),
    ('QUARTERLY', 799.00),
    ('YEARLY', 2499.00);

-- Tiers ordered by level (higher = better).
INSERT INTO tiers (name, level) VALUES
    ('SILVER', 1),
    ('GOLD', 2),
    ('PLATINUM', 3);

-- Configurable perks per tier.
INSERT INTO benefits (tier_id, type, benefit_value) VALUES
    ((SELECT id FROM tiers WHERE name = 'SILVER'),   'FREE_DELIVERY',    'true'),
    ((SELECT id FROM tiers WHERE name = 'SILVER'),   'DISCOUNT_PERCENT', '5'),
    ((SELECT id FROM tiers WHERE name = 'GOLD'),     'FREE_DELIVERY',    'true'),
    ((SELECT id FROM tiers WHERE name = 'GOLD'),     'DISCOUNT_PERCENT', '10'),
    ((SELECT id FROM tiers WHERE name = 'GOLD'),     'EXCLUSIVE_DEALS',  'true'),
    ((SELECT id FROM tiers WHERE name = 'PLATINUM'), 'FREE_DELIVERY',    'true'),
    ((SELECT id FROM tiers WHERE name = 'PLATINUM'), 'DISCOUNT_PERCENT', '15'),
    ((SELECT id FROM tiers WHERE name = 'PLATINUM'), 'EXCLUSIVE_DEALS',  'true'),
    ((SELECT id FROM tiers WHERE name = 'PLATINUM'), 'PRIORITY_SUPPORT', 'true');

-- Qualification thresholds per tier. SILVER is the entry tier (always satisfied).
INSERT INTO tier_criteria (tier_id, min_order_count, min_monthly_spend, required_cohort) VALUES
    ((SELECT id FROM tiers WHERE name = 'SILVER'),   0,  0.00,     NULL),
    ((SELECT id FROM tiers WHERE name = 'GOLD'),     5,  5000.00,  NULL),
    ((SELECT id FROM tiers WHERE name = 'PLATINUM'), 15, 20000.00, 'PREMIUM');

-- Sample users for demoing the APIs.
INSERT INTO users (name, email, cohort, created_at) VALUES
    ('Alice Regular', 'alice@example.com', 'REGULAR', CURRENT_TIMESTAMP),
    ('Bob Premium',   'bob@example.com',   'PREMIUM', CURRENT_TIMESTAMP);

INSERT INTO membership_activity (user_id, order_count, monthly_spend, total_spend, window_start) VALUES
    ((SELECT id FROM users WHERE email = 'alice@example.com'), 0, 0.00, 0.00, CURRENT_DATE),
    ((SELECT id FROM users WHERE email = 'bob@example.com'),   0, 0.00, 0.00, CURRENT_DATE);