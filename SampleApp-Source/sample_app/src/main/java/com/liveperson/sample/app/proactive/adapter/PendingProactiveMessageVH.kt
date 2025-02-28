package com.liveperson.sample.app.proactive.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.liveperson.infra.model.PushMessage
import com.liveperson.sample.app.R

class PendingProactiveMessageVH(view: View) : ViewHolder(view) {

    private val messageTextView: TextView = view.findViewById(R.id.message);
    private var pushMessageIdTextView: TextView = view.findViewById(R.id.pushMessageId);
    private var notificationTypeTextView: TextView = view.findViewById(R.id.notificationType);
    private var setTappedButton: Button = view.findViewById(R.id.setTapped);

    fun bind(message: PushMessage, listener: OnPushMessageInteractListener?) {
        messageTextView.text = message.message
        pushMessageIdTextView.text = message.pushMessageId
        notificationTypeTextView.text = message.notificationType.name
        setTappedButton.setOnClickListener {
            listener?.onButtonClick(message, bindingAdapterPosition)
        }
    }

    fun recycle() {
        setTappedButton.setOnClickListener(null)
    }
}