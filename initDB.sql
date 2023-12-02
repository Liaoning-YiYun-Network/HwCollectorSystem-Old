create table class
(
    name varchar(32) not null
        primary key
);

create table hw_info
(
    id       bigint auto_increment
        primary key,
    content  varchar(255) null,
    ddl_date varchar(255) null,
    title    varchar(255) null,
    class    varchar(255) null
);

create table stu_homework
(
    name          varchar(255) not null
        primary key,
    homework_file varchar(255) null
);

create table student
(
    name  varchar(16)  not null
        primary key,
    no    varchar(255) null,
    class varchar(255) null
);