package com.dspro.logic.tags;

import com.dspro.dto.TagDTO;
import com.dspro.enums.RedisConst;
import com.dspro.logic.ListRedisWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.StringRedisConnection;

public class CbctTbcPtWriter extends ListRedisWriter<TagDTO> {


    @Override
    protected void process(TagDTO item, StringRedisConnection redis) {
        //todo cant have cat w/o tag in link table because it is not put in cbct
        if (StringUtils.isNoneEmpty(item.category, item.tag)) {
            // 1 - ctns, 2 - profile, 3 - cl

            // 4. cbct
            redis.sAdd(RedisConst.CTNS_BY_CAT_TAG + item.category + ":" + item.tag, item.ctn);

            // 5. ubu

            // 6. tbc
            redis.zIncrBy(RedisConst.TAGS_BY_CAT + item.category, 1.0, item.tag);

            // 7. pt
            redis.sAdd(RedisConst.PROFILE_TAGS + item.ctn, item.tag);

            // 8,9 - OV and OP.
        }

    }
}
