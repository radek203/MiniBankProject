CREATE DATABASE my_database;
GO

USE my_database
    go

CREATE TABLE dbo.users
(
    id         int identity
        primary key,
    avatar     varchar(255),
    created_at datetime2(6),
    email      varchar(255),
    password   varchar(255),
    role       smallint
        check ([role] >= 0 AND [role] <= 1),
    username   varchar(255)
)
    GO
