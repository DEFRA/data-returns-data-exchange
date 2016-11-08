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

-- Load the aliases
-- In a small number of cases a historic alias can
-- refer to
insert into unique_identifier_aliases(unique_id, name)
  with p as (
    select l.lic_wml, l.lic_epr as name
    from stage_emma e
      join stage_lichold l on l.lic_wml = e.permit
    where l.lic_epr != ''
    union
    select l.lic_wml, l.lic_othid
    from stage_emma e
      join stage_lichold l on l.lic_wml = e.permit
    where l.lic_othid != ''
    union
    select l.lic_wml, l.pas_permit
    from stage_emma e
      join stage_lichold l on l.lic_wml = e.permit
    where l.pas_permit != ''
  ), q as (
      select max(lic_wml) as permit, name from p
      group by name having count(lic_wml) = 1
  )
  select u.id, q.name
  from q
    join unique_identifiers u on u.name = q.permit;
