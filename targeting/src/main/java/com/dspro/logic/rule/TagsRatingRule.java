package com.dspro.logic.rule;

import com.dspro.domain.MatcherInstance;
import com.dspro.domain.Profile;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Calculates two rating scores: total and tag.
 * Tag score is as number xx.yy, where xx = percentage of the same categories,
 * while yy - ratio (sum of private tags / sum of the same tags) per each same category
 * <p>
 * Total score is a derivative percentage of a category fraction
 * e.g. 3 categories = 30% each. Each category has 50% same tags, so 50% of 30% = 15%, so 15% * 3 (categories) out of 90 (rating fraction).
 * So it is 45% of 90. Total rating score is summed up across rules.
 */
public class TagsRatingRule extends AbstractRatingRule {
    private static final Logger log = LoggerFactory.getLogger(TagsRatingRule.class);

    private final static MathContext CONTEXT = new MathContext(3, RoundingMode.HALF_EVEN);
    private static BigDecimal RULE_FRACTION;
    private final static BigDecimal ONE_HUNDRED = new BigDecimal(100, CONTEXT);
    private static final int divisionScale = 3;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public TagsRatingRule(int ratingFraction) {
        super(ratingFraction);
        RULE_FRACTION = new BigDecimal(getRatingFraction(), CONTEXT);
    }

    @Override
    public void apply(MatcherInstance request, Profile myProfile) {
        log.debug("\n");
        log.debug("TagsRatingRule: start");
        final Map<String, Profile> allProfiles = request.newAdjustedModifiableProfiles();

        final Set<String> myCategories = myProfile.tags.keySet();
        final BigDecimal categoriesSelfNumber = new BigDecimal(myCategories.size(), CONTEXT);
        final BigDecimal categoryFraction = ONE_HUNDRED.divide(categoriesSelfNumber, CONTEXT);
        log.debug("my ({}) categories: {}, cat fraction: {}%", myProfile.ctn, myProfile.tags.keySet(), categoryFraction);


        if (CollectionUtils.isEmpty(myCategories)) {
            return;
        }

        for (Profile candidateProfile : allProfiles.values()) {
            final String candidateCtn = candidateProfile.ctn;
            final Set<String> candidateCategories = candidateProfile.tags.keySet();

            if (CollectionUtils.isEmpty(myCategories) || CollectionUtils.isEmpty(candidateCategories)) {
                continue;
            }

            final Set<String> matchedCategoriesCopy = new HashSet<>(myCategories);
            matchedCategoriesCopy.retainAll(candidateCategories);

            if (CollectionUtils.isEmpty(matchedCategoriesCopy)) {
                continue;
            }

            final BigDecimal categoriesMatchNumber = new BigDecimal(matchedCategoriesCopy.size(), CONTEXT);

            final int categoriesPercentage = categoriesMatchNumber.divide(categoriesSelfNumber, divisionScale, RoundingMode.HALF_EVEN).multiply(ONE_HUNDRED).intValue();

            log.debug("  {} has {}/{}={}% categories in common: {}/{}", candidateCtn, categoriesMatchNumber, categoriesSelfNumber, categoriesPercentage, candidateCategories, myCategories);

            BigDecimal myTotalTagsRollingTotal = new BigDecimal(0, CONTEXT); //per category
            BigDecimal tagsMatchRollingTotal = new BigDecimal(0, CONTEXT);  //per category
            BigDecimal percentageOfCategoryFractureRollingTotal = new BigDecimal(0, CONTEXT);  // how many tagsPercentage of categoryFraction? (33% (1 of 3 tags matched) of 33% (1 of 3 cat matched))

            for (String matchedCategory : matchedCategoriesCopy) {
                Collection<String> candidateTagsInCategory = candidateProfile.tags.get(matchedCategory);
                Collection<String> myTagsInCategory = myProfile.tags.get(matchedCategory);

                if (CollectionUtils.isEmpty(candidateTagsInCategory) || CollectionUtils.isEmpty(myTagsInCategory)) {
                    continue;
                }

                final Set<String> matchedTagsCopy = new HashSet<>(myTagsInCategory);
                matchedTagsCopy.retainAll(candidateTagsInCategory);

                if (CollectionUtils.isEmpty(matchedTagsCopy)) {
                    continue;
                }

                final BigDecimal tagsMatchNumber = new BigDecimal(matchedTagsCopy.size(), CONTEXT);
                final BigDecimal tagsSelfNumber = new BigDecimal(myTagsInCategory.size(), CONTEXT);
                final BigDecimal tagsPercentage = tagsMatchNumber.divide(tagsSelfNumber, CONTEXT);
                final BigDecimal tagsPercentageOfCategoryFracture = categoryFraction.multiply(tagsPercentage);


                tagsMatchRollingTotal = tagsMatchRollingTotal.add(tagsMatchNumber);
                myTotalTagsRollingTotal = myTotalTagsRollingTotal.add(tagsSelfNumber);
                percentageOfCategoryFractureRollingTotal = percentageOfCategoryFractureRollingTotal.add(tagsPercentageOfCategoryFracture);

                log.debug("    category '{}': {} has {}/{}={}% tags in common: {}/{}", matchedCategory, candidateCtn, tagsMatchNumber, tagsSelfNumber, tagsPercentage.multiply(ONE_HUNDRED), candidateTagsInCategory, myTagsInCategory);
                log.debug("        category '{}' fracture: {}% of {} = {}%", matchedCategory, tagsPercentage.multiply(ONE_HUNDRED), categoryFraction, tagsPercentageOfCategoryFracture);
                // todo add to rulesLog in request, because final rating are cumulative number and impossible to track individual numbers

            }

            final int totalTagsPercentage = tagsMatchRollingTotal.divide(myTotalTagsRollingTotal, CONTEXT).multiply(ONE_HUNDRED).intValue();
            final BigDecimal totalTagRating = percentageOfCategoryFractureRollingTotal.divide(ONE_HUNDRED, CONTEXT).multiply(RULE_FRACTION);

            log.debug("    {} of {} tags = {}% in common over matched categories {}", tagsMatchRollingTotal, myTotalTagsRollingTotal, totalTagsPercentage, matchedCategoriesCopy);
            log.debug("        {}% of {} = {} final rating", percentageOfCategoryFractureRollingTotal, RULE_FRACTION, totalTagRating);


            request.tagRatings.put(candidateCtn, categoriesPercentage + totalTagsPercentage / 100.0);
            request.totalRatings.put(candidateCtn, totalTagRating.doubleValue()); // 36% of 90-> 0.36 * 90
        }

        request.rulesLog.add(request.tagRatings);
        request.rulesLog.add(request.totalRatings);
        log.debug("tag ratings: {}", request.tagRatings);
        log.debug("total ratings: {}", request.totalRatings);
        log.debug("TagsRatingRule: end \n");
    }


}

