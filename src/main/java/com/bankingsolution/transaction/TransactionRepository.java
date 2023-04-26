package com.bankingsolution.transaction;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
interface TransactionRepository {

    @Insert("INSERT INTO transaction (amount, description,  direction, currency, created_on," +
            " account_id, balance_after) VALUES(#{amount}, #{description}," +
            " #{direction}, #{currency}, #{createdOn}, #{accountId}, #{balanceAfter})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Transaction transaction);

    @Select("SELECT * FROM transaction WHERE account_id = #{accountId}")
    List<Transaction> findByAccountId(Long accountId);

}
