with alias as (
  select m0.name as "name", null as "preferred", m0.type as "type", m0.cas as "cas" from parameters_preload m0 union
  select m1.alias1 as "name", m1.name as "preferred", null as "type", null as "cas" from parameters_preload m1 where m1.alias1 is not null union
  select m2.alias2 as "name", m2.name as "preferred", null as "type", null as "cas" from parameters_preload m2 where m2.alias2 is not null union
  select m3.alias3 as "name", m3.name as "preferred", null as "type", null as "cas" from parameters_preload m3 where m3.alias3 is not null union
  select m4.alias4 as "name", m4.name as "preferred", null as "type", null as "cas" from parameters_preload m4 where m4.alias4 is not null union
  select m5.alias5 as "name", m5.name as "preferred", null as "type", null as "cas" from parameters_preload m5 where m5.alias5 is not null union
  select m6.alias6 as "name", m6.name as "preferred", null as "type", null as "cas" from parameters_preload m6 where m6.alias6 is not null union
  select m7.alias7 as "name", m7.name as "preferred", null as "type", null as "cas" from parameters_preload m7 where m7.alias7 is not null
)
insert into parameters(name, preferred, type, cas)
  select name, preferred, type, cas
  from alias
  order by name;
