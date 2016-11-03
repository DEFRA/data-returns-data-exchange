--
-- Temporary solution to load the permit data into the database
--
delete from sites;

insert into sites(name)
select distinct trim(substr(long_name, strpos(long_name, '-') + 1)) from stage_emma;

