package miips.com.Init

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import miips.com.Home.HomeActivity
import miips.com.LoginActivity.LoginActivity
import miips.com.R

class ChooseActivity : AppCompatActivity() {

    private var btnLogin: Button? = null
    private var btnVisitor: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        btnLogin = findViewById(R.id.login_btn)
        btnVisitor = findViewById(R.id.visitor_btn)

        btnLogin!!.setOnClickListener {
            val intent =
                    Intent(this@ChooseActivity, LoginActivity::class.java)

            // start your next activity
            startActivity(intent)
        }
        btnVisitor!!.setOnClickListener {
            val intent =
                    Intent(this@ChooseActivity, LocationVisitorActivity::class.java)

            // start your next activity
            startActivity(intent)
        }
    }
}