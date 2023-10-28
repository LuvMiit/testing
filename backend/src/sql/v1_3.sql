--sync_reports_log table
CREATE SEQUENCE IF NOT EXISTS kn.sync_reports_log_seq;

CREATE TABLE IF NOT EXISTS kn.sync_reports_log
(
    id           integer     NOT NULL DEFAULT nextval('kn.sync_reports_log_seq') PRIMARY KEY,
    start_date   date        not null,
    end_date     date        not null,
    initiator    varchar(50) not null,
    process_date date        not null,
    error        varchar(1000)
);
ALTER SEQUENCE kn.sync_reports_log_seq OWNED BY kn.sync_reports_log.id;

--sync_report_log table
CREATE SEQUENCE IF NOT EXISTS kn.sync_report_log_seq;
CREATE TABLE IF NOT EXISTS kn.sync_report_log
(
    id                  integer NOT NULL DEFAULT nextval('kn.sync_report_log_seq') PRIMARY KEY,
    sync_reports_log_id integer NOT NULL REFERENCES kn.sync_reports_log (id),
    report_document     text,
    error               varchar(1000),
    dnch_id             bigint,
    blank_norms         text,
    log_list            text,
    files               text
);
ALTER SEQUENCE kn.sync_report_log_seq OWNED BY kn.sync_report_log.id;

--sync_norms_log table
CREATE SEQUENCE IF NOT EXISTS kn.sync_norms_log_seq;

CREATE TABLE IF NOT EXISTS kn.sync_norms_log
(
    id           integer     NOT NULL DEFAULT nextval('kn.sync_norms_log_seq') PRIMARY KEY,
    start_date   date        not null,
    end_date     date        not null,
    initiator    varchar(50) not null,
    process_date date        not null,
    error        varchar(1000)
);
ALTER SEQUENCE kn.sync_norms_log_seq OWNED BY kn.sync_norms_log.id;


--sync_norm_log table
CREATE SEQUENCE IF NOT EXISTS kn.sync_norm_log_seq;
CREATE TABLE IF NOT EXISTS kn.sync_norm_log
(
    id                integer NOT NULL DEFAULT nextval('kn.sync_norm_log_seq') PRIMARY KEY,
    sync_norms_log_id integer NOT NULL REFERENCES kn.sync_norms_log (id),
    error             varchar(1000),
    norm_plan_id      bigint,
    nlu_id            bigint,
    log_list          text
);
ALTER SEQUENCE kn.sync_norm_log_seq OWNED BY kn.sync_norm_log.id;