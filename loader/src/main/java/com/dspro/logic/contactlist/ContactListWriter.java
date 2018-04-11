package com.dspro.logic.contactlist;

import com.dspro.dto.ContactListDTO;
import com.dspro.enums.RedisConst;
import com.dspro.logic.ListRedisWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.StringRedisConnection;

public class ContactListWriter extends ListRedisWriter<ContactListDTO> {
    private static final Logger log = LoggerFactory.getLogger(ContactListWriter.class);

    @Override
    protected void process(ContactListDTO item, StringRedisConnection redis) {
        redis.zAdd(RedisConst.CL + item.ctn, item.unlockLevel, item.targetCtn);


    }
}
