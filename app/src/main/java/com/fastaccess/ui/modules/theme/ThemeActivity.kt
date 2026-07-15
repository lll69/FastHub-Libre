package com.fastaccess.ui.modules.theme

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.AdapterView
import android.widget.Spinner
import butterknife.BindView
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.main.premium.PremiumActivity
import com.fastaccess.ui.modules.theme.fragment.ThemeFragmentMvp
import com.fastaccess.ui.widgets.CardsPagerTransformerBasic
import com.fastaccess.ui.widgets.ViewPagerView


/**
 * Created by Kosh on 08 Jun 2017, 10:34 PM
 */

class ThemeActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(), ThemeFragmentMvp.ThemeListener, AdapterView.OnItemSelectedListener {

    @BindView(R.id.pager) lateinit var pager: ViewPagerView
    @BindView(R.id.parentLayout) lateinit var parentLayout: View
    @BindView(R.id.systemModeSpinner) lateinit var systemModeSpinner: Spinner

    @OnClick(R.id.premium) fun onOpenPremium() {
        PremiumActivity.startActivity(this)
    }

    override fun layout(): Int = R.layout.theme_viewpager

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }

    override fun isDarkMode(): Boolean {
        return if(!::systemModeSpinner.isInitialized) super.isDarkMode() else getSelectedSystemMode() == 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pager.adapter = FragmentsPagerAdapter(supportFragmentManager, FragmentPagerAdapterModel.buildForTheme())
        pager.clipToPadding = false
        val partialWidth = resources.getDimensionPixelSize(R.dimen.spacing_s_large)
        val pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        val pagerPadding = partialWidth + pageMargin
        pager.pageMargin = pageMargin
        pager.setPageTransformer(true, CardsPagerTransformerBasic(4, 10))
        pager.setPadding(pagerPadding, pagerPadding, pagerPadding, pagerPadding)
        if (savedInstanceState == null) {
            val theme = PrefGetter.getThemeType(this, getSelectedSystemMode() == 1)
            pager.setCurrentItem(theme - 1, true)
        }
        systemModeSpinner.setSelection(if(super.isDarkMode()) 1 else 0)
        systemModeSpinner.setOnItemSelectedListener(this)
    }

    override fun onChangePrimaryDarkColor(color: Int, darkIcons: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = window.decorView
            view.systemUiVisibility = if (darkIcons) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else view.systemUiVisibility and View
                    .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        val cx = parentLayout.width / 2
        val cy = parentLayout.height / 2
        if (parentLayout.isAttachedToWindow) {
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(parentLayout, cx, cy, 0f, finalRadius)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    window?.statusBarColor = color
                    changeNavColor(color)
                }
            })
            parentLayout.setBackgroundColor(color)
            anim.start()
        } else {
            parentLayout.setBackgroundColor(color)
            window.statusBarColor = color
            changeNavColor(color)
        }
    }

    private fun changeNavColor(color: Int) {
        window?.navigationBarColor = color
    }

    override fun onThemeApplied() {
        showMessage(R.string.success, R.string.change_theme_warning)
        onThemeChanged()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent == systemModeSpinner) {
            val theme = PrefGetter.getThemeType(this, position == 1)
            pager.setCurrentItem(theme - 1, true)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    public fun getSelectedSystemMode(): Int {
        return systemModeSpinner.getSelectedItemPosition()
    }
}