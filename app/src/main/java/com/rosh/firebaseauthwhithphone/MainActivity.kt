package com.rosh.firebaseauthwhithphone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btn_reg)
            .setOnClickListener {
                val singInIntent = googleSignInClient.signInIntent
                startActivityForResult(singInIntent, 1)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "onActivityResult: ${account.displayName}")
                fireBaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "onActivityResult: failure ${e.message}")
            }
        }
    }
    private fun fireBaseAuthWithGoogle(idToken:String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    //  Sign in success, update UI with the signed signed-in user's information
                    Log.d(TAG, "signInWithCredential:success ")
                    val user = auth.currentUser
                    //   updateUI(user)
                    Toast.makeText(this, "${user?.email}", Toast.LENGTH_SHORT).show()
                }else{
                    //if sign in fails, display a message tho the user.
                    Log.w(TAG, "signInWithCredential:failure ", task.exception)
                    //updateUI(null)
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

            }
    }
}