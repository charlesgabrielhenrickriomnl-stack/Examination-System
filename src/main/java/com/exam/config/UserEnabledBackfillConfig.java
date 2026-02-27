package com.exam.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEnabledBackfillConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void backfillNullEnabledUsers() {
        jdbcTemplate.update("UPDATE users SET enabled = 1 WHERE enabled IS NULL");
    }
}
