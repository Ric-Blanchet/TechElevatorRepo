DROP TABLE IF EXISTS tenmo_user;
DROP TABLE IF EXISTS user_transaction;
DROP SEQUENCE IF EXISTS seq_user_id;

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
	password_hash varchar(200) NOT NULL,
	balance int NOT NULL,
	role varchar(20),
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username),
	CHECK (balance > 0)
);

CREATE TABLE user_transaction (
	transaction_id serial NOT NULL,
	user_from int NOT NULL,
	user_to int NOT NULL,
	amount int NOT NULL,
	transfer_date date NOT NULL,
	status varchar(20) NOT NULL,
	CONSTRAINT PK_transaction_id PRIMARY KEY (transaction_id),
	CONSTRAINT FK_user_from FOREIGN KEY (user_from) REFERENCES tenmo_user (user_id),
	CONSTRAINT FK_user_to FOREIGN KEY (user_to) REFERENCES tenmo_user (user_id),
	CHECK (amount > 0)
);
