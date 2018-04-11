package com.dspro.logic.contactlist;

import com.dspro.dto.ContactListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ContactListBatchMapper implements RowMapper<ContactListDTO> {
    private static final Logger log = LoggerFactory.getLogger(ContactListBatchMapper.class);

    @Override
    public ContactListDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        ContactListDTO object = new ContactListDTO();
        object.ctn = resultSet.getString("CTN");
        object.targetCtn = resultSet.getString("TARGET_CTN");
        object.unlockLevel = resultSet.getInt("UNLOCK_LEVEL");
        return object;
    }
}
