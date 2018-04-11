package com.dspro.logic.tags;

import com.dspro.dto.TagDTO;
import com.dspro.enums.ProfileField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TagBatchMapper implements RowMapper<TagDTO> {
    private static final Logger log = LoggerFactory.getLogger(TagBatchMapper.class);

    @Override
    public TagDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        TagDTO object = new TagDTO();
        object.ctn = resultSet.getString(ProfileField.CTN.db);
        object.category = resultSet.getString("CATEGORY");
        object.tag = resultSet.getString("TAG");
        return object;
    }
}
