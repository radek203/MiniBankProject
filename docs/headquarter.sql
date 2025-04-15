CREATE DATABASE my_database;
GO

USE my_database
    GO

CREATE TABLE dbo.client
(
    id             uniqueidentifier not null
        primary key,
    account_number varchar(255),
    balance        float            not null,
    branch         int              not null,
    user_id        int              not null
)
    GO

create unique index ind_account_number
    on dbo.client (account_number)
    where [account_number] IS NOT NULL
GO

