package cn.yue.base.utils.device

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import cn.yue.base.utils.Utils


object NetworkUtils {

    fun register() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkImpl24.register(Utils.getContext())
        } else {
            NetworkImplPre24.register(Utils.getContext())
        }
    }

    fun unregister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkImpl24.unregister(Utils.getContext())
        } else {
            NetworkImplPre24.unregister(Utils.getContext())
        }
    }

    fun addNetworkCallback(listener: (connect: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkImpl24.addNetworkCallback(listener)
        } else {
            NetworkImplPre24.addNetworkCallback(listener)
        }
    }

    fun removeNetworkCallback(listener: (connect: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkImpl24.removeNetworkCallback(listener)
        } else {
            NetworkImplPre24.removeNetworkCallback(listener)
        }
    }

    fun isAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkImpl24.isAvailable(Utils.getContext())
        } else {
            NetworkImplPre24.isAvailable(Utils.getContext())
        }
    }

    fun isWifi(): Boolean {
        val connectivityManager = Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val activeNetInfo = connectivityManager.activeNetworkInfo
            activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
        }

    }

    fun isMobile(): Boolean {
        val connectivityManager = Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val activeNetInfo = connectivityManager.activeNetworkInfo
            activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_MOBILE
        }
    }


    object NetworkImpl24 {

        private var listeners = ArrayList<((connect: Boolean) -> Unit)>()
        private var networkAvailable = false

        private val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkAvailable = true
                listeners.forEach {
                    it.invoke(networkAvailable)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkAvailable = false
                listeners.forEach {
                    it.invoke(networkAvailable)
                }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                networkAvailable = false
                listeners.forEach {
                    it.invoke(networkAvailable)
                }
            }
        }

        @SuppressLint("NewApi")
        fun register(context: Context) {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return
            manager.registerDefaultNetworkCallback(defaultNetworkCallback)
        }

        fun unregister(context: Context) {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return
            manager.unregisterNetworkCallback(defaultNetworkCallback)
        }

        fun isAvailable(context: Context): Boolean {
            return networkAvailable
        }

        fun addNetworkCallback(listener: (connect: Boolean) -> Unit) {
            listeners.add(listener)
        }

        fun removeNetworkCallback(listener: (connect: Boolean) -> Unit) {
            listeners.remove(listener)
        }
    }

    object NetworkImplPre24 {

        private var listeners = ArrayList<((connect: Boolean) -> Unit)>()
        private var networkAvailable = false
        private var isRegistered = false

        private val connectivityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val wasConnected: Boolean = networkAvailable
                networkAvailable = isAvailable(context)
                if (wasConnected != networkAvailable) {
                    listeners.forEach {
                        it.invoke(networkAvailable)
                    }
                }
            }
        }

        fun register(context: Context) {
            if (isRegistered) {
                return
            }
            networkAvailable = isAvailable(context)
            try {
                context.registerReceiver(
                    connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
                isRegistered = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        fun unregister(context: Context) {
            if (!isRegistered) {
                return
            }

            context.unregisterReceiver(connectivityReceiver)
            isRegistered = false
        }

        fun isAvailable(context: Context): Boolean {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
            val info = manager.activeNetworkInfo
            return null != info && info.isConnected && info.isAvailable
        }

        fun addNetworkCallback(listener: (connect: Boolean) -> Unit) {
            listeners.add(listener)
        }

        fun removeNetworkCallback(listener: (connect: Boolean) -> Unit) {
            listeners.remove(listener)
        }
    }
}