alter table kn.template add column changed_user_id integer,
add column changed_user_name varchar (300),
add column changed_date date;

update kn.blank_norm set period='onDemand' where id in (select id from kn.blank_norm where blank_id in (
    SELECT id FROM kn.blank t WHERE template_id in (select template_id from kn.template_norm where         period='selected' and (custom_period is null or custom_period ='')))
    and period='selected' ) and name in (select name from kn.template_norm where         period='selected' and (custom_period is null or custom_period =''));

update kn.template_norm set period='onDemand'  where
period='selected' and (custom_period is null or custom_period ='');


