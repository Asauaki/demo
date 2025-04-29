@echo off
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
mysqldump -u root -p123456 film_comment_system > film_comment_system_backup_%TIMESTAMP%.sql
echo Database backup completed: film_comment_system_backup_%TIMESTAMP%.sql
pause 