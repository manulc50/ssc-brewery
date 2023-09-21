create table persistent_logins (series varchar(64) primary key,
								username varchar(64) not null,
                                token varchar(64) not null,
                                last_used timestamp not null);