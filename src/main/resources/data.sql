-- Insertar Clubes
INSERT INTO club (nombre, presupuesto) VALUES ('Real Madrid', 150000000.0);
INSERT INTO club (nombre, presupuesto) VALUES ('Getafe CF', 20000000.0);

-- Insertar Jugadores (Real Madrid)
INSERT INTO jugador (nombre, valor_mercado, nacionalidad, posicion, club_id) 
VALUES ('Vinicius Jr', 150000000.0, 'Brasil', 'EXTREMO', (SELECT id FROM club WHERE nombre = 'Real Madrid'));

INSERT INTO jugador (nombre, valor_mercado, nacionalidad, posicion, club_id) 
VALUES ('Jude Bellingham', 150000000.0, 'Inglaterra', 'PIVOTE', (SELECT id FROM club WHERE nombre = 'Real Madrid'));

INSERT INTO jugador (nombre, valor_mercado, nacionalidad, posicion, club_id) 
VALUES ('Antonio Rüdiger', 30000000.0, 'Alemania', 'DEFENSA', (SELECT id FROM club WHERE nombre = 'Real Madrid'));

-- Insertar Jugadores (Getafe CF - Moneyball)
INSERT INTO jugador (nombre, valor_mercado, nacionalidad, posicion, club_id) 
VALUES ('Nemanja Maksimovic', 5000000.0, 'Serbia', 'PIVOTE', (SELECT id FROM club WHERE nombre = 'Getafe CF'));

INSERT INTO jugador (nombre, valor_mercado, nacionalidad, posicion, club_id) 
VALUES ('Borja Mayoral', 10000000.0, 'España', 'DELANTERO', (SELECT id FROM club WHERE nombre = 'Getafe CF'));

INSERT INTO jugador (nombre, valor_mercado, nacionalidad, posicion, club_id) 
VALUES ('Djene Dakonam', 4000000.0, 'Togo', 'DEFENSA', (SELECT id FROM club WHERE nombre = 'Getafe CF'));

-- Insertar Estadísticas (Real Madrid)
INSERT INTO estadistica (goles, pases_exitosos, minutos, rating, jugador_id) 
VALUES (15, 850, 2500, 8.5, (SELECT id FROM jugador WHERE nombre = 'Vinicius Jr'));

INSERT INTO estadistica (goles, pases_exitosos, minutos, rating, jugador_id) 
VALUES (20, 1200, 2800, 8.8, (SELECT id FROM jugador WHERE nombre = 'Jude Bellingham'));

INSERT INTO estadistica (goles, pases_exitosos, minutos, rating, jugador_id) 
VALUES (2, 1500, 3000, 7.5, (SELECT id FROM jugador WHERE nombre = 'Antonio Rüdiger'));

-- Insertar Estadísticas (Getafe CF - Moneyball)
-- Maksimovic: Joya oculta con bajo valor pero rating/pases altísimos
INSERT INTO estadistica (goles, pases_exitosos, minutos, rating, jugador_id) 
VALUES (1, 2100, 3100, 8.2, (SELECT id FROM jugador WHERE nombre = 'Nemanja Maksimovic'));

-- Mayoral
INSERT INTO estadistica (goles, pases_exitosos, minutos, rating, jugador_id) 
VALUES (15, 400, 2600, 7.8, (SELECT id FROM jugador WHERE nombre = 'Borja Mayoral'));

-- Dakonam: Joya oculta con buen rating
INSERT INTO estadistica (goles, pases_exitosos, minutos, rating, jugador_id) 
VALUES (0, 1800, 3200, 8.1, (SELECT id FROM jugador WHERE nombre = 'Djene Dakonam'));
