package application.dao;

import application.emuns.DelFlag;
import application.entity.AbstractEntity;

/**
 * DAO共通抽象クラス
 */
abstract class AbstractDao<T extends AbstractEntity> {
    abstract int insert(T entity);
    abstract int update(T entity);

    int delete(T entity) {
        entity.delFlg = DelFlag.ON.getVal();
        return update(entity);
    }
}
