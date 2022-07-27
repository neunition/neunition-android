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
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ca.neunition.R
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.adblockplus.libadblockplus.android.AndroidHttpClientResourceWrapper
import org.adblockplus.libadblockplus.android.settings.AdblockHelper
import org.adblockplus.libadblockplus.android.settings.AdblockSettingsStorage
import org.adblockplus.libadblockplus.android.webview.AdblockWebView

@SuppressLint("SourceLockedOrientationActivity")
class RecipeWebViewFragment(private val recipeTitle: String, private var recipeUrl: String) : DialogFragment(), ComponentCallbacks2 {
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: LinearProgressIndicator
    private var recipeWebView: AdblockWebView? = null

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
                .init(requireActivity(), requireActivity().filesDir.absolutePath, AdblockHelper.PREFERENCE_NAME)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        return inflater.inflate(R.layout.fragment_recipe_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.full_screen_dialog_toolbar)
        progressBar = view.findViewById(R.id.recipe_progress_bar)
        recipeWebView = view.findViewById(R.id.recipe_webview)

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
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AdblockHelper.get().provider.release()
        clearRecipeWebView()
        clearAdAndWebStorage()
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        clearRecipeWebView()
        clearAdAndWebStorage()
    }

    /**
     * Display the recipe in a webview.
     *
     * @param fragmentManager the fragment that will contain the full screen dialog fragment
     *
     * @return the webview fragment hosting the recipe
     */
    fun display(fragmentManager: FragmentManager): RecipeWebViewFragment {
        val fullScreenDialog = RecipeWebViewFragment(recipeTitle, recipeUrl)
        fullScreenDialog.show(fragmentManager, FULL_SCREEN_DIALOG_TAG)
        return fullScreenDialog
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

    companion object {
        private val FULL_SCREEN_DIALOG_TAG: String by lazy { "FULL_SCREEN_DIALOG" }
    }
}
