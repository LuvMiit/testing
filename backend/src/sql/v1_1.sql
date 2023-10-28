alter table kn.template_norm add column docs varchar(300);
alter table kn.template_norm add column  akt_addon varchar(100);
alter table kn.template_norm add column  recording_addon varchar(100);
alter table kn.template_norm add column  other_addon varchar(100);
alter table kn.template_norm add column  custom_period varchar(12);
alter table kn.blank_norm add column  custom_period integer  not null  default  0;
alter table kn.blank_norm add column  docs varchar(500);