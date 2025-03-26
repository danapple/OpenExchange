CREATE TABLE IF NOT EXISTS order_states (
  orderId BIGINT PRIMARY KEY REFERENCES orders,
  orderStatus VARCHAR
);