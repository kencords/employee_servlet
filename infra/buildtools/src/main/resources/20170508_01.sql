CREATE USER kcordero WITH PASSWORD 'sushimi11';
DROP TABLE IF EXIST annot_employeedb;
CREATE DATABASE employeedb OWNER kcordero;

USE annot_employeedb;

CREATE TABLE address(
	addr_id SERIAL PRIMARY KEY,
	str_no INTEGER NOT NULL,
	street VARCHAR(25) NOT NULL,
	brgy VARCHAR(30) NOT NULL,
	city VARCHAR(30) NOT NULL,
	zipcode VARCHAR(10) NOT NULL
);

CREATE TABLE employees(
	emp_id SERIAL PRIMARY KEY,
	addr_id SERIAL REFERENCES address(addr_id),
	lastname VARCHAR(25) NOT NULL,
	firstname VARCHAR(55) NOT NULL,
	middlename VARCHAR(25) NOT NULL,
	suffix VARCHAR(25),
	title VARCHAR(25),
	
	birthdate DATE NOT NULL,
	gwa FLOAT NOT NULL,
	hiredate DATE NOT NULL,
	currentlyhired BOOL NOT NULL
);

CREATE TABLE contacts(
	contact_id SERIAL PRIMARY KEY,
	emp_id SERIAL REFERENCES employees(emp_id),
	contact_type VARCHAR(20),
	contact_value VARCHAR(30)
);

CREATE TABLE roles(
	role_id SERIAL PRIMARY KEY,
	role VARCHAR(25) NOT NULL
);

CREATE TABLE employee_role(
	emp_id INT REFERENCES employees(emp_id),
	role_id INT REFERENCES roles(role_id),
	PRIMARY KEY(emp_id,role_id)
);

INSERT INTO roles(role) VALUES ('CEO');
INSERT INTO roles(role) VALUES ('PRESIDENT');
INSERT INTO roles(role) VALUES ('SOFTWARE ENGINEER');
INSERT INTO roles(role) VALUES ('GRAPHICS DESIGNER');
INSERT INTO roles(role) VALUES ('WEB DEVELOPER');
INSERT INTO roles(role) VALUES ('WEB DESIGNER');
INSERT INTO roles(role) VALUES ('GAME DEVELOPER');
INSERT INTO roles(role) VALUES ('GUNDAM PILOT');