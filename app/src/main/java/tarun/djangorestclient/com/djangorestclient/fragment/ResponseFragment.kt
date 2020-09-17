/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import tarun.djangorestclient.com.djangorestclient.R
import tarun.djangorestclient.com.djangorestclient.databinding.BottomSheetResponseInfoBinding
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentResponseBinding
import tarun.djangorestclient.com.djangorestclient.model.RestResponse
import tarun.djangorestclient.com.djangorestclient.utils.HttpUtil
import tarun.djangorestclient.com.djangorestclient.utils.MiscUtil

/**
 * This fragment shows user the response information received as a result of the REST request made by user.
 */
class ResponseFragment : Fragment() {
    companion object {
        const val TITLE = "Response"
        private val TAG = ResponseFragment::class.java.simpleName

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment ResponseFragment.
         */
        @JvmStatic
        fun newInstance(): ResponseFragment {
            return ResponseFragment()
        }
    }

    private lateinit var binding: FragmentResponseBinding
    private var restResponse: RestResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentResponseBinding.inflate(inflater, container, false)
        binding.fabCopyResponseBody.setOnClickListener { copyResponseBodyTextToClipboard() }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.response_fragment_menu, menu)
        val item = menu.findItem(R.id.action_show_extra_info)
        item.isVisible = restResponse != null
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_show_extra_info -> {
                showAdditionalResponseInfo(restResponse?.url, restResponse?.responseHeaders)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Update the response screen with the response info received.
     */
    fun updateUI(restResponse: RestResponse) {
        this.restResponse = restResponse
        requireActivity().invalidateOptionsMenu()
        binding.tvResponseCode.text = getString(R.string.response_code_label_with_value, restResponse.responseCode)
        binding.tvResponseTime.text = getString(R.string.response_time_ms_label_with_value, restResponse.responseTime)
        binding.tvResponseBody.text = HttpUtil.getFormattedJsonText(restResponse.responseBody)
    }

    /**
     * Show the additional response information inside a bottom sheet dialog.
     */
    private fun showAdditionalResponseInfo(requestUrl: String?, responseHeaders: CharSequence?) {
        val responseInfoBinding = BottomSheetResponseInfoBinding.inflate(layoutInflater)

        responseInfoBinding.tvRequestUrl.text = requestUrl
        responseInfoBinding.tvResponseHeaders.text = responseHeaders

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(responseInfoBinding.root)
        dialog.show()
    }

    /**
     * Copy the contents of response body to clipboard.
     */
    private fun copyResponseBodyTextToClipboard() {
        if (restResponse?.responseBody.orEmpty().isNotEmpty()) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.response_body_label), restResponse?.responseBody)
            clipboard.setPrimaryClip(clip)
            MiscUtil.displayShortToast(context, getString(R.string.fab_copy_success))
        } else {
            MiscUtil.displayShortToast(context, getString(R.string.fab_copy_empty))
        }
    }
}