create table chat_service.users
(
    id           varchar(255) not null
        constraint users_pkey
            primary key,
    create_date        timestamp    not null,
    username          varchar(255),
    first_name         varchar(255),
    last_modified_date timestamp    not null,
    last_name          varchar(255),
    image_url          varchar(255),
    version            integer      not null
);

create table chat_service.chats
(
    id                                varchar(255) not null
        constraint chats_pkey
            primary key,
    create_date                       timestamp    not null,
    last_modified_date                timestamp    not null,
    version                           integer      not null,
    first_user_last_seen_date         timestamp,
    first_user_unseen_messages_count  integer      not null,
    last_message_date                 timestamp,
    second_user_last_seen_date        timestamp,
    second_user_unseen_messages_count integer      not null,
    subject                           varchar(255),
    first_user_id                     varchar(255) not null
        constraint fkg36wtxlub2qi4i9ys9hahrrmr
            references chat_service.users,
    last_message_id                   varchar(255),
    second_user_id                    varchar(255) not null
        constraint fkc6dm8xgp7grllknpr9evn04mk
            references chat_service.users
);

create table chat_service.messages
(
    id                 varchar(255) not null
        constraint messages_pkey
            primary key,
    create_date        timestamp    not null,
    last_modified_date timestamp    not null,
    version            integer      not null,
    body               varchar(255),
    chat_id            varchar(255) not null
        constraint fk64w44ngcpqp99ptcb9werdfmb
            references chat_service.chats,
    receiver_id        varchar(255)
        constraint fkksa9gywt5lft6bveoxae62eei
            references chat_service.users,
    sender_id          varchar(255)
        constraint fk68jbduqps2xpp4oh4kdas0kle
            references chat_service.users
);

alter table chat_service.chats
    add constraint fk8y0mndjgabg4cq2x9o0dj72p
        foreign key (last_message_id) references chat_service.messages;

create table chat_service.mqtt_connection_details
(
    id         bigserial    not null
        constraint mqtt_connection_details_pkey
            primary key,
    password   varchar(255) not null,
    super_user boolean      not null,
    username   varchar(255) not null
        constraint fk3cijetsekeb7boavj8trkvwao
            references chat_service.users
);

insert into chat_service.users (id, username, create_date, first_name, last_modified_date, last_name, version)
values ('mqtt_admin', 'mqtt_admin', now(), 'mqtt_admin', now(), 'mqtt_admin', 0);

insert into chat_service.mqtt_connection_details(username, password, super_user) values ('mqtt_admin', '$2a$10$Wyvqf9P8eEd0HGdNA/Dk3O.elGkvs2yAV/GfpjC/PwG0d4/EdlFpO', true);
