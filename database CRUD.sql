CREATE DATABASE CRUD;

CREATE TABLE cargo(
	id numeric(15,0),
	descricao varchar(50),
	PRIMARY KEY(id)
);

CREATE TABLE funcionario(
	cpf varchar(11),
	nome varchar(50),
	cargo numeric(15,0),
	salario numeric(15,2),
	PRIMARY KEY(cpf),
	FOREIGN KEY(cargo) REFERENCES cargo(id)
);

INSERT INTO cargo VALUES (1, 'Programador Back-End'),(2, 'Programador Front-End'),  (3, 'Designer') , (4, 'Especialista em Redes'), (5, 'DBA');


INSERT INTO Funcionario VALUES ('12345678901', 'JV', 1, 5000), ('09876543210', 'PH', 1, 5000), ('15975312310', 'Vitor', 1, 5000), ('75315975310', 'Golo', 1, 5000), ('65498732102', 'Ana', 1, 5000);

