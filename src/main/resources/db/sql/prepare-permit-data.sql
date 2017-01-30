--
-- Temporary solution to load the permit data into the database
--

-- Load site data
insert into sites(name)
  select distinct site from stage_permits;

-- Load permits
insert into unique_identifiers(name, site_id)
  select p.ea_id, cast(s.id as bigint) as site_id
  from stage_permits p
    join sites s on s.name = p.site;

-- Load aliases
insert into unique_identifier_aliases(unique_id, name)
  select cast(max(u.id) as bigint) as unique_id, p.alternatives as name
  from stage_permits p
    join unique_identifiers u on u.name = p.ea_id
  group by p.alternatives having count(*) = 1;