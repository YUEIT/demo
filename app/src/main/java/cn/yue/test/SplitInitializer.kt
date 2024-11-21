package cn.yue.test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.startup.Initializer
import androidx.window.embedding.ActivityFilter
import androidx.window.embedding.ActivityRule
import androidx.window.embedding.EmbeddingAspectRatio
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitPairFilter
import androidx.window.embedding.SplitPairRule
import androidx.window.embedding.SplitPlaceholderRule
import androidx.window.embedding.SplitRule
import cn.yue.base.activity.CommonActivity
import cn.yue.test.main.DetailActivity
import cn.yue.test.main.MainActivity
import cn.yue.test.main.PlaceholderActivity

class SplitInitializer : Initializer<RuleController> {

    override fun create(context: Context): RuleController {
        return RuleController.getInstance(context).apply {
            initRule(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    private fun RuleController.initRule(context: Context) {
        //分屏规则
        val splitPairFilter = SplitPairFilter(
            ComponentName(context, MainActivity::class.java),
            ComponentName(context, DetailActivity::class.java),
            null
        )
        val filterSet = setOf(splitPairFilter)
        val splitAttributes: SplitAttributes = SplitAttributes.Builder()
            .setSplitType(SplitAttributes.SplitType.ratio(0.33f))
            .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
            .build()
        val splitPairRule = SplitPairRule.Builder(filterSet)
            .setDefaultSplitAttributes(splitAttributes)
            .setMinWidthDp(840)
            .setMinSmallestWidthDp(600)
            .setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ratio(1.5f))
            .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.NEVER)
            .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
            .setClearTop(false)
            .build()
        addRule(splitPairRule)

        //站位规则
        val placeholderActivityFilter = ActivityFilter(
            ComponentName(context, MainActivity::class.java),
            null
        )
        val placeholderActivityFilterSet = setOf(placeholderActivityFilter)
        val splitPlaceholderRule = SplitPlaceholderRule.Builder(
            placeholderActivityFilterSet,
            Intent(context, PlaceholderActivity::class.java)
        ).setDefaultSplitAttributes(splitAttributes)
            .setMinWidthDp(840)
            .setMinSmallestWidthDp(600)
            .setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ratio(1.5f))
            .setFinishPrimaryWithPlaceholder(SplitRule.FinishBehavior.ALWAYS)
            .setSticky(false)
            .build()
        addRule(splitPlaceholderRule)

        //全屏规则
        val expandedActivityFilter = ActivityFilter(
            ComponentName(context, CommonActivity::class.java),
            null
        )
        val expandedActivityFilterSet = setOf(expandedActivityFilter)
        val activityRule = ActivityRule.Builder(expandedActivityFilterSet)
            .setAlwaysExpand(true)
            .build()
        addRule(activityRule)
    }
}