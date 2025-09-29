-- Tabla de clientes
CREATE TABLE cliente (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         identificacion VARCHAR(20) NOT NULL UNIQUE,
                         nombre VARCHAR(100) NOT NULL,
                         genero CHAR(1) NOT NULL,
                         edad INT NOT NULL,
                         direccion VARCHAR(200),
                         telefono VARCHAR(20),
                         contrasenia VARCHAR(255),
                         estado BOOLEAN NOT NULL
);

-- Tabla de cuentas
CREATE TABLE cuenta (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
                        tipo_cuenta VARCHAR(20) NOT NULL, -- AHORROS, CORRIENTE
                        saldo_inicial DECIMAL(15,2) NOT NULL,
                        estado BOOLEAN NOT NULL,
                        cliente_id BIGINT NOT NULL,
                        CONSTRAINT fk_cuenta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Tabla de movimientos
CREATE TABLE movimiento (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            fecha TIMESTAMP NOT NULL,
                            tipo_movimiento VARCHAR(20) NOT NULL, -- RETIRO, DEPOSITO
                            valor DECIMAL(15,2) NOT NULL,
                            saldo DECIMAL(15,2) NOT NULL,
                            cuenta_id BIGINT NOT NULL,
                            CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta(id)
);

INSERT INTO cliente (identificacion, nombre, genero, edad, direccion, telefono, contrasenia, estado)
VALUES
    ('123456789', 'Juan Pérez', 'M', 30, 'Calle 10 #20-30', '3001234567', '1234', TRUE),
    ('987654321', 'Daniela Gómez', 'F', 27, 'Carrera 15 #45-12', '3207920496', '1234', TRUE),
    ('555555555', 'Carlos Torres', 'M', 40, 'Av. Siempre Viva 123', '3109876543', '1234', TRUE);

-- Insertar cuentas
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES
    ('10001', 'AHORROS', 2000.00, TRUE, 1),
    ('10002', 'CORRIENTE', 5000.00, TRUE, 2),
    ('10003', 'AHORROS', 1500.00, TRUE, 3);

-- Insertar movimientos
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_id)
VALUES
    (NOW(), 'DEPOSITO', 500.00, 2500.00, 1),
    (NOW(), 'RETIRO', 300.00, 4700.00, 2),
    (NOW(), 'DEPOSITO', 200.00, 1700.00, 3);