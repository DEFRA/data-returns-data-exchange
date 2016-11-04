--
-- Temporary solution to load the permit data into the database
--

-- Load site data
insert into sites(name)
select distinct trim(substr(long_name, strpos(long_name, '-') + 1)) from stage_emma;

-- Load Emma permits
insert into unique_identifiers(name, site_id)
  select e.permit, cast(s.id as bigint) as site_id
  from stage_emma e
    join sites s on s.name = trim(substr(long_name, strpos(long_name, '-') + 1));

