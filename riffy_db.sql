-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 10-05-2026 a las 00:14:53
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE =  "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone =  "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `riffy_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `conversacion`
--

CREATE TABLE `conversacion` (
  `id_conversacion` bigint(20) NOT NULL,
  `id_producto` bigint(20) DEFAULT NULL,
  `id_comprador` bigint(20) DEFAULT NULL,
  `id_vendedor` bigint(20) DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  `conversacion_activa` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `conversacion`
--

INSERT INTO `conversacion` (`id_conversacion`, `id_producto`, `id_comprador`, `id_vendedor`, `fecha_creacion`, `conversacion_activa`) VALUES
(1, 1, 3, 2, '2026-05-10', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mensaje`
--

CREATE TABLE `mensaje` (
  `id_mensaje` bigint(20) NOT NULL,
  `id_conversacion` bigint(20) DEFAULT NULL,
  `mensaje` text NOT NULL,
  `fecha_envio` datetime NOT NULL,
  `leido` tinyint(1) DEFAULT 0,
  `id_remitente` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `mensaje`
--

INSERT INTO `mensaje` (`id_mensaje`, `id_conversacion`, `mensaje`, `fecha_envio`, `leido`, `id_remitente`) VALUES
(1, 1, 'Hola! Me gustaría hablar sobre tu vinilo? Puedes hablarme de cómo está y sus características?', '2026-05-10 00:00:31', 0, 3),
(2, 1, 'Heya!', '2026-05-10 00:01:10', 0, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id_producto` bigint(20) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `artista` varchar(255) DEFAULT NULL,
  `formato` varchar(255) DEFAULT NULL,
  `descripcion` varchar(1000) DEFAULT NULL,
  `precio` decimal(38,2) NOT NULL,
  `propietario` bigint(20) NOT NULL,
  `fecha_edicion` date DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `categoria` varchar(255) DEFAULT NULL,
  `imagenes` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id_producto`, `titulo`, `artista`, `formato`, `descripcion`, `precio`, `propietario`, `fecha_edicion`, `estado`, `categoria`, `imagenes`) VALUES
(1, 'A head full of dreams', 'Coldplay', 'Muy Bueno', 'Vinilo edición rosa.', 35.00, 2, '2026-05-09', 'Disponible', 'Vinilo', 'usu2_vinilo1.jpg,usu2_vinilo2.jpg'),
(2, 'Doo-wops & hooligans', 'Bruno Mars', 'Nuevo', 'Cd completamente nuevo. Poco uso. Canciones más famosas.', 25.00, 2, '2026-05-09', 'Disponible', 'CD', 'usu2_doowoopsandhooligans.jpg'),
(3, 'Born this way', 'Lady Gaga', 'Nuevo', 'Prácticamente nuevo, regalo de mi hija pero prefiere la versión limitada.', 20.00, 2, '2026-05-09', 'Reservado', 'CD', 'usu2_bornthiswayladygaga2.jpg,usu2_bornthiswayladygaga1.jpg'),
(4, 'The Life of a Showgirl', 'Taylor Swift', 'Muy Bueno', 'La vida de una showcera, primera versión. ', 60.00, 3, '2026-05-09', 'Disponible', 'Vinilo', 'usu3_🧡Life of a showgirl Vinyl 🧡.jpg,usu3_the life of a showgirl vinyl.jpg'),
(5, 'Teenage Dream', 'Katy Perry', 'Usado', 'El mejor album de Katy Perry.', 30.00, 3, '2026-05-09', 'Disponible', 'CD', 'usu3_cd teenage dream katy perry 🍭.jpg'),
(6, 'Eternal sunshine', 'Ariana Grande', 'Nuevo', 'Vinilo rojo, regalo de cumpleaños. Se equivocaron.', 100.00, 3, '2026-05-09', 'Disponible', 'Vinilo', 'usu3_Eternal Sunshine.jpg,usu3_🙈❤️ @moewkii pics ♡.jpg'),
(7, 'Best selection Paco de Lucía', 'Paco de Lucia', 'Muy Bueno', 'EDICIÓN JAPONESA', 160.00, 4, '2026-05-10', 'Disponible', 'CD', 'usu4_heftyclassic auf eBay.jpg'),
(8, 'Man\'s Best Friend', 'Sabrina Carpenter', 'Nuevo', 'Vinilo rosa, versión de Target. Edición limitada', 97.00, 4, '2026-05-10', 'Disponible', 'Vinilo', 'usu4_Sabrina Carpenter Man’s Best Friend.jpg'),
(9, 'Michael jackson', 'Michael Jackson', 'Usado', 'HEE HEE, ya llegó el cd de MJ a Riffy. Cómpralo y te vendrá un poster de él. AUUU!', 50.00, 4, '2026-05-10', 'Disponible', 'CD', 'usu4_Michael Jackson.jpg');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `fecha_registro` date NOT NULL,
  `foto_perfil` varchar(255) DEFAULT NULL,
  `rol` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `usuario`, `email`, `contrasena`, `fecha_registro`, `foto_perfil`, `rol`) VALUES
(1, 'Administrador', 'admin', 'admin@riffy.com', '$2a$10$8lfUMe1mhV7sG9.7KWriOeBrX5TsQb0quNn1QuszFGZf1ZEay81EC', '2026-05-09', NULL, 'USER'),
(2, 'Javier Mancera', 'ihavenomouth', 'javier.mancera@riffy.com', '$2a$10$NGL5rtXq29VYs6H26eqfnOjSfuLlcSZi7ZPs0XDJGMlAwh9GoiB6O', '2026-05-09', 'usu2_62035356.png', 'USER'),
(3, 'Juan de Dios Álvarez', 'juande', 'juande.alvarez@riffy.com', '$2a$10$qjzMoICVI5jmxng0FbTr9uV.iICXWmNSBeReW0vFx4SPRkOPTehBm', '2026-05-09', 'usu3_katsuki bakugou.jpg', 'USER'),
(4, 'Antonio Ladesa', 'antolade', 'antonio.ladesa@riffy.com', '$2a$10$u0DONjqwRCOqOv3d7egBmOtUPjadn6.sdslNLLQoG5xSuUWSP/ZDy', '2026-05-09', 'usu4_tortilladepapas.jpg', 'USER');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `conversacion`
--
ALTER TABLE `conversacion`
  ADD PRIMARY KEY (`id_conversacion`),
  ADD KEY `conversacion_ibfk_1` (`id_producto`),
  ADD KEY `conversacion_ibfk_2` (`id_comprador`),
  ADD KEY `conversacion_ibfk_3` (`id_vendedor`);

--
-- Indices de la tabla `mensaje`
--
ALTER TABLE `mensaje`
  ADD PRIMARY KEY (`id_mensaje`),
  ADD KEY `mensaje_ibfk_1` (`id_conversacion`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`),
  ADD KEY `producto_ibfk_1` (`propietario`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `usuario` (`usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `conversacion`
--
ALTER TABLE `conversacion`
  MODIFY `id_conversacion` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `mensaje`
--
ALTER TABLE `mensaje`
  MODIFY `id_mensaje` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `conversacion`
--
ALTER TABLE `conversacion`
  ADD CONSTRAINT `conversacion_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  ADD CONSTRAINT `conversacion_ibfk_2` FOREIGN KEY (`id_comprador`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `conversacion_ibfk_3` FOREIGN KEY (`id_vendedor`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `mensaje`
--
ALTER TABLE `mensaje`
  ADD CONSTRAINT `mensaje_ibfk_1` FOREIGN KEY (`id_conversacion`) REFERENCES `conversacion` (`id_conversacion`);

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`propietario`) REFERENCES `usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
