CREATE TABLE etl_task_cfg (
  task_id int NOT NULL,
  task_name varchar(200)  NOT NULL,
  table_name varchar(300)  ,
  remark varchar(200)  ,
  database_src varchar(100) ,
  task_obj varchar(300)  NOT NULL,
  para1 varchar(150)  ,
  para2 varchar(150)  ,
  para3 varchar(300),
  log_home varchar(150)  NOT NULL,
  log_file varchar(150)  NOT NULL,
  task_group_id int ,
  cycle varchar(2)  NOT NULL,
  prio_lvl int NOT NULL,
  if_recall int NOT NULL,
  if_wait int NOT NULL,
  if_pre int NOT NULL,
  if_val int NOT NULL,
  add_user varchar(20)  ,
  add_time timestamp NULL ,
  update_user varchar(20)  ,
  update_time timestamp NULL ,
  type int NOT NULL,
  offset varchar(20) NOT NULL,
  offset_type varchar(10) NOT NULL,
  freq varchar(200) ,
  owner varchar(100) NOT NULL,
  wait_code varchar(100) ,
  recall_code varchar(100) ,
  timeout int NOT NULL,
  recall_interval int NOT NULL,
  recall_limit int NOT NULL,
  success_code varchar(50) NOT NULL,
  PRIMARY KEY (task_id)
);

CREATE TABLE etl_taskrela_cfg (
  task_id int NOT NULL,
  task_pre_id int NOT NULL,
  remark varchar(200),
  cycle_gap varchar(5) NOT NULL,
  time_stamp timestamp NOT NULL,
  PRIMARY KEY (task_id,task_pre_id,cycle_gap),
);
