CREATE TABLE IF NOT EXISTS order_base (
  orderId BIGINT PRIMARY KEY,
  customerId BIGINT NOT NULL,
  clientOrderId BIGINT NOT NULL,
  instrumentId BIGINT NOT NULL,
  price FLOAT NOT NULL,
  quantity INT NOT NULL
);

CREATE UNIQUE INDEX unq_customer_order ON order_base (customerId, clientOrderId);
