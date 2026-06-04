-- ====================================================================
-- MARS CORE ANALYTICAL PLATFORM - SEED DATA SCRIPT (data-full.sql)
-- Author: Josue Mullo (sekai882) / josuemullo10@hotmail.com
-- Purpose: Complete seed data for evaluation of cost-efficiency (Moneyball)
-- ====================================================================

-- 1. Limpiar datos existentes en cascada
TRUNCATE TABLE estadistica_detallada, estadistica, jugador, club, usuario RESTART IDENTITY CASCADE;

-- 2. Insertar Usuarios (Semilla de Seguridad)
-- La contraseña para ambos es "password"
INSERT INTO usuario (id, nombre, email, password, rol) VALUES 
(1, 'Administrador MARS', 'admin@mars.com', '$2a$10$30q4WCSV/KzuzEFoUulXpe1SNk8sNNOj4xs.HpIGBP5cvn/8bYirG', 'Administrador'),
(2, 'Director Académico', 'director@mars.com', '$2a$10$30q4WCSV/KzuzEFoUulXpe1SNk8sNNOj4xs.HpIGBP5cvn/8bYirG', 'Director Académico');

-- 3. Insertar Clubes (La Liga Santander)
INSERT INTO club (id, nombre, presupuesto) VALUES 
(1, 'Real Madrid CF', 900000000.0),
(2, 'FC Barcelona', 800000000.0),
(3, 'Getafe CF', 50000000.0),
(4, 'Rayo Vallecano', 45000000.0),
(5, 'Villarreal CF', 150000000.0),
(6, 'Atlético de Madrid', 350000000.0),
(7, 'Real Betis', 120000000.0),
(8, 'Celta de Vigo', 65000000.0),
(9, 'Valencia CF', 80000000.0),
(10, 'Real Sociedad', 140000000.0),
(11, 'RCD Espanyol', 45000000.0),
(12, 'Athletic Club', 110000000.0),
(13, 'Sevilla FC', 90000000.0),
(14, 'Deportivo Alavés', 40000000.0),
(15, 'Elche CF', 35000000.0),
(16, 'Levante UD', 38000000.0),
(17, 'CA Osasuna', 42000000.0),
(18, 'RCD Mallorca', 40000000.0),
(19, 'Girona FC', 70000000.0),
(20, 'Real Oviedo', 30000000.0);

-- 3. Insertar Jugadores (5 por Club)
-- Nota: Las posiciones válidas según el enum son: PIVOTE, EXTREMO, DELANTERO, DEFENSA.
INSERT INTO jugador (id, nombre, valor_mercado, nacionalidad, edad, posicion, club_id) VALUES
-- Real Madrid CF (Estrellas Caras: Costos muy altos)
(1, 'Vinícius Júnior', 150000000.0, 'Brasil', 25, 'EXTREMO', 1),
(2, 'Jude Bellingham', 180000000.0, 'Inglaterra', 22, 'PIVOTE', 1),
(3, 'Kylian Mbappé', 200000000.0, 'Francia', 27, 'DELANTERO', 1),
(4, 'Antonio Rüdiger', 120000000.0, 'Alemania', 33, 'DEFENSA', 1),
(5, 'Federico Valverde', 100000000.0, 'Uruguay', 27, 'PIVOTE', 1),

-- FC Barcelona (Estrellas Caras: Costos muy altos)
(6, 'Robert Lewandowski', 110000000.0, 'Polonia', 37, 'DELANTERO', 2),
(7, 'Lamine Yamal', 140000000.0, 'España', 18, 'EXTREMO', 2),
(8, 'Pedri González', 115000000.0, 'España', 23, 'PIVOTE', 2),
(9, 'Ronald Araujo', 105000000.0, 'Uruguay', 27, 'DEFENSA', 2),
(10, 'Gavi', 100000000.0, 'España', 21, 'PIVOTE', 2),

-- Getafe CF (Joyas Ocultas: Rendimiento Élite, Costo Fichaje Muy Bajo < 10M)
(11, 'Borja Mayoral', 8500000.0, 'España', 29, 'DELANTERO', 3),
(12, 'Luis Milla', 5200000.0, 'España', 25, 'PIVOTE', 3),
(13, 'Mason Greenwood', 9000000.0, 'Inglaterra', 24, 'EXTREMO', 3),
(14, 'Djené Dakonam', 4500000.0, 'Togo', 33, 'DEFENSA', 3),
(15, 'Mauro Arambarri', 6000000.0, 'Uruguay', 29, 'PIVOTE', 3),

-- Rayo Vallecano (Joyas Ocultas: Rendimiento Élite, Costo Fichaje Muy Bajo < 10M)
(16, 'Sergio Camello', 7800000.0, 'España', 24, 'DELANTERO', 4),
(17, 'Isi Palazón', 8200000.0, 'España', 30, 'EXTREMO', 4),
(18, 'Óscar Valentín', 4800000.0, 'España', 32, 'PIVOTE', 4),
(19, 'Florian Lejeune', 3500000.0, 'Francia', 33, 'DEFENSA', 4),
(20, 'Álvaro García', 6500000.0, 'España', 28, 'EXTREMO', 4),

-- Villarreal CF
(21, 'Gerard Moreno', 15000000.0, 'España', 32, 'DELANTERO', 5),
(22, 'Dani Parejo', 5000000.0, 'España', 35, 'PIVOTE', 5),
(23, 'Alex Baena', 40000000.0, 'España', 22, 'EXTREMO', 5),
(24, 'Yeremy Pino', 30000000.0, 'España', 21, 'EXTREMO', 5),
(25, 'Alexander Sorloth', 25000000.0, 'Noruega', 28, 'DELANTERO', 5),

-- Atlético de Madrid
(26, 'Antoine Griezmann', 25000000.0, 'Francia', 33, 'DELANTERO', 6),
(27, 'Koke Resurrección', 12000000.0, 'España', 32, 'PIVOTE', 6),
(28, 'Marcos Llorente', 30000000.0, 'España', 29, 'PIVOTE', 6),
(29, 'Jan Oblak', 30000000.0, 'Eslovenia', 31, 'PORTERO', 6),
(30, 'Álvaro Morata', 16000000.0, 'España', 31, 'DELANTERO', 6);

-- 4. Insertar Estadísticas Generales (estadistica)
INSERT INTO estadistica (id, goles, pases_exitosos, minutos, rating, jugador_id) VALUES
-- Real Madrid CF (Estadísticas Altas, pero Valor desmesurado)
(1, 18, 450, 2800, 8.1, 1),
(2, 19, 850, 3100, 8.2, 2),
(3, 27, 420, 2900, 8.4, 3),
(4, 2, 600, 3200, 7.9, 4),
(5, 7, 900, 3000, 8.0, 5),

-- FC Barcelona (Estadísticas Altas, pero Valor desmesurado)
(6, 24, 380, 2800, 8.1, 6),
(7, 12, 580, 2700, 8.3, 7),
(8, 5, 1100, 2500, 8.2, 8),
(9, 3, 550, 2600, 7.9, 9),
(10, 4, 880, 2400, 8.0, 10),

-- Getafe CF (Rendimiento Élite, Joyas Ocultas, Rating > 8.0, Eficiencia Extrema)
(11, 25, 320, 2600, 8.4, 11),
(12, 6, 1150, 2800, 8.2, 12),
(13, 16, 490, 2500, 8.3, 13),
(14, 1, 450, 3000, 8.1, 14),
(15, 5, 780, 2300, 8.1, 15),

-- Rayo Vallecano (Rendimiento Élite, Joyas Ocultas, Rating > 8.0, Eficiencia Extrema)
(16, 22, 340, 2700, 8.3, 16),
(17, 15, 610, 2900, 8.4, 17),
(18, 3, 980, 2800, 8.2, 18),
(19, 4, 620, 3100, 8.1, 19),
(20, 13, 430, 2500, 8.2, 20),

-- Villarreal CF
(21, 20, 400, 2600, 8.3, 21),
(22, 4, 1200, 3000, 8.1, 22),
(23, 10, 800, 2800, 8.2, 23),
(24, 8, 600, 2500, 8.0, 24),
(25, 23, 300, 2700, 8.4, 25),

-- Atlético de Madrid
(26, 18, 800, 3100, 8.5, 26),
(27, 2, 1300, 3200, 8.2, 27),
(28, 6, 900, 2900, 8.3, 28),
(29, 0, 400, 3400, 8.0, 29),
(30, 21, 350, 2800, 8.2, 30);

-- 5. Insertar Estadísticas Granulares (estadistica_detallada)
INSERT INTO estadistica_detallada (id, velocidad_punta, pases_ultimo_tercio, duelos_defensivos, expected_goals, atajadas, jugador_id) VALUES
-- Real Madrid CF
(1, 36.5, 120, 25, 16.5, 0, 1),
(2, 33.2, 180, 80, 17.2, 0, 2),
(3, 37.9, 105, 15, 25.8, 0, 3),
(4, 35.1, 45, 180, 1.5, 0, 4),
(5, 36.2, 160, 95, 6.8, 0, 5),

-- FC Barcelona
(6, 32.5, 70, 20, 22.4, 0, 6),
(7, 35.8, 210, 40, 11.5, 0, 7),
(8, 31.8, 250, 75, 4.8, 0, 8),
(9, 35.4, 35, 165, 2.1, 0, 9),
(10, 32.7, 140, 110, 3.5, 0, 10),

-- Getafe CF (Estadísticas extraordinarias de xG, duelos y pases)
(11, 33.1, 65, 18, 24.2, 0, 11),
(12, 31.5, 280, 85, 5.5, 0, 12),
(13, 36.4, 155, 30, 15.2, 0, 13),
(14, 33.8, 25, 210, 0.8, 0, 14),
(15, 32.9, 110, 140, 4.2, 0, 15),

-- Rayo Vallecano (Estadísticas extraordinarias de xG, duelos y pases)
(16, 34.0, 80, 22, 21.9, 0, 16),
(17, 34.5, 195, 48, 14.8, 0, 17),
(18, 31.2, 130, 195, 2.8, 0, 18),
(19, 32.1, 55, 185, 3.5, 0, 19),
(20, 36.8, 125, 35, 12.1, 0, 20),

-- Villarreal CF
(21, 33.5, 120, 40, 20.5, 0, 21),
(22, 30.5, 250, 60, 3.5, 0, 22),
(23, 34.8, 180, 50, 10.2, 0, 23),
(24, 35.1, 140, 45, 8.5, 0, 24),
(25, 34.2, 80, 30, 22.8, 0, 25),

-- Atlético de Madrid
(26, 33.8, 220, 45, 19.5, 0, 26),
(27, 31.5, 280, 80, 2.8, 0, 27),
(28, 35.5, 190, 75, 5.5, 0, 28),
(29, 0.0, 35, 0, 0.0, 88, 29),
(30, 33.2, 90, 35, 21.0, 0, 30);

-- 6. Ajustar secuencias de ID de PostgreSQL para prevenir colisiones en ejecuciones futuras
SELECT setval('club_id_seq', (SELECT MAX(id) FROM club));
SELECT setval('jugador_id_seq', (SELECT MAX(id) FROM jugador));
SELECT setval('estadistica_id_seq', (SELECT MAX(id) FROM estadistica));
SELECT setval('estadistica_detallada_id_seq', (SELECT MAX(id) FROM estadistica_detallada));
