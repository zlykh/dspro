package com.dspro.logic;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;

public class SqlResourceReader<T> extends JdbcCursorItemReader<T> {
    private static final Logger log = LoggerFactory.getLogger(SqlResourceReader.class);

    private String sqlFileName;

    public void setSqlFileName(String sqlFileName) {
        log.info("sqlFileName = {}", sqlFileName);
        this.sqlFileName = sqlFileName;
        try {
            String sql = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(sqlFileName));
            setSql(sql);
        } catch (Exception e) {
            log.error("SQL source ({}) can not be loaded.\n", sqlFileName);
        }
    }
}
