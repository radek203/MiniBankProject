CREATE DATABASE my_database;
GO

USE my_database
    GO

CREATE TABLE dbo.bank
(
    bank_id bigint identity
        primary key,
    name    varchar(255),
    url     varchar(255)
)
    GO

