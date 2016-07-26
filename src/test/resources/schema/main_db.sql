--PUBLIC schema holds only application metadata - the tenant table
SET SCHEMA PUBLIC;
create table Tenant (name varchar(255) not null, schemaName varchar(255) not null, password varchar(255) not null, primary key (name));
insert into Tenant (name, schemaName, password) values ('Alice', 'alice', 'lorem');
insert into Tenant (name, schemaName, password) values ('Bob', 'bob', 'ipsum');
