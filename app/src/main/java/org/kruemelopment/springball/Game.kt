package org.kruemelopment.springball

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class Game : Activity(), View.OnClickListener {
    private var googlePlayGamesHandler: GPGHandler? = GPGHandler.instance
    private var screenWidth = 0
    private var screenHeight = 0
    private var playMusic = true
    private var playSounds = true
    private var bgMusicPlayer: MediaPlayer? = null
    private var touchPlayer: MediaPlayer? = null
    private var endPlayer: MediaPlayer? = null
    private var finishPlayer: MediaPlayer? = null
    private var ballresource = 0
    private var moveUpwards = 0f
    private var fallVelocity = 0f
    private var up = 0f
    private var z = 2.0
    private var minus = 0
    private var points = 0.0
    private var countDownTime = 0
    private var extradown = 0
    private var yCoordinate = 0
    private var pointsproClick = 1.0
    private var random: Random? = null
    private var ball: Button? = null
    private var highScoreTextView: TextView? = null
    private var countDownTextView: TextView? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContentView(R.layout.activity_game)
        if (googlePlayGamesHandler == null) {
            googlePlayGamesHandler = GPGHandler.instance
            googlePlayGamesHandler!!.init(this)
        }
        val dm = Resources.getSystem().displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        up = screenHeight.toFloat() / 48000
        val sp2 = getSharedPreferences("Einstellungen", 0)
        val plusdown = sp2.getInt("three", 0).toFloat()
        fallVelocity = screenHeight.toFloat() / 480000 * (1 + plusdown / 10)
        moveUpwards = screenHeight.toFloat() / 16
        val relativeLayout = findViewById<RelativeLayout>(R.id.activity_game)
        loadBallResource()
        countDownTextView = findViewById(R.id.textView4)
        highScoreTextView = findViewById(R.id.textView3)
        val typeface = Typeface.createFromAsset(this.assets, "fonts/Ba.ttf")
        highScoreTextView!!.typeface=typeface
        highScoreTextView!!.setTextColor(Color.parseColor("#FFFFFF"))
        countDownTextView!!.typeface=typeface
        countDownTextView!!.setTextColor(Color.parseColor("#FFFFFF"))
        loadMusicSettings()
        if (playSounds) {
            touchPlayer = MediaPlayer.create(applicationContext, R.raw.nge)
            finishPlayer = MediaPlayer.create(this, R.raw.ende)
            endPlayer = MediaPlayer.create(this, R.raw.schluss)
        }
        if (playMusic) {
            bgMusicPlayer = MediaPlayer.create(this, R.raw.hg3)
            bgMusicPlayer!!.start()
            bgMusicPlayer!!.isLooping = true
        }
        loadBallResource()
        ball = findViewById(R.id.imageView)
        if (ballresource == 0) {
            ball!!.setBackgroundResource(R.drawable.fussball4)
        } else {
            ball!!.setBackgroundResource(ballresource)
        }
        ball!!.bringToFront()
        extradown = sp2.getInt("one", 0)
        val tabletSize = resources.getBoolean(R.bool.isTablet)
        minus = if (tabletSize) {
            75
        } else {
            100
        }
        ball!!.x = screenWidth / 2f - minus
        ball!!.y = screenHeight / 2f - minus + extradown * -50
        loadAndSetBackground(relativeLayout, screenWidth, screenHeight)
        random = Random()
        ball!!.setOnClickListener(this)
        COUNTDOWNTIME = sp2.getInt("two", 60)
        val faktor1 = (2 - COUNTDOWNTIME / 60f).toDouble()
        val faktor2 = (extradown * -1 / 10f + 1).toDouble()
        val faktor3 = (plusdown / 10 + 1).toDouble()
        pointsproClick = (faktor1 + faktor2 + faktor3) / 3
        newGame()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.decorView.setOnApplyWindowInsetsListener { v: View?, insets: WindowInsets ->
                if (insets.displayCutout != null) {
                    val a = insets.displayCutout!!.boundingRects[0].height()
                    if (a != 0) {
                        highScoreTextView!!.setPadding(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        countDownTextView!!.setPadding(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                    }
                }
                insets
            }
        }
    }

    private fun newGame() {
        points = 0.0
        ball!!.x = screenWidth / 2f - minus
        ball!!.y = screenHeight / 2f - minus + extradown * -50
        highScoreTextView!!.text = String.format(Locale.GERMANY, "%.2f", points)
        ball!!.setOnClickListener(this)
        initround()
    }

    private fun initround() {
        countDownTime = COUNTDOWNTIME
        countDownTextView!!.text = countDownTime.toString()
        z = 1.5
    }

    private fun ballFall() {
        if (ball!!.y > screenHeight + 200) {
            ball!!.y = screenHeight / 2f - minus + extradown * -50
            ball!!.x = screenWidth / 2f - minus
            z -= up
        }
        yCoordinate = ball!!.y.toInt()
        z += fallVelocity
        ball!!.y = (yCoordinate + z).toFloat()
        handler.postDelayed(gameLoopRunnable, 10)
    }

    private val handler = Handler()
    private val countdownRunnable: Runnable = object : Runnable {
        override fun run() {
            countDownTime -= 1
            countDownTextView!!.text = countDownTime.toString()
            handler.postDelayed(this, 1000)
        }
    }
    private val gameLoopRunnable = Runnable { ballFall() }
    private val gameEndCheckRunnable = Runnable { counter() }
    private fun counter() {
        if (countDownTime <= 0) {
            if (playSounds) {
                finishPlayer!!.start()
            }
            storeCoinsAndHighscore(points.toInt() / 10, points.toInt())
            handler.removeCallbacks(gameLoopRunnable)
            handler.removeCallbacks(gameEndCheckRunnable)
            handler.removeCallbacks(countdownRunnable)
            ball!!.setOnClickListener(null)
            Handler().postDelayed({
                val dialog = Dialog(this@Game, R.style.AppDialog)
                dialog.setContentView(R.layout.dialog)
                val reach = dialog.findViewById<TextView>(R.id.texthg)
                val showntext = getString(R.string.sco)
                val text = showntext + points.toInt()
                reach.text = text
                val go = dialog.findViewById<TextView>(R.id.bt2)
                val menue = dialog.findViewById<TextView>(R.id.bt)
                val sh = dialog.findViewById<Button>(R.id.button3)
                val tf = Typeface.createFromAsset(assets, "fonts/Ba.ttf")
                go.typeface = tf
                menue.typeface = tf
                sh.typeface = tf
                sh.setOnClickListener {
                    val mainView =
                        this@Game.window.decorView.findViewById<View>(android.R.id.content)
                    mainView.isDrawingCacheEnabled = true
                    val bitmap = mainView.drawingCache
                    if (dialog.window == null) return@setOnClickListener
                    val dialo = dialog.window!!.decorView.rootView
                    val loc = IntArray(2)
                    mainView.getLocationOnScreen(loc)
                    val loc2 = IntArray(2)
                    dialo.getLocationOnScreen(loc2)
                    dialo.isDrawingCacheEnabled = true
                    val bitmap1 = dialo.drawingCache
                    val canvas = Canvas(bitmap)
                    canvas.drawBitmap(
                        bitmap1,
                        (loc2[0] - loc[0]).toFloat(),
                        (loc2[1] - loc[1]).toFloat(),
                        Paint()
                    )
                    val c = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
                    val tit = sdf.format(c.time)
                    val imagepath = File(getExternalFilesDir(null), "$tit.png")
                    try {
                        imagepath.createNewFile()
                        val fil = FileOutputStream(imagepath)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fil)
                        fil.flush()
                        fil.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    shared = true
                    val uri = FileProvider.getUriForFile(
                        this@Game,
                        BuildConfig.APPLICATION_ID + ".provider",
                        imagepath
                    )
                    val sharingintent = Intent(Intent.ACTION_SEND)
                    sharingintent.setType("image/*")
                    sharingintent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(sharingintent, getString(R.string.ah)))
                }
                go.setOnClickListener { view: View? ->
                    dialog.dismiss()
                    newGame()
                    handler.post(gameLoopRunnable)
                    handler.post(gameEndCheckRunnable)
                    handler.post(countdownRunnable)
                    if (playMusic) {
                        bgMusicPlayer!!.start()
                    }
                }
                menue.setOnClickListener { view: View? ->
                    dialog.dismiss()
                    finish()
                }
                dialog.setOnCancelListener { dialogInterface: DialogInterface? ->
                    dialog.dismiss()
                    finish()
                }
                dialog.show()
            }, 500)
        } else {
            handler.postDelayed(gameEndCheckRunnable, 1000)
        }
        if (countDownTime == 5) {
            if (playSounds) {
                if (playMusic) {
                    bgMusicPlayer!!.pause()
                    bgMusicPlayer!!.seekTo(0)
                }
                endPlayer!!.start()
            }
        }
    }

    private fun loadBallResource() {
        val image = getSharedPreferences("Ball", 0)
        ballresource = image.getInt("ball", 0)
    }

    private fun loadMusicSettings() {
        val sp4 = getSharedPreferences("Sound", 0)
        playMusic = sp4.getInt("tb", 1) == 1
        playSounds = sp4.getInt("tb2", 1) == 1
    }

    private fun storeCoinsAndHighscore(coins: Int, highscore: Int) {
        val koi = getSharedPreferences("Coin", 0)
        val highPreference = getSharedPreferences("Achi", 0)
        val highcoins = highPreference.getInt("highcoins", 0)
        var currentCoins = koi.getInt("coin", 0)
        currentCoins += coins
        if (currentCoins > highcoins) {
            val e = highPreference.edit()
            e.putInt("highcoins", currentCoins)
            e.apply()
            val editor = koi.edit()
            editor.putInt("coin", currentCoins)
            editor.apply()
            if (googlePlayGamesHandler != null) if (googlePlayGamesHandler!!.isSignedIn) googlePlayGamesHandler!!.submitcoins(
                highcoins,
                this
            )
        }
        val spun = getSharedPreferences("Score", 0)
        val oldhighscore = spun.getInt("score", 0)
        if (highscore > oldhighscore) {
            val e = spun.edit()
            e.putInt("score", highscore)
            e.apply()
        }
        val returnIntent = Intent()
        returnIntent.putExtra("highscore", Math.max(highscore, oldhighscore))
        returnIntent.putExtra("coins", currentCoins)
        setResult(RESULT_OK, returnIntent)
        if (googlePlayGamesHandler != null) {
            if (googlePlayGamesHandler!!.isSignedIn) {
                googlePlayGamesHandler!!.unlockscorer(highscore, this)
                googlePlayGamesHandler!!.submithighscore(highscore, this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusicAndSounds()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            storeCoinsAndHighscore(points.toInt() / 10, points.toInt())
            stopMusicAndSounds()
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun stopMusicAndSounds() {
        try {
            if (playMusic) {
                if (bgMusicPlayer!!.isPlaying) {
                    bgMusicPlayer!!.stop()
                    bgMusicPlayer!!.release()
                    bgMusicPlayer = null
                }
            }
            if (playSounds) {
                if (endPlayer!!.isPlaying) {
                    endPlayer!!.stop()
                    endPlayer!!.release()
                    endPlayer = null
                }
                if (finishPlayer!!.isPlaying) {
                    finishPlayer!!.stop()
                    finishPlayer!!.release()
                    finishPlayer = null
                }
                if (touchPlayer!!.isPlaying) {
                    touchPlayer!!.stop()
                    touchPlayer!!.release()
                    touchPlayer = null
                }
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(gameEndCheckRunnable)
        handler.removeCallbacks(gameLoopRunnable)
        handler.removeCallbacks(countdownRunnable)
        try {
            if (playSounds) {
                touchPlayer!!.pause()
                endPlayer!!.pause()
                finishPlayer!!.pause()
            }
            if (playMusic) {
                bgMusicPlayer!!.pause()
            }
        } catch (ignored: Exception) {
        }
    }

    private var shared = false
    override fun onResume() {
        super.onResume()
        try {
            if (playMusic) {
                bgMusicPlayer!!.start()
            }
            if (!shared) {
                handler.post(gameEndCheckRunnable)
                handler.post(gameLoopRunnable)
                handler.post(countdownRunnable)
            } else shared = false
        } catch (ignored: Exception) {
        }
    }

    private fun loadAndSetBackground(menue: RelativeLayout, width: Int, height: Int) {
        val hgs = getSharedPreferences("bh", 0)
        val backgroundImage = hgs.getInt("Hintergrund", 0)
        val rsource: Int
        rsource = when (backgroundImage) {
            2 -> R.drawable.hg3
            3 -> R.drawable.hg5
            4 -> R.drawable.hg2
            5 -> R.drawable.hg4
            6 -> R.drawable.zzz
            else -> R.drawable.hggame
        }
        val bitmap = BitmapFactory.decodeResource(resources, rsource)
        val hg = getResizedBitmap(bitmap, width, height)
        val hintergrundbild: Drawable = BitmapDrawable(resources, hg)
        menue.background = hintergrundbild
        menue.invalidate()
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
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

    override fun onClick(v: View) {
        if (playSounds) {
            if (touchPlayer!!.isPlaying) {
                touchPlayer!!.pause()
                touchPlayer!!.seekTo(0)
            }
            touchPlayer!!.start()
        }
        points = points + pointsproClick
        yCoordinate = ball!!.y.toInt()
        ball!!.y = yCoordinate - moveUpwards
        ball!!.x = (random!!.nextInt(screenWidth - 200) + 100).toFloat()
        highScoreTextView!!.text = String.format(Locale.GERMANY, "%.2f", points)
    }

    companion object {
        private var COUNTDOWNTIME = 0
    }
}
