CREATE SCHEMA IF NOT EXISTS kn AUTHORIZATION asrb;

--file table
CREATE SEQUENCE IF NOT EXISTS kn.file_seq;
CREATE  TABLE IF NOT EXISTS kn.file (
    id integer NOT NULL DEFAULT nextval('kn.file_seq') PRIMARY KEY,
    name varchar(500) not null,
    mime varchar(150) not null,
    data bytea not null,
    created timestamp default current_timestamp
);
ALTER SEQUENCE kn.file_seq
    OWNED BY kn.file.id;

--document table
CREATE SEQUENCE IF NOT EXISTS kn.document_seq;
CREATE  TABLE IF NOT EXISTS kn.document (
    id integer NOT NULL DEFAULT nextval('kn.document_seq')  PRIMARY KEY,
    name varchar(1000) not null,
    file_id integer,
    file_name varchar(500),
    file_type varchar(150),
    active boolean not null
);
ALTER SEQUENCE kn.document_seq
    OWNED BY kn.document.id;

--template table
CREATE SEQUENCE IF NOT EXISTS kn.template_seq;
CREATE TABLE IF NOT EXISTS kn.template (
    id integer NOT NULL DEFAULT nextval('kn.template_seq') PRIMARY KEY,
    main_id integer NOT NULL,
    status varchar(20) not null,
    level varchar (20) not null,
    post varchar (20) not null,
    post_full varchar (300) not null,
    from_ref integer,
    from_date date not null,
    to_date date not null,
    created_user_id integer not null,
    created_user_name varchar (300) not null,
    created_date date default current_date,
    agreed_user_id integer,
    agreed_user_name varchar (300),
    agreed_date date,
    approved_user_id integer,
    approved_user_name varchar (300),
    approved_date date
);
ALTER SEQUENCE kn.template_seq
    OWNED BY kn.template.id;

--template norm table
CREATE TABLE IF NOT EXISTS kn.template_norm (
    template_id integer NOT NULL REFERENCES kn.template (id),
    index integer  not null,
    name varchar (1000) NOT NULL,
    period varchar (20) not null
);

--blank table
CREATE SEQUENCE IF NOT EXISTS kn.blank_seq;

CREATE TABLE IF NOT EXISTS kn.blank (
   id integer NOT NULL DEFAULT nextval('kn.blank_seq') PRIMARY KEY,
   template_id integer references kn.template(id),
   post varchar (20) not null,
   main_id integer NOT NULL,
   reg_id integer,
   pred_id integer NOT NULL,
   dor_kod integer,
   level varchar (20) not null,
   pred_name varchar(300) not null,
   year integer not null,
   open integer,
   doc_name varchar(1000) not null,
   created_user_id integer not null,
   created_user_name varchar (300) not null,
   created_date date default current_date
);
ALTER SEQUENCE kn.blank_seq
    OWNED BY kn.blank.id;
--blank view
CREATE TABLE IF NOT EXISTS kn.blank_view (
    blank_id integer NOT NULL REFERENCES kn.blank (id),
    user_id integer not null,
    user_name varchar (300) not null,
    view_date date not null,
    comment varchar(500) not null
);
--blank norm
CREATE SEQUENCE IF NOT EXISTS kn.blank_norm_seq;
CREATE TABLE IF NOT EXISTS kn.blank_norm (
       id integer NOT NULL DEFAULT nextval('kn.blank_norm_seq') PRIMARY KEY,
       blank_id integer NOT NULL REFERENCES kn.blank (id),
       name varchar(1000) not null,
       period varchar(20) not null,
       completion integer not null,
       plan integer,
       doc_id integer,
       dsc varchar(5) not null
);
ALTER SEQUENCE kn.blank_norm_seq
    OWNED BY kn.blank_norm.id;
--blank file
CREATE SEQUENCE IF NOT EXISTS kn.blank_file_seq;
CREATE TABLE IF NOT EXISTS kn.blank_file (
     id integer NOT NULL DEFAULT nextval('kn.blank_file_seq') PRIMARY KEY,
     norm_id integer NOT NULL REFERENCES kn.blank_norm (id),
     month integer not null,
     file_id integer NOT NULL,
     file_name varchar(500) not null,
     file_type varchar(150) not null
);
ALTER SEQUENCE kn.blank_file_seq
    OWNED BY kn.blank_file.id;
