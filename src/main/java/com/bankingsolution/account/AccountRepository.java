package com.bankingsolution.account;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface AccountRepository {

    @Insert("INSERT INTO account (customer_id, country,  created_on)" +
            " VALUES(#{customerId}, #{country}, #{createdOn})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Account account);

    @Select("SELECT * FROM account WHERE id = #{id}")
    Optional<Account> findById(Long id);

}
