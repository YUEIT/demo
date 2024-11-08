package cn.yue.test.login
import android.content.Intent
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import cn.yue.base.activity.BaseFragment
import cn.yue.base.utils.debug.LogUtils
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ThirdLoginHelper(private val fragment: BaseFragment) {

    private lateinit var googleSignInClient: SignInClient

    private val googleLoginLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        try {
            it.data?.let { intent ->
                val credential = googleSignInClient.getSignInCredentialFromIntent(intent)
                val idToken = credential.googleIdToken
                val googleId = credential.id
                googleLoginInFirebase(googleId, idToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is ApiException) {
                if (e.statusCode == Status.RESULT_CANCELED.statusCode) {
                    googleLoginFailureBlock?.invoke("")//R.string.login_cancel.getString())
                    return@registerForActivityResult
                }
                else if (e.statusCode == Status.RESULT_TIMEOUT.statusCode){
                    googleLoginFailureBlock?.invoke("")
                    return@registerForActivityResult
                }
            }
            googleLoginFailureBlock?.invoke(e.message ?: "")
        }
    }



    fun init() {
        googleSignInClient = Identity.getSignInClient(fragment.mActivity)
    }

    private var googleLoginSuccessBlock: ((id: String, idToken: String) -> Unit)? = null
    private var googleLoginFailureBlock: ((str: String) -> Unit)? = null

    private val key = "888823960533-ebcu5j0nmc0o5rnmtnavmqejp6ei522g.apps.googleusercontent.com"

    fun doLoginInGoogle(success: ((id: String, idToken: String) -> Unit),
                                 failure: ((str: String) -> Unit)) {
//        this.googleLoginSuccessBlock = success
//        this.googleLoginFailureBlock = failure
//        val request = GetSignInIntentRequest.builder()
//            .setServerClientId(key)
//            .build()
//        googleSignInClient.getSignInIntent(request)
//            .addOnSuccessListener {
//                try {
//                    val intentSenderRequest = IntentSenderRequest.Builder(it).build()
//                    googleLoginLauncher.launch(intentSenderRequest)
//                } catch (e: IntentSender.SendIntentException) {
//                    LogUtils.e("Google Sign-in failed ${e.message}")
//                    googleLoginFailureBlock?.invoke(e.message ?: "")
//                }
//            }
//            .addOnFailureListener {
//                LogUtils.e("Google Sign-in failed ${it.message}")
//                googleLoginFailureBlock?.invoke(it.message ?: "")
//            }
        doLoginInGoogle2(success, failure)
    }

    fun doLoginInGoogle2(success: ((id: String, idToken: String) -> Unit),
                        failure: ((str: String) -> Unit)) {
        this.googleLoginSuccessBlock = success
        this.googleLoginFailureBlock = failure
        val build = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(key)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()
        googleSignInClient.beginSignIn(build)
            .addOnSuccessListener {
                try {
                    val intentSenderRequest: IntentSenderRequest =
                        IntentSenderRequest.Builder(it.getPendingIntent().getIntentSender())
                            .build()
                    googleLoginLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    LogUtils.e("Google Sign-in failed ${e.message}")
                    googleLoginFailureBlock?.invoke(e.message ?: "")
                }
            }
            .addOnFailureListener {
                LogUtils.e("Google Sign-in failed ${it.message}")
                googleLoginFailureBlock?.invoke(it.message ?: "")
            }
    }

    private fun googleLoginInFirebase(id: String, idToken: String?) {
        try {
            if (idToken == null) {
                googleLoginFailureBlock?.invoke("")
            } else {
                googleLoginSuccessBlock?.invoke(id, idToken)
                // Got an ID token from Google. Use it to authenticate with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                Firebase.auth.signInWithCredential(firebaseCredential)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mCallbackManager = CallbackManager.Factory.create()

    private var facebookLoginSuccessBlock: ((id: String, idToken: String) -> Unit)? = null
    private var facebookLoginFailureBlock: ((str: String) -> Unit)? = null

    fun loginInFacebook(success: ((id: String, idToken: String) -> Unit),
                                 failure: ((str: String) -> Unit)) {
        this.facebookLoginSuccessBlock = success
        this.facebookLoginFailureBlock = failure
        LoginManager.getInstance().registerCallback(mCallbackManager, object :
            FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult) {
                LogUtils.d("fb login onSuccess: $result")
                facebookLoginInFirebase(result.accessToken.userId, result.accessToken.token)
            }

            override fun onCancel() {
                LogUtils.d( "fb login onCancel: ")
                facebookLoginFailureBlock?.invoke("cancel")
            }

            override fun onError(error: FacebookException) {
                LogUtils.d("fb login onError: ${error.message}")
                facebookLoginFailureBlock?.invoke(error.message ?: "login error")
            }
        })
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (!isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(fragment,
                mCallbackManager, listOf("public_profile"))
        }
    }

    private fun facebookLoginInFirebase(id: String, token: String) {
        try {
            LogUtils.d("handleFacebookAccessToken:$token")
            facebookLoginSuccessBlock?.invoke(id, token)
            val credential = FacebookAuthProvider.getCredential(token)
            Firebase.auth.signInWithCredential(credential)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}