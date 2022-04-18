-- @Author Charlie Moreland
drop procedure if exists project.findUserPostingHashtag;
delimiter //
create procedure project.findUserPostingHashtag(in hashtagin varchar(80), in monthin integer, in yearin integer, in state varchar(80))
begin
select count(t.tid) as tweet_count, t.user_screen_name, u.category
from project.tweets t, project.hashtags h, project.users u
where h.name =hashtagin and h.tid=t.tid and t.user_screen_name=u.screen_name and u.state=state and t.post_month = monthin and t.post_year=yearin
group by t.user_screen_name order by count(t.tid) desc;
end;//
delimiter ;
