
package im.tox.antox.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.ActionBarActivity
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.util.ArrayList
import java.util.Arrays
import im.tox.antox.R
import im.tox.antox.data.UserDB
import im.tox.antox.tox.ToxDoService
//remove if not needed
import scala.collection.JavaConversions._

class LoginActivity extends ActionBarActivity with AdapterView.OnItemSelectedListener {

  private var profileSelected: String = _

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    getSupportActionBar.hide()
    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN &&
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      getWindow.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    val db = new UserDB(this)
    if (!db.doUsersExist()) {
      db.close()
      val createAccount = new Intent(getApplicationContext, classOf[CreateAcccountActivity])
      startActivity(createAccount)
      finish()
    } else if (preferences.getBoolean("loggedin", false)) {
      db.close()
      val startTox = new Intent(getApplicationContext, classOf[ToxDoService])
      getApplicationContext.startService(startTox)
      val main = new Intent(getApplicationContext, classOf[MainActivity])
      startActivity(main)
      finish()
    } else {
      val profiles = db.getAllProfiles
      db.close()
      val profileSpinner = findViewById(R.id.login_account_name).asInstanceOf[Spinner]
      val adapter = new ArrayAdapter[String](this, android.R.layout.simple_spinner_dropdown_item, profiles)
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      profileSpinner.setAdapter(adapter)
      profileSpinner.setSelection(0)
      profileSpinner.setOnItemSelectedListener(this)
    }
  }

  def onItemSelected(parent: AdapterView[_],
    view: View,
    pos: Int,
    id: Long) {
    profileSelected = parent.getItemAtPosition(pos).toString
  }

  def onNothingSelected(parent: AdapterView[_]) {
  }

  def onClickLogin(view: View) {
    val account = profileSelected
    if (account == "") {
      val context = getApplicationContext
      val text = getString(R.string.login_must_fill_in)
      val duration = Toast.LENGTH_SHORT
      val toast = Toast.makeText(context, text, duration)
      toast.show()
    } else {
      val db = new UserDB(this)
      if (db.login(account)) {
        val details = db.getUserDetails(account)
        db.close()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean("loggedin", true)
        editor.putString("active_account", account)
        editor.putString("nickname", details(0))
        editor.putString("status", details(1))
        editor.putString("status_message", details(2))
        editor.apply()
        val startTox = new Intent(getApplicationContext, classOf[ToxDoService])
        getApplicationContext.startService(startTox)
        val main = new Intent(getApplicationContext, classOf[MainActivity])
        startActivity(main)
        finish()
      } else {
        val context = getApplicationContext
        val text = getString(R.string.login_bad_login)
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)
        toast.show()
      }
    }
  }

  def onClickCreateAccount(view: View) {
    val createAccount = new Intent(getApplicationContext, classOf[CreateAcccountActivity])
    startActivityForResult(createAccount, 1)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        finish()
      }
    }
  }
}
