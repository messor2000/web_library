insert into web_library.library_user (id, first_name, last_name, email, password) values ('1','admin', 'admin', 'admin@gmail.com', 'admin');
insert into web_library.role (idrole, name) values ('1','ROLE_ADMIN');
insert into web_library.user_roles (user_id, role_id) values ('1', '1');