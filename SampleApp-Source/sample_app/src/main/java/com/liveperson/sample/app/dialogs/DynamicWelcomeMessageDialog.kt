package com.liveperson.sample.app.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.IdRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.liveperson.infra.configuration.Configuration
import com.liveperson.infra.model.LPWelcomeMessage
import com.liveperson.infra.model.LPWelcomeMessage.MessageFrequency
import com.liveperson.infra.model.MessageOption
import com.liveperson.messaging.sdk.api.LivePerson
import com.liveperson.sample.app.R


class DynamicWelcomeMessageDialog : BottomSheetDialogFragment() {

    companion object {

        private const val MAX_OPTIONS_QUANTITY = 24

        private const val KEY_BRAND_ID = "brand.id"
        private const val KEY_CONTENT = "wm.content"
        private const val KEY_FREQUENCY = "wm.content"
        private const val KEY_OPTIONS = "wm.options"

        @JvmStatic
        fun newInstance(brandId: String): DynamicWelcomeMessageDialog {
            val dialog = DynamicWelcomeMessageDialog()
            dialog.arguments = Bundle().apply { putString(KEY_BRAND_ID, brandId) }
            return dialog
        }
    }

    private lateinit var welcomeMessageContentEditText: EditText
    private lateinit var quickRepliesChipLayout: ChipGroup
    private lateinit var addQuickReplyOptionButton: ImageButton
    private lateinit var quickReplyOptionEditText: EditText
    private lateinit var timeoutEditText: EditText
    private lateinit var welcomeMessageFrequencyGroup: RadioGroup
    private lateinit var applyWelcomeMessageButton: Button

    private lateinit var timeoutTextWatcher: TextWatcher
    private lateinit var welcomeMessageTextWatcher: TextWatcher

    private var brandId: String? = null

    private var welcomeMessage: String = ""
    private val welcomeMessageOptions: MutableList<MessageOption> = mutableListOf()
    private var welcomeMessageFrequency: MessageFrequency = MessageFrequency.FIRST_TIME_CONVERSATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val brandId = arguments?.getString(KEY_BRAND_ID) ?: return
        val lpWelcomeMessage: LPWelcomeMessage? =
            LivePerson.getWelcomeMessage(requireContext(), brandId)
        val frequency = savedInstanceState?.getInt(
            KEY_FREQUENCY,
            MessageFrequency.FIRST_TIME_CONVERSATION.ordinal
        )
            ?: lpWelcomeMessage?.messageFrequency?.ordinal
            ?: 0
        val welcomeMessage = savedInstanceState?.getString(KEY_CONTENT)
            ?: lpWelcomeMessage?.welcomeMessage
            ?: ""
        val options: List<MessageOption> = arguments?.getOptionList(KEY_OPTIONS)
            ?.takeIf { it.isNotEmpty() }
            ?: lpWelcomeMessage?.messageOptions
            ?: emptyList()

        this.brandId = brandId
        this.welcomeMessage = welcomeMessage
        this.welcomeMessageOptions.addAll(options)
        this.welcomeMessageFrequency = MessageFrequency.fromOrdinal(frequency)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dynamic_welcome_message, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener { dialogInterface ->
            val dialog: BottomSheetDialog = dialogInterface as? BottomSheetDialog ?: return@setOnShowListener
            val rootView: View? = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            rootView?.let { BottomSheetBehavior.from(it).setState(BottomSheetBehavior.STATE_EXPANDED); }
        }
        return dialog;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // region views initialization
        welcomeMessageContentEditText = view.findViewById(R.id.welcome_message_content_edit_text)
        quickRepliesChipLayout = view.findViewById(R.id.quick_replies_chip_group)
        quickReplyOptionEditText = view.findViewById(R.id.quick_reply_option_edit_text)
        addQuickReplyOptionButton = view.findViewById(R.id.add_qr_option_button)
        timeoutEditText = view.findViewById(R.id.timeout_edit_text)
        welcomeMessageFrequencyGroup = view.findViewById(R.id.wm_frequency_group)
        applyWelcomeMessageButton = view.findViewById(R.id.apply_changes_button)
        // endregion views initialization

        // region data apply
        quickRepliesChipLayout.showMessageOptions(welcomeMessageOptions)
        welcomeMessageFrequencyGroup.setFrequency(welcomeMessageFrequency)
        welcomeMessageContentEditText.setText(welcomeMessage)

        timeoutEditText.setText("" + Configuration.getInteger(R.integer.lp_welcome_message_delay_in_seconds))
        // endregion data apply

        // region actions setup
        addQuickReplyOptionButton.setOnClickListener {
            addChip()
        }
        applyWelcomeMessageButton.setOnClickListener {
            applyChanges()
        }

        welcomeMessageTextWatcher = welcomeMessageContentEditText.addAfterTextChangeListener {
            welcomeMessage = it
        }

        timeoutTextWatcher = timeoutEditText.addAfterTextChangeListener {
            Configuration.set(R.integer.lp_welcome_message_delay_in_seconds, it.toIntOrNull() ?: 0)
        }

        welcomeMessageFrequencyGroup.setOnCheckedChangeListener { group, checkedId ->
            onFrequencyChanged(checkedId)
        }
        // endregion actions setup
    }

    override fun onDismiss(dialog: DialogInterface) {
        val currentDialog: BottomSheetDialog? = dialog as? BottomSheetDialog
        currentDialog?.setOnShowListener(null)
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        timeoutEditText.removeTextChangedListener(timeoutTextWatcher)
        welcomeMessageContentEditText.removeTextChangedListener(welcomeMessageTextWatcher)
        addQuickReplyOptionButton.setOnClickListener(null)
        applyWelcomeMessageButton.setOnClickListener(null)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CONTENT, welcomeMessage)
        outState.putInt(KEY_FREQUENCY, welcomeMessageFrequency.ordinal)
        outState.putOptionList(KEY_OPTIONS, welcomeMessageOptions)
    }

    private fun ChipGroup.showMessageOptions(list: List<MessageOption>) {
        repeat(childCount) { _ ->
            removeViewAt(0)
        }
        list.forEach {
            addView(chipOf(it))
        }
        if (list.isEmpty()) {
            quickRepliesChipLayout.visibility = View.GONE
        } else {
            quickRepliesChipLayout.visibility = View.VISIBLE
        }
    }

    private fun optionOf(text: String) = MessageOption(text, text)
    private fun chipOf(option: MessageOption): Chip = Chip(ContextThemeWrapper(requireContext(), R.style.Theme_MyTheme_Chip)).apply {
        setOnLongClickListener {
            quickRepliesChipLayout.removeView(this)
            welcomeMessageOptions.remove(option)
            if (welcomeMessageOptions.isEmpty()) {
                quickRepliesChipLayout.visibility = View.GONE
            }
            return@setOnLongClickListener true
        }
        text = option.displayText
    }

    private fun onFrequencyChanged(@IdRes id: Int) {
        welcomeMessageFrequency = when (id) {
            R.id.first_conversation_radio_button -> MessageFrequency.FIRST_TIME_CONVERSATION
            else -> MessageFrequency.EVERY_CONVERSATION
        }
    }

    private fun addChip() {
        val text = quickReplyOptionEditText.text.toString()
            .takeUnless { it.isNullOrBlank() } ?: return
        if (welcomeMessageOptions.size == MAX_OPTIONS_QUANTITY) {
            Toast.makeText(requireContext(), R.string.toast_message_max_options_quanity, Toast.LENGTH_SHORT).show()
            return
        }

        val option = optionOf(text)
        welcomeMessageOptions.add(option)
        quickRepliesChipLayout.visibility = View.VISIBLE
        quickRepliesChipLayout.addView(chipOf(option))
        quickReplyOptionEditText.setText("")
    }

    private fun applyChanges() {
        if (welcomeMessageOptions.size > MAX_OPTIONS_QUANTITY) {
            Toast.makeText(requireContext(), R.string.toast_message_max_options_quanity, Toast.LENGTH_SHORT).show()
        } else {
            val welcomeMessage = LPWelcomeMessage(welcomeMessage)
            welcomeMessage.messageFrequency = welcomeMessageFrequency
            welcomeMessage.messageOptions = welcomeMessageOptions
            brandId?.let { brand -> LivePerson.setWelcomeMessage(requireContext(), brand, welcomeMessage) }
        }
        dismiss()
    }

    private fun Bundle.putOptionList(key: String, list: List<MessageOption>) {
        putParcelableArrayList(key, ArrayList(list))
    }

    private fun Bundle.getOptionList(key: String): List<MessageOption> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayList(key, MessageOption::class.java) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun RadioGroup.setFrequency(frequency: MessageFrequency) {
        val id = when (frequency) {
            MessageFrequency.FIRST_TIME_CONVERSATION -> R.id.first_conversation_radio_button
            else -> R.id.every_conversation_radio_button
        }
        check(id)
    }

    private inline fun EditText.addAfterTextChangeListener(crossinline block: (String) -> Unit): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let(block)
            }
        }
        return textWatcher.also { addTextChangedListener(it) }
    }
}