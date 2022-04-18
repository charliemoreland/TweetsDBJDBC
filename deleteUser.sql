-- @Author Charlie Moreland
drop procedure if exists project.deleteUser;
delimiter //
create procedure project.deleteUser(in username varchar(80))
begin


delete m
from project.mentions m left join project.tweets t on t.tid=m.tid
where m.screen_name = username and t.tid = m.tid;


delete h
from project.hashtags h, project.tweets t
where h.tid=t.tid and t.user_screen_name = username;


delete m
from project.mentions m, project.tweets t
where t.user_screen_name = username and m.tid=t.tid;


delete u
from project.urls u, project.tweets t
where t.user_screen_name = username and u.tid=t.tid;

delete t
from project.tweets t where t.user_screen_name = username;

delete u
from project.users u 
where screen_name = username;
end;//

delimiter ;