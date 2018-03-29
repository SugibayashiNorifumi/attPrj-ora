UPDATE m_org
   SET
       org_name = :orgName
      ,location = :location
      ,disp_seq = :dispSeq
      ,regist_date = :registDate
      ,regist_user_id = :registUserId
      ,regist_func_cd = :registFuncCd
      ,del_flg = :delFlg
WHERE org_cd = :orgCd
