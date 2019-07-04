package ir.radical_app.radical.classes

import ir.radical_app.radical.R

class Const{

    companion object {
        const val CHANNEL_CODE:String = "radical_notifications"
        const val NOTIFICATION_ID = 159
        const val FCM_TOPIC = "radical_users"
        const val RADICAL_BROADCAST = "ir.radical_app.BROADCAST"
        const val RADICAL_BROADCAST_MESSAGE_EXTRA = "ir.radical_app.MESSAGE_BROADCAST"
        const val RADICAL_BROADCAST_SUPPORT_EXTRA = "ir.radical_app.Support_BROADCAST"
        val categories = listOf(R.mipmap.ic_loc1, R.mipmap.ic_loc2, R.mipmap.ic_loc3, R.mipmap.ic_loc4, R.mipmap.ic_loc5, R.mipmap.ic_loc6, R.mipmap.ic_loc7, R.mipmap.ic_loc8, R.mipmap.ic_loc9, R.mipmap.ic_loc10, R.mipmap.ic_loc11, R.mipmap.ic_loc12, R.mipmap.ic_loc13, R.mipmap.ic_loc14, R.mipmap.ic_loc15, R.mipmap.ic_loc16, R.mipmap.ic_loc17, R.mipmap.ic_loc18)


        var onboarding: Int = 0
        var category: String? = null
        var word: String? = null
    }



}