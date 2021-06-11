package vivant.me.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.caressa.security.ui.SecurityActivity
import com.caressa.security.ui.SplashScreenActivity
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, SplashScreenActivity::class.java))
        finish()
    }

}
