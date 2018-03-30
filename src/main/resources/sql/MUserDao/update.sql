update m_user
   set update_date = :updateDate
    <#if password??>
      ,password = :password
    </#if>
    <#if mail??>
      ,mail = :mail
    </#if>
    <#if lineId??>
      ,line_id = :lineId
    </#if>
    <#if delFlg??>
      ,del_flg = :delFlg
    </#if>
where user_id = :userId