/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Show the web content for the selected recipe in a full screen dialog.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.main.view

import android.content.Context
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
import org.adblockplus.libadblockplus.android.AdblockEngine
import org.adblockplus.libadblockplus.android.settings.AdblockHelper
import org.adblockplus.libadblockplus.android.settings.AdblockSettingsStorage
import org.adblockplus.libadblockplus.android.webview.AdblockWebView

class RecipeWebViewFragment(private val recipeTitle: String, private var recipeUrl: String) : DialogFragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: LinearProgressIndicator
    private var recipeWebView: AdblockWebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
        if (!AdblockHelper.get().isInit) {
            val adblockBasePath = requireContext().getDir(AdblockEngine.BASE_PATH_DIRECTORY, Context.MODE_PRIVATE).absolutePath
            AdblockHelper.get().init(requireContext(), adblockBasePath, AdblockHelper.PREFERENCE_NAME)
            AdblockHelper.get().provider.retain(false);
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_full_screen_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.full_screen_dialog_toolbar)
        progressBar = view.findViewById(R.id.recipe_progress_bar)
        recipeWebView = view.findViewById(R.id.recipe_webview)

        val adBlockStorage = AdblockHelper.get().storage

        var adBlockSettings = adBlockStorage.load()
        if (adBlockSettings == null) {
            adBlockSettings = AdblockSettingsStorage.getDefaultSettings(requireContext())
        }

        adBlockSettings.isAdblockEnabled = true
        adBlockStorage.save(adBlockSettings)

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
                allowFileAccess = false
                allowContentAccess = false
                javaScriptEnabled = true
                domStorageEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                cacheMode = WebSettings.LOAD_NO_CACHE
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadsImagesAutomatically = true
            }
            it.setProvider(AdblockHelper.get().provider)
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

    fun display(fragmentManager: FragmentManager): RecipeWebViewFragment {
        val fullScreenDialog = RecipeWebViewFragment(recipeTitle, recipeUrl)
        fullScreenDialog.show(fragmentManager, FULL_SCREEN_DIALOG_TAG)
        return fullScreenDialog
    }

    private fun clearRecipeWebView() {
        recipeWebView?.apply {
            clearCache(true)
            clearHistory()
            clearFormData()
            clearSslPreferences()
            invalidate()
            destroy()
        }
        recipeWebView = null
    }

    private fun clearAdAndWebStorage() {
        AdblockHelper.get().provider.release()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }

    companion object {
        private const val FULL_SCREEN_DIALOG_TAG = "FULL_SCREEN_DIALOG"
    }
}
