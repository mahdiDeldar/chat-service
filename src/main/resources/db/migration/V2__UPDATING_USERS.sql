alter table chat_service.users add column full_name varchar(255);

update chat_service.users u
set full_name = first_name
where first_name is not null;

update chat_service.users u
set full_name = last_name
where last_name is not null;

update chat_service.users u
set full_name = concat(first_name, ' ', last_name)
where first_name is not null and last_name is not null;

alter table chat_service.users drop column first_name;
alter table chat_service.users drop column last_name;
