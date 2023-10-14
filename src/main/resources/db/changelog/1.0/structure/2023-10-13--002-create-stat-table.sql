create table if not exists linestat.stat (
    id serial PRIMARY KEY,
    place_id integer ,
    person_type varchar(15),
    line_time integer,
    created_at timestamp
);

CREATE INDEX ON linestat.stat(place_id, person_type, created_at);