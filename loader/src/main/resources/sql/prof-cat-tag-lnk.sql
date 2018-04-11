select prof.name as name, cat.name as category, tag.name as tag from prof_cat_tag_lnk lnk
join profile prof on prof.profile_id = lnk.profile_id
left join category cat on cat.category_id = lnk.category_id
left join tag on tag.tag_id = lnk.tag_id;