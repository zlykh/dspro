package com.dspro;

import com.dspro.domain.Profile;
import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.dspro.enums.ProfileField.*;

public class ProfileMapper {

    public static Profile fromResultSet(ResultSet resultSet) throws SQLException {
        Profile p = new Profile();
        p.ctn = resultSet.getString(CTN.db);
        p.age = resultSet.getInt(AGE.db);
        p.city = resultSet.getString(CITY.db);
        p.name = resultSet.getString(NAME.db);

        return p;
    }

    public static Profile fromMap(Map<String, String> profileMap) {
        if (profileMap == null || profileMap.isEmpty())
            return null;

        Profile p = new Profile();
        p.name = profileMap.get(NAME.redis);
        p.city = profileMap.get(CITY.redis);
        p.age = NumberUtils.toInt(profileMap.get(AGE.redis), 0);
        p.tags = HashMultimap.create();
        p.categories = HashMultimap.create();

        return p;
    }

    public static Map<String, String> toMap(Profile p) {
        if (p == null)
            return null;
        Map<String, String> result = new HashMap<>(5);
        result.put(NAME.redis, Objects.toString(p.name, ""));
        result.put(AGE.redis, Objects.toString(p.age, ""));
        result.put(CITY.redis, Objects.toString(p.city, ""));

        return result;
    }

}
