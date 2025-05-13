package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.FeedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedFileJdbcRepositoryImpl implements FeedFileJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllFeedFiles(List<FeedFile> files) {
        jdbcTemplate.batchUpdate("INSERT INTO feed_file (feed_id, url, type, created_at) " +
                "VALUES(?, ?, ?, ?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    FeedFile file = files.get(i);
                    ps.setLong(1, file.getFeed().getId());
                    ps.setString(2, file.getUrl());
                    ps.setString(3, file.getType().toString());
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                }

                @Override
                public int getBatchSize() {
                    return files.size();
                }
            }
        );
    }
}
