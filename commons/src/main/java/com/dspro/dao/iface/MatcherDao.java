package com.dspro.dao.iface;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Set;

/**
 * Created by dmitry.zlykh
 */
public interface MatcherDao {

    Set<String> getSimilarOnTag(String category, String tag, int count);

    Multimap<String, String> getSimilarOnTags(String category, Collection<String> tags);
}

