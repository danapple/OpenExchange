CREATE TABLE IF NOT EXISTS order_state (
  orderId BIGINT PRIMARY KEY REFERENCES order_base,
  orderStatus VARCHAR
);