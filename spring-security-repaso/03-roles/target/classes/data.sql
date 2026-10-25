INSERT INTO products (name, price) VALUES
  ('Laptop Lenovo', 3200.00),
  ('Mouse inalámbrico', 45.90),
  ('Teclado mecánico', 189.50);

-- admin / admin123
-- user  / user123
INSERT INTO users (username, password, role) VALUES
  ('admin', '$2a$10$lnVu6vYzATv0r9O9oBwBbehY5aFxNCmPq6rWza58cerz0FG/qGl1O', 'ADMIN'),
  ('user',  '$2a$10$AdN.RfqjYNPOTs44k8Xm5uOosPDCApXzIXI0xxRjmeEc/Lb6E8AmO', 'USER');
