CREATE TABLE m_setting(
  open_time CHAR(2) NOT NULL DEFAULT '09'
 ,open_minutes CHAR(2) NOT NULL DEFAULT '00'
 ,close_time CHAR(2) NOT NULL DEFAULT '18'
 ,close_minutes CHAR(2) NOT NULL DEFAULT '00'
 ,alert_open_time CHAR(2) NOT NULL DEFAULT '08'
 ,alert_open_minutes CHAR(2) NOT NULL DEFAULT '55'
 ,alert_close_time CHAR(2) NOT NULL DEFAULT '20'
 ,alert_close_minutes CHAR(2) NOT NULL DEFAULT '00'
 ,business_flag_mon CHAR(1) NOT NULL DEFAULT '1'
 ,business_flag_tue CHAR(1) NOT NULL DEFAULT '1'
 ,business_flag_wed CHAR(1) NOT NULL DEFAULT '1'
 ,business_flag_thu CHAR(1) NOT NULL DEFAULT '1'
 ,business_flag_fri CHAR(1) NOT NULL DEFAULT '1'
 ,business_flag_sat CHAR(1) NOT NULL DEFAULT '0'
 ,business_flag_sun CHAR(1) NOT NULL DEFAULT '0'
 ,alert_flag CHAR(1) NOT NULL DEFAULT '1'
 ,regist_date TIMESTAMP DEFAULT now() NOT NULL
 ,regist_user_id INTEGER DEFAULT 0 NOT NULL
 ,regist_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,update_date TIMESTAMP DEFAULT now() NOT NULL
 ,update_user_id INTEGER DEFAULT 0 NOT NULL
 ,update_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,del_flg CHAR(1) DEFAULT '0' NOT NULL
);
COMMENT ON TABLE m_setting IS '設定マスタ';

CREATE TABLE m_user(
  user_id INTEGER NOT NULL
 ,password VARCHAR(64) NOT NULL
 ,name VARCHAR(40) NOT NULL
 ,mail VARCHAR(60) NOT NULL
 ,auth_cd CHAR(2)
 ,org_cd CHAR(2)
 ,manager_id INTEGER
 ,line_id VARCHAR(255)
 ,regist_date TIMESTAMP DEFAULT now() NOT NULL
 ,regist_user_id INTEGER DEFAULT 0 NOT NULL
 ,regist_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,update_date TIMESTAMP DEFAULT now() NOT NULL
 ,update_user_id INTEGER DEFAULT 0 NOT NULL
 ,update_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,del_flg CHAR(1) DEFAULT '0' NOT NULL
 ,PRIMARY KEY(user_id)
);
COMMENT ON TABLE m_user IS 'ユーザマスタ';

CREATE TABLE m_org(
  org_cd CHAR(2) NOT NULL
 ,org_name VARCHAR(20) NOT NULL
 ,location VARCHAR(20)
 ,disp_seq INTEGER NOT NULL
 ,regist_date TIMESTAMP DEFAULT now() NOT NULL
 ,regist_user_id INTEGER DEFAULT 0 NOT NULL
 ,regist_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,update_date TIMESTAMP DEFAULT now() NOT NULL
 ,update_user_id INTEGER DEFAULT 0 NOT NULL
 ,update_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,del_flg CHAR(1) DEFAULT '0' NOT NULL
 ,PRIMARY KEY(org_cd)
);
COMMENT ON TABLE m_org IS '組織マスタ';

CREATE TABLE m_div_details(
  div_id INTEGER NOT NULL
 ,div_cd CHAR(2) NOT NULL
 ,div_cd_content VARCHAR(40) NOT NULL
 ,regist_date TIMESTAMP DEFAULT now() NOT NULL
 ,regist_user_id INTEGER DEFAULT 0 NOT NULL
 ,regist_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,update_date TIMESTAMP DEFAULT now() NOT NULL
 ,update_user_id INTEGER DEFAULT 0 NOT NULL
 ,update_func_cd VARCHAR(20) DEFAULT '0' NOT NULL
 ,del_flg CHAR(1) DEFAULT '0' NOT NULL
 ,PRIMARY KEY(div_id, div_cd)
);
COMMENT ON TABLE m_div_details IS '汎用区分明細マスタ';