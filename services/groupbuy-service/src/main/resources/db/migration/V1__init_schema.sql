CREATE TABLE product (
    product_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    initial_stock INT NOT NULL,
    created_at TIMESTAMPZ,
    updated_at TIMESTAMPZ
);

CREATE TABLE groupbuy (
    groupbuy_id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL REFERENCES product(product_id),
    status VARCHAR(20) NOT NULL,
    target_quantity INT NOT NULL,
    current_quantity INT DEFAULT 0,
    start_at TIMESTAMPZ,
    end_at TIMESTAMPZ,
    created_at TIMESTAMPZ,
    updated_at TIMESTAMPZ
);

CREATE TABLE participation(
    participation_id VARCHAR(36) PRIMARY KEY,
    groupbuy_id VARCHAR(36) NOT NULL REFERENCES groupbuy(groupbuy_id),
    product_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPZ
);