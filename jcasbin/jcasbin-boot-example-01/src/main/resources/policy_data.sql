INSERT INTO `casbin-lab`.casbin_rule (ptype,v0,v1,v2,v3,v4,v5) VALUES
   ('g','admin','ROLE_ADMIN',NULL,NULL,NULL,NULL),
   ('g','admin','ROLE_LEADER',NULL,NULL,NULL,NULL),
   ('g','admin','ROLE_NORMAL',NULL,NULL,NULL,NULL),
   ('g','kwseeker','ROLE_LEADER',NULL,NULL,NULL,NULL),
   ('g','kwseeker','ROLE_NORMAL',NULL,NULL,NULL,NULL),
   ('g','arvin','ROLE_NORMAL',NULL,NULL,NULL,NULL),
   ('p','ROLE_ADMIN','/user/*','(POST|GET|PUT)',NULL,NULL,NULL),
   ('p','ROLE_LEADER','/user/manage','(POST|GET)',NULL,NULL,NULL),
   ('p','ROLE_LEADER','/user/info','(POST|GET|PUT)',NULL,NULL,NULL),
   ('p','ROLE_NORMAL','/user/info','(POST|GET)',NULL,NULL,NULL);