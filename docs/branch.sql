CREATE DATABASE my_database;
GO

USE my_database
    GO

CREATE TABLE dbo.balance_change
(
    id        uniqueidentifier not null
        primary key,
    account   varchar(255),
    amount    float            not null,
    branch_id int              not null,
    date      bigint           not null,
    status    smallint
        check ([status] >= 0 AND [status] <= 1)
    )
    GO

CREATE TABLE dbo.client
(
    id               uniqueidentifier not null
        primary key,
    account_number   varchar(255),
    address          varchar(255),
    balance          float            not null,
    balance_reserved float            not null,
    branch           int              not null,
    city             varchar(255),
    user_id          int,
    first_name       varchar(255),
    last_name        varchar(255),
    phone            varchar(255),
    status           smallint
        check ([status] >= 0 AND [status] <= 1)
    )
    GO

create unique index ind_account_number
    on dbo.client (account_number)
    where [account_number] IS NOT NULL
GO

CREATE TABLE dbo.transfer
(
    id             uniqueidentifier not null
        primary key,
    amount         float            not null,
    date           bigint           not null,
    from_account   varchar(255),
    from_branch_id int              not null,
    message        varchar(255),
    status         smallint
        check ([status] >= 0 AND [status] <= 2),
    to_account     varchar(255),
    to_branch_id   int              not null
)
    GO
