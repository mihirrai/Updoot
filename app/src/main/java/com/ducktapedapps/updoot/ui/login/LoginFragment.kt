package com.ducktapedapps.updoot.ui.login

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ducktapedapps.updoot.R
import com.ducktapedapps.updoot.UpdootApplication
import com.ducktapedapps.updoot.databinding.FragmentLoginBinding
import com.ducktapedapps.updoot.ui.login.LoginState.*
import com.ducktapedapps.updoot.ui.login.ResultState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class LoginFragment : Fragment() {

    @Inject
    lateinit var vmFactory: LoginVMFactory
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by lazy { ViewModelProvider(this, vmFactory).get(LoginViewModel::class.java) }

    override fun onAttach(context: Context) {
        (requireActivity().application as UpdootApplication).updootComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        binding = FragmentLoginBinding.inflate(layoutInflater)

        clearCookies()

        observeViewModel()

        binding.webView.apply {
            loadUrl(viewModel.authUrl)
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) = viewModel.parseUrl(Uri.parse(url))
            }
        }
        return binding.root
    }

    private fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    private fun observeViewModel() {
        viewModel.apply {
            loginResult.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is NotLoggedIn -> Unit
                    is Processing -> {
                        binding.webView.apply {
                            visibility = View.GONE
                            stopLoading()
                        }
                    }
                    is LoggedIn -> {
                        Toast.makeText(requireContext(), "Logged In", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    is Error -> {
                        Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
            accountNameState.observe(viewLifecycleOwner) { state ->
                with(binding) {
                    when (state) {
                        is Uninitiated -> {
                            userNameStatus.visibility = View.GONE
                            userNameStatusIcon.visibility = View.GONE
                        }
                        is Initiated -> {
                            userNameStatusIcon.apply {
                                visibility = View.VISIBLE
                                Glide.with(this@LoginFragment)
                                        .load(R.drawable.ic_account_circle_24dp)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(userNameStatusIcon)
                            }
                            userNameStatus.apply {
                                visibility = View.VISIBLE
                                text = context.getString(R.string.requesting_user_name)
                            }
                        }
                        is Finished -> {
                            userNameStatus.text = state.result.name
                            Glide.with(this@LoginFragment)
                                    .load(state.result.icon_img)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userNameStatusIcon)
                        }
                    }
                }
            }
            subscribedSubreddits.observe(viewLifecycleOwner) { state ->
                with(binding) {
                    when (state) {
                        Uninitiated -> {
                            subredditStatus.visibility = View.GONE
                            subredditStatusIcon.visibility = View.GONE
                        }
                        Initiated -> {
                            subredditStatusIcon.apply {
                                visibility = View.VISIBLE
                                Glide.with(this@LoginFragment)
                                        .load(R.drawable.ic_subreddit_default_24dp)
                                        .into(this)
                            }
                            subredditStatus.apply {
                                visibility = View.VISIBLE
                                text = context.getString(R.string.subreddit_syncing)
                            }
                        }
                        is Finished -> {
                            subredditStatus.text = String.format("Synced %d subscribed subreddits", state.result)
                        }
                    }
                }
            }
        }
    }
}