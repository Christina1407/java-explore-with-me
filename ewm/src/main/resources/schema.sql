create table if not exists users
(
    user_id bigint generated by default as identity primary key,
    email     varchar(254) not null CHECK (char_length(email) >= 6),
    name     varchar(250) not null CHECK (char_length(name) >= 2),
    unique (email),
    unique (name)
);

create table if not exists categories
(
    category_id bigint generated by default as identity primary key,
    name     varchar(50) not null CHECK (char_length(name) >= 1),
    unique (name)
);

create table if not exists places
(
    place_id bigint generated by default as identity primary key,
    latitude float not null,
    longitude float not null,
    radius int not null,
    name varchar(100) not null CHECK (char_length(name) >= 1),
    type_id int not null,
    season varchar (20),
    feature varchar (100),
     unique (name)
);
create table if not exists place_type
(
    type_id bigint generated by default as identity primary key,
    name varchar(100) not null CHECK (char_length(name) >= 1),
    unique (name)
);

create table if not exists events
(
    event_id bigint generated by default as identity primary key,
    annotation     varchar(2000) not null CHECK (char_length(annotation) >= 20),
    category_id bigint not null,
    description     varchar(7000) not null CHECK (char_length(description) >= 20),
    event_date timestamp not null,
    created_date timestamp not null,
    published_date timestamp,
    initiator_id bigint not null,
    paid boolean default false,
    participant_limit int default 0,
    request_moderation boolean default true,
    title varchar(120) not null CHECK (char_length(title) >= 3),
    state varchar(20) not null,
    lat float not null,
    lon float not null,
    constraint fk_events_category_id
               foreign key (category_id)
               references categories (category_id),
    constraint fk_events_initiator_id
               foreign key (initiator_id)
               references users (user_id) ON DELETE CASCADE
);

create table if not exists events_places
(
    event_place_id bigint generated by default as identity primary key,
    event_id bigint not null,
    place_id bigint not null,
    constraint fk_events_places_event_id
               foreign key(event_id)
               references events(event_id) ON DELETE CASCADE,
    constraint fk_events_places_place_id
               foreign key(place_id)
               references places(place_id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX if not exists unique_index_events_places ON events_places (event_id, place_id);

create table if not exists requests
(
    request_id bigint generated by default as identity primary key,
    created_date timestamp not null,
    event_id bigint not null,
    requester_id bigint not null,
    status varchar(20) not null,
    constraint fk_requests_event_id
               foreign key (event_id)
               references events (event_id)ON DELETE CASCADE,
    constraint fk_requests_requester_id
               foreign key (requester_id)
               references users (user_id) ON DELETE CASCADE

);
CREATE UNIQUE INDEX if not exists unique_index_requests ON requests (event_id, requester_id);

create table if not exists compilations
(
    compilation_id bigint generated by default as identity primary key,
    pinned boolean default false,
    title varchar(50) not null CHECK (char_length(title) >= 1)
);
create table if not exists events_compilations
(
    event_compilation_id bigint generated by default as identity primary key,
    event_id bigint not null,
    compilation_id bigint not null,
    constraint fk_events_compilations_event_id
               foreign key(event_id)
               references events(event_id) ON DELETE CASCADE,
    constraint fk_events_compilations_compilation_id
               foreign key(compilation_id)
               references compilations(compilation_id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX if not exists unique_index_events_compilations ON events_compilations (event_id, compilation_id);