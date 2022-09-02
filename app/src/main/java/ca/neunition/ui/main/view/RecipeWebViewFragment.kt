/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Show the web content for the selected recipe in a full screen dialog.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.annotation.SuppressLint
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import ca.neunition.R
import ca.neunition.util.Constants
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.adblockplus.libadblockplus.android.AndroidHttpClientResourceWrapper
import org.adblockplus.libadblockplus.android.settings.AdblockHelper
import org.adblockplus.libadblockplus.android.settings.AdblockSettingsStorage
import org.adblockplus.libadblockplus.android.webview.AdblockWebView


@SuppressLint("SourceLockedOrientationActivity")
class RecipeWebViewFragment(
    private val recipeTitle: String,
    private val recipeUrl: String
) : DialogFragment(), ComponentCallbacks2 {
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: LinearProgressIndicator
    private var recipeWebView: AdblockWebView? = null

    private lateinit var adaptiveBannerAdView: AdView
    private lateinit var adViewContainer: FrameLayout
    private var initialLayoutComplete = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        if (!AdblockHelper.get().isInit) {
            val map = HashMap<String, Int>()
            map[AndroidHttpClientResourceWrapper.EASYLIST] = R.raw.easylist

            AdblockHelper
                .get()
                .init(
                    requireActivity(),
                    requireActivity().filesDir.absolutePath,
                    AdblockHelper.PREFERENCE_NAME
                )
                .preloadSubscriptions(AdblockHelper.PRELOAD_PREFERENCE_NAME, map)
        }

        AdblockHelper.get().provider.retain(true)

        var adBlockSettings = AdblockHelper.get().storage.load()
        if (adBlockSettings == null) {
            adBlockSettings = AdblockSettingsStorage.getDefaultSettings(requireActivity())
        }
        adBlockSettings.isAdblockEnabled = true
        AdblockHelper.get().storage.save(adBlockSettings)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        return inflater.inflate(R.layout.fragment_recipe_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.full_screen_dialog_toolbar)
        progressBar = view.findViewById(R.id.recipe_progress_bar)
        recipeWebView = view.findViewById(R.id.recipe_webview)
        adViewContainer = view.findViewById(R.id.ad_view_web_container)

        adaptiveBannerAdView = AdView(requireActivity())
        adViewContainer.addView(adaptiveBannerAdView)
        adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadAdaptiveBanner()
            }
        }

        toolbar.also {
            it.setNavigationOnClickListener {
                clearRecipeWebView()
                clearAdAndWebStorage()
                dismiss()
            }
            it.title = recipeTitle
        }

        recipeWebView?.also {
            it.webChromeClient = WebChromeClient()
            it.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            it.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            it.settings.apply {
                allowFileAccess = true
                builtInZoomControls = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                displayZoomControls = false
                domStorageEnabled = true
                javaScriptEnabled = true
                loadWithOverviewMode = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                useWideViewPort = true
            }
            it.setProvider(AdblockHelper.get().provider)
            it.siteKeysConfiguration = AdblockHelper.get().siteKeysConfiguration
            it.enableJsInIframes(true)
        }

        recipeWebView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageCommitVisible(view: WebView, url: String) {
                super.onPageCommitVisible(view, url)
                progressBar.visibility = View.INVISIBLE
            }
        }

        recipeWebView?.loadUrl(recipeUrl)
    }

    override fun onResume() {
        super.onResume()
        adaptiveBannerAdView.resume()
    }

    override fun onPause() {
        adaptiveBannerAdView.pause()
        super.onPause()
    }

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL && AdblockHelper.get().isInit) {
            AdblockHelper.get().provider.engine.onLowMemory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        clearRecipeWebView()
        clearAdAndWebStorage()
    }

    override fun onDestroy() {
        adaptiveBannerAdView.destroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AdblockHelper.get().provider.release()
        clearRecipeWebView()
        clearAdAndWebStorage()
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        clearRecipeWebView()
        clearAdAndWebStorage()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initialLayoutComplete = false
        adViewContainer.removeView(adaptiveBannerAdView)
        adaptiveBannerAdView.destroy()

        adaptiveBannerAdView = AdView(requireActivity())
        adViewContainer.addView(adaptiveBannerAdView)
        adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadAdaptiveBanner()
            }
        }
    }

    private fun adAdapterSize(): AdSize {
        val display = requireActivity().windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adViewContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireActivity(), adWidth)
    }

    private fun loadAdaptiveBanner() {
        adaptiveBannerAdView.adUnitId = Constants.BANNER_AD_UNIT_ID
        adaptiveBannerAdView.setAdSize(adAdapterSize())
        adaptiveBannerAdView.loadAd(Constants.AD_REQUEST)
    }

    /**
     * Clear webview memory.
     */
    private fun clearRecipeWebView() {
        recipeWebView?.apply {
            stopLoading()
            tag = null
            clearCache(true)
            clearHistory()
            clearFormData()
            clearSslPreferences()
            invalidate()
            removeAllViews()
            destroy()
        }
        recipeWebView = null
    }

    /**
     * Clear cookies and other data from webview.
     */
    private fun clearAdAndWebStorage() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }
}
