-- Файл миграции: создание таблицы ссылок и таблицы чатов
create table link
(
    link_id         bigint generated always as identity,
    url             varchar(255)              not null,

    primary key (link_id),
    unique (url)
);

create table chat
(
    chat_id         bigint generated always as identity,

    primary key (chat_id)
);

-- Связывающая таблица для отслеживания связей между ссылками и чатами
create table link_chat_mapping
(
    chat_id         bigint                    not null,
    link_id         bigint                    not null,

    primary key (chat_id, link_id),
    foreign key (link_id) references link(link_id),
    foreign key (chat_id) references chat(chat_id)
);
