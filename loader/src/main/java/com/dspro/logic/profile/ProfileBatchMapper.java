package com.dspro.logic.profile;

import com.dspro.ProfileMapper;
import com.dspro.domain.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ProfileBatchMapper implements RowMapper<Profile> {
    private static final Logger log = LoggerFactory.getLogger(ProfileBatchMapper.class);

    @Override
    public Profile mapRow(ResultSet resultSet, int i) throws SQLException {
        return ProfileMapper.fromResultSet(resultSet);
    }
}
