-- Add in the additional parameters
with new_names as (
        select name, type, cas from parameters_name_supplement union
        select alias as name, type, cas from parameters_name_supplement
)
insert into parameters(name, type, cas)
        select distinct name, type, cas from new_names
        where new_names.name not in (select name from parameters)
        and new_names.name is not null and trim(name) != '';

-- update the preferred column of the aliases with the primaries
update parameters set preferred = (
        select name
        from parameters_name_supplement s
        where parameters.name = s.alias
) where exists (
    select null
    from parameters_name_supplement s2
    where parameters.name = s2.alias
          and parameters.preferred is null
);

-- This gives the new names to be added into the base table - both the aliases and the preferred
with new_names as (
	select name, type, cas from parameters_alias_supplement union
	select alias as name, type, cas from parameters_alias_supplement
)
insert into parameters(name, type, cas)
select distinct name, type, cas from new_names
 where name not in (select name from parameters)
 order by 1;

-- update the preferred column of the aliases with the primaries
update parameters set preferred = (
	select name
	  from parameters_alias_supplement s
	 where parameters.name = s.alias
) where exists (
	select null
	  from parameters_alias_supplement s2
	 where parameters.name = s2.alias
     and (parameters.preferred is null or trim(parameters.preferred) = '')
);