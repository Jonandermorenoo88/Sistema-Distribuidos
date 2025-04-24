CREATE TABLE IF NOT EXISTS usuarios (
    id IDENTITY PRIMARY KEY,
    nombre VARCHAR(255),
    usuario VARCHAR(100),
    contrasena VARCHAR(100)
);
