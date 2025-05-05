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

CREATE TABLE dbo.credit_card
(
    card_number     varchar(255) not null
        primary key,
    account_number  varchar(255),
    cvv             varchar(255),
    expiration_date varchar(255),
    bank_bank_id    bigint
        constraint fk_bank
            references dbo.bank
)
    GO