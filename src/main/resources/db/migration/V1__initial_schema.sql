create sequence trip_SEQ start with 1 increment by 1;

create table trip
(
    id               bigserial    not null primary key,
    user_id          int8         not null,
    name             varchar(50),
    start_dt         date,
    end_dt           date,
    expired          boolean,
    created_at       timestamptz  default now(),

    constraint user_id_name_uq unique (user_id, name)
);
