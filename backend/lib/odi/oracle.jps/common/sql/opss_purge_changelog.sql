
SET ECHO ON
SET FEEDBACK 1
SET NUMWIDTH 10
SET LINESIZE 80
SET TRIMSPOOL ON
SET TAB OFF
SET PAGESIZE 100

delete from jps_changelog where createdate < (select(max(createdate) - 1) from jps_changelog);
commit;

