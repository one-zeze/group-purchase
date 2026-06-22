CREATE TABLE tb_order (
    order_id VARCHAR(36) PRIMARY KEY,
    participation_id VARCHAR(36) NOT NULL,
    groupbuy_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    total_price NUMERIC(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPZ,
    updated_at TIMESTAMPZ
);

CREATE TABLE tb_payment (
    payment_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPZ,
    updated_at TIMESTAMPZ
);