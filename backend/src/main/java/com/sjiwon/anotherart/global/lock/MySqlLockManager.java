package com.sjiwon.anotherart.global.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySqlLockManager implements LockManager {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void acquire(final String key, final int timeout) {
//        final Map<String, Object> params = new HashMap<>();
//        params.put("key", key);
//        params.put("timeout", timeout);
//
//        final Integer result = jdbcTemplate.queryForObject(
//                "SELECT GET_LOCK(:key, :timeout)",
//                params,
//                Integer.class
//        );
//        checkResult(result, key, QueryType.GET_LOCK);

        final Integer result = jdbcTemplate.getJdbcTemplate().query(con -> {
                    log.info(">> GET_LOCK [{}] -> Connection = [{}] || Key = [{}] || Timeout = [{}]", Thread.currentThread().getName(), con, key, timeout);
                    final PreparedStatement preparedStatement = con.prepareStatement("SELECT GET_LOCK(?, ?)");
                    preparedStatement.setString(1, key);
                    preparedStatement.setInt(2, timeout);
                    return preparedStatement;
                }, rs -> {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    return null;
                }
        );
        checkResult(result, key, QueryType.GET_LOCK);
    }

    @Override
    public void release(final String key) {
//        final Map<String, Object> params = new HashMap<>();
//        params.put("key", key);
//
//        final Integer result = jdbcTemplate.queryForObject(
//                "SELECT RELEASE_LOCK(:key)",
//                params,
//                Integer.class
//        );
//        checkResult(result, key, QueryType.RELEASE_LOCK);

        final Integer result = jdbcTemplate.getJdbcTemplate().query(con -> {
                    log.info(">> RELEASE_LOCK [{}] -> Connection = [{}] || Key = [{}]", Thread.currentThread().getName(), con, key);
                    final PreparedStatement preparedStatement = con.prepareStatement("SELECT RELEASE_LOCK(?)");
                    preparedStatement.setString(1, key);
                    return preparedStatement;
                }, rs -> {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    return null;
                }
        );
        checkResult(result, key, QueryType.RELEASE_LOCK);
    }

    private void checkResult(final Integer result, final String key, final QueryType type) {
        if (result == null) {
            log.error("Named Lock 쿼리 결과가 null입니다 -> type = [{}], key = [{}]", type, key);
            throw new RuntimeException("Named lock result is null...");
        }
        if (result != 1) {
            log.error("Named Lock 쿼리 결과가 1(성공)이 아닙니다 -> type = [{}], result = [{}] key = [{}]", type, result, key);
            throw new RuntimeException("Named lock failed...");
        }
    }

    private enum QueryType {
        GET_LOCK, RELEASE_LOCK
    }
}
