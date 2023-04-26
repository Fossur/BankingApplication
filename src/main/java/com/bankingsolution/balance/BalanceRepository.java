package com.bankingsolution.balance;

import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.List;

@Mapper
public interface BalanceRepository {

    @Insert("INSERT INTO balance (amount, account_id, currency, created_on, valid_from)" +
            " VALUES(#{amount}, #{accountId}, #{currency}, #{createdOn}, #{validFrom})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Balance balance);

    @Select("SELECT * FROM balance WHERE id = #{id}")
    Optional<Balance> findById(Long id);

    @Select("SELECT * FROM balance WHERE account_id = #{id} AND valid_to IS NULL FOR UPDATE")
    List<Balance> findValidByAccountId(Long id);

    @Select("SELECT * FROM balance WHERE account_id = #{accountId}" +
        " AND currency = #{currency} AND valid_to IS NULL FOR UPDATE")
    Optional<Balance> findValidBalanceByAccountIdAndCurrency(Long accountId, Currency currency);

    @Update("UPDATE balance SET valid_to = now() WHERE id = #{id} FOR UPDATE")
    void invalidateBalance(Balance balance);

}
