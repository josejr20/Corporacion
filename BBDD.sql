CREATE DATABASE VLAG;
USE VLAG;

-- Tabla Rol
CREATE TABLE Rol (
    idRol INT AUTO_INCREMENT PRIMARY KEY,
    nombreRol VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla Empleado
CREATE TABLE Empleado (
    idEmpleado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL
);

-- Tabla Usuario
CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    idRol INT NOT NULL,
    idEmpleado INT NOT NULL,
    FOREIGN KEY (idRol) REFERENCES Rol(idRol),
    FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
);

-- Tabla Cliente
CREATE TABLE Cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    razonSocial VARCHAR(150) NOT NULL,
    ruc VARCHAR(11) UNIQUE NOT NULL,
    direccion VARCHAR(150),
    telefono VARCHAR(20),
    email VARCHAR(100)
);

-- Tabla Producto
CREATE TABLE Producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0)
);

-- Tabla Pedido
CREATE TABLE Pedido (
    idPedido INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    idEmpleado INT NOT NULL,
    fechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
    estadoPedido ENUM('pendiente','validado','embalado','entregado') DEFAULT 'pendiente',
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
);

-- Tabla DetallePedido
CREATE TABLE DetallePedido (
    idDetalle INT AUTO_INCREMENT PRIMARY KEY,
    idPedido INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

-- Tabla Factura
CREATE TABLE Factura (
    idFactura INT AUTO_INCREMENT PRIMARY KEY,
    idPedido INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    fechaEmision DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido)
);

-- Tabla Pago
CREATE TABLE Pago (
    idPago INT AUTO_INCREMENT PRIMARY KEY,
    idFactura INT NOT NULL,
    metodoPago ENUM('efectivo','transferencia','tarjeta'),
    monto DECIMAL(10,2),
    fechaPago DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idFactura) REFERENCES Factura(idFactura)
);

-- Tabla Entrega
CREATE TABLE Entrega (
    idEntrega INT AUTO_INCREMENT PRIMARY KEY,
    idPedido INT NOT NULL,
    fechaEntrega DATE,
    estadoEntrega ENUM('programada','en camino','entregado') DEFAULT 'programada',
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido)
);

-- Tabla Reporte
CREATE TABLE Reporte (
    idReporte INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50),
    fechaGeneracion DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Trigger para actualizar stock al insertar detalle
DELIMITER //
CREATE TRIGGER trg_reducir_stock AFTER INSERT ON DetallePedido
FOR EACH ROW
BEGIN
    UPDATE Producto SET stock = stock - NEW.cantidad WHERE idProducto = NEW.idProducto;
END;
//

-- Trigger para restaurar stock si se elimina detalle
DELIMITER //
CREATE TRIGGER trg_restaurar_stock
AFTER DELETE ON DetallePedido
FOR EACH ROW
BEGIN
    UPDATE Producto
    SET stock = stock + OLD.cantidad
    WHERE idProducto = OLD.idProducto;
END//
DELIMITER ;