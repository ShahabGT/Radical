package ir.radical_app.radical.classes

class Const{

    companion object {
        const val CHANNEL_CODE:String = "radical_notifications"
        const val NOTIFICATION_ID = 159
        const val FCM_TOPIC = "radical_users"
        const val RADICAL_BROADCAST = "ir.radical_app.BROADCAST"
        const val RADICAL_BROADCAST_MESSAGE_EXTRA = "ir.radical_app.MESSAGE_BROADCAST"
        const val RADICAL_BROADCAST_SUPPORT_EXTRA = "ir.radical_app.Support_BROADCAST"

        var onboarding: Int = 0
        var category: String? = null
        var word: String? = null
    }



}