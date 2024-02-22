package org.kruemelopment.springball

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout

class Menu : Activity() {
    private var web = false
    private var highscoreTextView: TextView? = null
    private var coinsTextView: TextView? = null
    private var highScoreSP: SharedPreferences? = null
    private var coinsSP: SharedPreferences? = null
    private var googlePlayGamesHandler: GPGHandler? = GPGHandler.instance
    private var start = false
    private var playMusic = true
    private var mediaPlayer: MediaPlayer? = null
    private var wiggle: Animation? = null
    private var startImage: AppCompatImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setContentView(R.layout.activity_menue)
        val menue = findViewById<ConstraintLayout>(R.id.activity_menue)
        val dm = Resources.getSystem().displayMetrics
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels
        loadAndSetBackground(menue, screenWidth, screenHeight)
        checkAGBConsens()
    }

    private fun checkAGBConsens() {
        val sese = getSharedPreferences("Start", 0)
        web = sese.getBoolean("agbs", false)
        if (!web) {
            val dialog = Dialog(this, R.style.AppDialog)
            dialog.setContentView(R.layout.webdialog)
            val ja = dialog.findViewById<TextView>(R.id.textView5)
            val nein = dialog.findViewById<TextView>(R.id.textView8)
            ja.setOnClickListener {
                val ed = sese.edit()
                ed.putBoolean("agbs", true)
                ed.apply()
                web = true
                dialog.dismiss()
                start()
            }
            nein.setOnClickListener { finishAndRemoveTask() }
            val textView = dialog.findViewById<TextView>(R.id.textView4)
            textView.text = Html.fromHtml(
                "Mit der Nutzung dieser App aktzeptiere ich die " +
                        "<a href=\"https://www.kruemelopment-dev.de/datenschutzerklärung\">Datenschutzerklärung</a>" + " und die " + "<a href=\"https://www.kruemelopment-dev.de/nutzungsbedingungen\">Nutzungsbedingungen</a>" + " von Krümelopment Dev",
                Html.FROM_HTML_MODE_LEGACY
            )
            textView.movementMethod = LinkMovementMethod.getInstance()
            dialog.setCancelable(false)
            dialog.show()
        } else start()
    }

    private fun start() {
        val typeface = Typeface.createFromAsset(assets, "fonts/Ba.ttf")
        val shop = findViewById<AppCompatImageView>(R.id.imageButton)
        coinsTextView = findViewById(R.id.textView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.decorView.setOnApplyWindowInsetsListener { _: View?, insets: WindowInsets ->
                if (insets.displayCutout != null) {
                    val a = insets.displayCutout!!.boundingRects[0].height()
                    if (a != 0) {
                        val params =
                            coinsTextView!!.layoutParams as ConstraintLayout.LayoutParams
                        params.setMargins(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        coinsTextView!!.layoutParams = params
                        val params2 = shop.layoutParams as ConstraintLayout.LayoutParams
                        params2.setMargins(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        shop.layoutParams = params2
                        val params3 =
                            highscoreTextView!!.layoutParams as ConstraintLayout.LayoutParams
                        params3.setMargins(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        highscoreTextView!!.layoutParams = params3
                    }
                }
                insets
            }
        }
        if (googlePlayGamesHandler == null) googlePlayGamesHandler =
            GPGHandler.instance
        googlePlayGamesHandler!!.init(this)
        loadMusic()
        if (playMusic) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hg2)
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.start()
        }
        startImage = findViewById(R.id.imageButton2)
        initwiggleAnimation()
        val image = getSharedPreferences("Ball", 0)
        val iamgeid = image.getInt("ball", 0)
        if (iamgeid == 0) {
            startImage!!.setImageResource(R.drawable.fussball4)
        } else {
            startImage!!.setImageResource(iamgeid)
        }
        shop.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@Menu,
                    Shop::class.java
                ), 102
            )
        }
        val startButton = findViewById<Button>(R.id.button)
        startButton.typeface = typeface
        startButton.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@Menu,
                    Game::class.java
                ), 101
            )
        }
        startImage!!.setOnClickListener {
            startActivityForResult(
                Intent(this@Menu, Game::class.java),
                101
            )
        }
        val settings = findViewById<AppCompatImageView>(R.id.imageButton9)
        settings.setOnClickListener {
            startActivity(
                Intent(
                    this@Menu,
                    Settings::class.java
                )
            )
        }
        highscoreTextView = findViewById(R.id.textView5)
        highscoreTextView!!.typeface=typeface
        highScoreSP = getSharedPreferences("Score", 0)
        loadAndSetHighscore()
        coinsTextView!!.typeface=typeface
        coinsSP = getSharedPreferences("Coin", 0)
        loadAndSetCoins()
        if (intent.dataString != null && intent.dataString == "startGameDirectly") {
            startActivityForResult(Intent(this@Menu, Game::class.java), 101)
            intent.setData(null)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            if (playMusic) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loadAndSetBackground(menue: ConstraintLayout, width: Int, height: Int) {
        val resource: Int
        val hgs = getSharedPreferences("bh", 0)
        val backgroundImage = hgs.getInt("Hintergrund", 0)
        resource = when (backgroundImage) {
            2 -> R.drawable.hg3
            3 -> R.drawable.hg5
            4 -> R.drawable.hg2
            5 -> R.drawable.hg4
            6 -> R.drawable.zzz
            else -> R.drawable.hggame
        }
        val bitmap = BitmapFactory.decodeResource(resources, resource)
        val hg = getResizedBitmap(bitmap, width, height)
        val hintergrundbild: Drawable = BitmapDrawable(resources, hg)
        menue.background = hintergrundbild
        menue.invalidate()
    }

    private fun loadAndSetHighscore() {
        val highScore = highScoreSP!!.getInt("score", 0)
        highscoreTextView!!.text = highScore.toString()
    }

    private fun loadAndSetCoins() {
        val coins = coinsSP!!.getInt("coin", 0)
        coinsTextView!!.text = coins.toString()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            } else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val startAnimationRunnable = Runnable { startImage!!.startAnimation(wiggle) }
    private fun initwiggleAnimation() {
        wiggle = AnimationUtils.loadAnimation(this, R.anim.rotate)
        wiggle!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                handler.postDelayed(startAnimationRunnable, 2000)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        handler.postDelayed(startAnimationRunnable, 2000)
    }

    private fun loadMusic() {
        val sp2 = getSharedPreferences("Sound", 0)
        playMusic = sp2.getInt("tb", 1) == 1
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (playMusic) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (playMusic) {
                mediaPlayer!!.pause()
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (!start) {
            start = true
            return
        }
        if (!web) return
        loadMusic()
        if (playMusic) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.hg2)
                mediaPlayer!!.isLooping = true
            }
            mediaPlayer!!.start()
        } else {
            try {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            } catch (ignored: Exception) {
            }
        }
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }
}
