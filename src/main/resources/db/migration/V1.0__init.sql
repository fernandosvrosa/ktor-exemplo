create table books(
book_id varchar (60) not null,
book_title varchar (100) not null,
version int not null,
primary key  (book_id, version)
)