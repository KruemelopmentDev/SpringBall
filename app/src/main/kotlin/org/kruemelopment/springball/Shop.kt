package org.kruemelopment.springball

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.constraintlayout.widget.ConstraintLayout

class Shop : Activity() {
    private val googlePlayGamesHandler: GPGHandler? = GPGHandler.instance
    private var playMusic = true
    private var mediaPlayer: MediaPlayer? = null
    private var wiggle: Animation? = null
    private var coins = 0
    private var coinsPreference: SharedPreferences? = null
    private var itemSharedPreference: SharedPreferences? = null
    private val ballsUnlocked = BooleanArray(6)
    private val backgroundsUnlocked = BooleanArray(6)
    private var parentLayout1: ConstraintLayout? = null
    private var parentLayout2: ConstraintLayout? = null
    private val returnIntent = Intent()
    private var coinTextView1: TextView? = null
    private var coinTextView2: TextView? = null
    private var myToast: MyToast? = null
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopneu)
        myToast = MyToast(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.decorView.setOnApplyWindowInsetsListener { _: View?, insets: WindowInsets ->
                if (insets.displayCutout != null) {
                    val a = insets.displayCutout!!.boundingRects[0].height()
                    if (a != 0) {
                        val textTopLayout1 = findViewById<ConstraintLayout>(R.id.layout1)
                        val textTopLayout2 = findViewById<ConstraintLayout>(R.id.layout5)
                        val params = textTopLayout1.layoutParams as ConstraintLayout.LayoutParams
                        params.setMargins(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        textTopLayout1.layoutParams = params
                        val params2 = textTopLayout2.layoutParams as ConstraintLayout.LayoutParams
                        params2.setMargins(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        textTopLayout2.layoutParams = params2
                    }
                }
                insets
            }
        }
        coinsPreference = getSharedPreferences("Coin", 0)
        itemSharedPreference = getSharedPreferences("JA", 0)
        wiggle = AnimationUtils.loadAnimation(this, R.anim.wackel)
        val switcher = findViewById<ViewSwitcher>(R.id.shop)
        parentLayout1 = findViewById(R.id.rl)
        parentLayout2 = findViewById(R.id.rl2)
        parentLayout1!!.setOnTouchListener(object : OnSwipeTouchListener(this@Shop) {
            override fun onSwipeLeft() {
                switcher.showPrevious()
            }
        })
        parentLayout2!!.setOnTouchListener(object : OnSwipeTouchListener(this@Shop) {
            override fun onSwipeRight() {
                switcher.showNext()
            }
        })
        loadMusic()
        if (playMusic) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hg1)
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.start()
        }
        loadAndSetBackground(parentLayout1, 0)
        loadAndSetBackground(parentLayout2, 0)
        loadShop()
        loadCoins()
        coinTextView1 = findViewById(R.id.textView6)
        coinTextView2 = findViewById(R.id.textView10)
        setTypeFacetoTextViews()
        coinTextView1!!.text = coins.toString()
        coinTextView2!!.text = coins.toString()
        val layout1 = findViewById<RelativeLayout>(R.id.click1)
        val layout2 = findViewById<RelativeLayout>(R.id.click2)
        val layout3 = findViewById<RelativeLayout>(R.id.click3)
        val layout4 = findViewById<RelativeLayout>(R.id.click4)
        val layout5 = findViewById<RelativeLayout>(R.id.click5)
        val layout6 = findViewById<RelativeLayout>(R.id.click6)
        val layout7 = findViewById<RelativeLayout>(R.id.click7)
        val layout8 = findViewById<RelativeLayout>(R.id.click8)
        val layout9 = findViewById<RelativeLayout>(R.id.click9)
        val layout10 = findViewById<RelativeLayout>(R.id.click10)
        val layout11 = findViewById<RelativeLayout>(R.id.click11)
        val layout12 = findViewById<RelativeLayout>(R.id.click12)
        val schlo1 = findViewById<ImageView>(R.id.imageView2)
        val schlo2 = findViewById<ImageView>(R.id.imageView3)
        val schlo3 = findViewById<ImageView>(R.id.imageView4)
        val schlo4 = findViewById<ImageView>(R.id.imageView5)
        val schlo5 = findViewById<ImageView>(R.id.imageView6)
        val schlo6 = findViewById<ImageView>(R.id.imageView7)
        val schlo7 = findViewById<ImageView>(R.id.imageView8)
        val schlo8 = findViewById<ImageView>(R.id.imageView9)
        val schlo9 = findViewById<ImageView>(R.id.imageView10)
        val schlo10 = findViewById<ImageView>(R.id.imageView11)
        val schlo11 = findViewById<ImageView>(R.id.imageView12)
        val schlo12 = findViewById<ImageView>(R.id.imageView13)
        schlo5.setImageResource(if (ballsUnlocked[0]) 0 else R.drawable.schloss)
        schlo1.setImageResource(if (ballsUnlocked[1]) 0 else R.drawable.schloss)
        schlo3.setImageResource(if (ballsUnlocked[2]) 0 else R.drawable.schloss)
        schlo2.setImageResource(if (ballsUnlocked[3]) 0 else R.drawable.schloss)
        schlo4.setImageResource(if (ballsUnlocked[4]) 0 else R.drawable.schloss)
        schlo6.setImageResource(if (ballsUnlocked[5]) 0 else R.drawable.schloss)
        schlo7.setImageResource(if (backgroundsUnlocked[0]) 0 else R.drawable.lock1)
        schlo12.setImageResource(if (backgroundsUnlocked[1]) 0 else R.drawable.lock1)
        schlo8.setImageResource(if (backgroundsUnlocked[2]) 0 else R.drawable.lock1)
        schlo10.setImageResource(if (backgroundsUnlocked[3]) 0 else R.drawable.lock1)
        schlo11.setImageResource(if (backgroundsUnlocked[4]) 0 else R.drawable.lock1)
        schlo9.setImageResource(if (backgroundsUnlocked[5]) 0 else R.drawable.lock1)
        val weiterButton = findViewById<ImageView>(R.id.imageButton10)
        val zurueckButton = findViewById<ImageView>(R.id.imageButton17)
        weiterButton.setOnClickListener { switcher.showNext() }
        zurueckButton.setOnClickListener { switcher.showPrevious() }
        setOnItemClickListener(layout5, 0, BALL1KEY, TYPEBALL, schlo5)
        setOnItemClickListener(layout1, 1, BALL2KEY, TYPEBALL, schlo1)
        setOnItemClickListener(layout3, 2, BALL3KEY, TYPEBALL, schlo3)
        setOnItemClickListener(layout2, 3, BALL4KEY, TYPEBALL, schlo2)
        setOnItemClickListener(layout4, 4, BALL5KEY, TYPEBALL, schlo4)
        setOnItemClickListener(layout6, 5, BALL6KEY, TYPEBALL, schlo6)
        setOnItemClickListener(layout7, 0, HINTERGRUND1KEY, TYPEBACKGROUND, schlo7)
        setOnItemClickListener(layout12, 1, HINTERGRUND2KEY, TYPEBACKGROUND, schlo12)
        setOnItemClickListener(layout8, 2, HINTERGRUND3KEY, TYPEBACKGROUND, schlo8)
        setOnItemClickListener(layout10, 3, HINTERGRUND4KEY, TYPEBACKGROUND, schlo10)
        setOnItemClickListener(layout11, 4, HINTERGRUND5KEY, TYPEBACKGROUND, schlo11)
        setOnItemClickListener(layout9, 5, HINTERGRUND6KEY, TYPEBACKGROUND, schlo9)
        initReturnIntent()
    }

    private fun setOnItemClickListener(
        layout: RelativeLayout,
        number: Int,
        id: String,
        type: Int,
        schloss: ImageView
    ) {
        layout.setOnClickListener {
            if (type == TYPEBACKGROUND) {
                if (backgroundsUnlocked[number]) {
                    var background = 0
                    when (number) {
                        0 -> background = R.drawable.hggame
                        1 -> background = R.drawable.hg3
                        2 -> background = R.drawable.hg5
                        3 -> background = R.drawable.hg2
                        4 -> background = R.drawable.hg4
                        5 -> background = R.drawable.zzz
                    }
                    setSelectedBackground(background, number + 1)
                    loadAndSetBackground(parentLayout1, background)
                    loadAndSetBackground(parentLayout2, background)
                    toastAuswahl()
                } else {
                    if (coins < 50) {
                        notEnoughMoneyToast(TYPEBACKGROUND)
                    } else {
                        backgroundsUnlocked[number] = true
                        openLock(schloss, type)
                        buy(id)
                    }
                }
            } else if (type == TYPEBALL) {
                if (ballsUnlocked[number]) {
                    when (number) {
                        0 -> setSelectedBall(R.drawable.fusball)
                        1 -> setSelectedBall(R.drawable.volleyball)
                        2 -> setSelectedBall(R.drawable.basketball)
                        3 -> setSelectedBall(R.drawable.ball1)
                        4 -> setSelectedBall(R.drawable.ball2)
                        5 -> setSelectedBall(R.drawable.ball3)
                    }
                    toastAuswahl()
                } else {
                    if (coins < 50) {
                        notEnoughMoneyToast(TYPEBALL)
                    } else {
                        ballsUnlocked[number] = true
                        openLock(schloss, type)
                        buy(id)
                    }
                }
            }
        }
    }

    private fun loadMusic() {
        val sp2 = getSharedPreferences("Sound", 0)
        playMusic = sp2.getInt("tb", 1) == 1
    }

    private fun loadCoins() {
        coins = coinsPreference!!.getInt("coin", 0)
    }

    private fun loadShop() {
        ballsUnlocked[0] = itemSharedPreference!!.getInt(BALL1KEY, 0) == 1
        ballsUnlocked[1] = itemSharedPreference!!.getInt(BALL2KEY, 0) == 1
        ballsUnlocked[2] = itemSharedPreference!!.getInt(BALL3KEY, 0) == 1
        ballsUnlocked[3] = itemSharedPreference!!.getInt(BALL4KEY, 0) == 1
        ballsUnlocked[4] = itemSharedPreference!!.getInt(BALL5KEY, 0) == 1
        ballsUnlocked[5] = itemSharedPreference!!.getInt(BALL6KEY, 0) == 1
        backgroundsUnlocked[0] = itemSharedPreference!!.getInt(HINTERGRUND1KEY, 0) == 1
        backgroundsUnlocked[1] = itemSharedPreference!!.getInt(HINTERGRUND2KEY, 0) == 1
        backgroundsUnlocked[2] = itemSharedPreference!!.getInt(HINTERGRUND3KEY, 0) == 1
        backgroundsUnlocked[3] = itemSharedPreference!!.getInt(HINTERGRUND4KEY, 0) == 1
        backgroundsUnlocked[4] = itemSharedPreference!!.getInt(HINTERGRUND5KEY, 0) == 1
        backgroundsUnlocked[5] = itemSharedPreference!!.getInt(HINTERGRUND6KEY, 0) == 1
    }

    private fun openLock(schloss: ImageView, typ: Int) {
        schloss.setImageResource(if (typ == TYPEBALL) R.drawable.schloss2 else R.drawable.lock2)
        schloss.startAnimation(wiggle)
        wiggle!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                schloss.visibility = View.INVISIBLE
                schloss.setBackgroundResource(android.R.color.transparent)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun buy(item: String) {
        myToast!!.showSuccess(getString(R.string.gekauft))
        coins -= 50
        val e = coinsPreference!!.edit()
        e.putInt("coin", coins)
        e.apply()
        coinTextView1!!.text = coins.toString()
        coinTextView2!!.text = coins.toString()
        returnIntent.putExtra("coins", coins)
        when (item) {
            HINTERGRUND1KEY, HINTERGRUND2KEY, HINTERGRUND3KEY, HINTERGRUND4KEY, HINTERGRUND5KEY, HINTERGRUND6KEY -> storePurchase(
                item,
                TYPEBACKGROUND
            )

            BALL1KEY, BALL2KEY, BALL3KEY, BALL4KEY, BALL5KEY, BALL6KEY -> storePurchase(
                item,
                TYPEBALL
            )
        }
    }

    private fun storePurchase(id: String, type: Int) {
        val edj = itemSharedPreference!!.edit()
        edj.putInt(id, 1)
        edj.apply()
        if (type == TYPEBALL) {
            if (googlePlayGamesHandler!!.isSignedIn) {
                when (id) {
                    BALL1KEY, BALL2KEY, BALL3KEY -> googlePlayGamesHandler.unlocksmallballs(this)
                    BALL4KEY, BALL5KEY, BALL6KEY -> googlePlayGamesHandler.unlockbigballs(this)
                }
            }
        } else if (type == TYPEBACKGROUND) if (googlePlayGamesHandler!!.isSignedIn) googlePlayGamesHandler.unlockbackgrounds(
            this
        )
    }

    private fun setTypeFacetoTextViews() {
        val typeface = Typeface.createFromAsset(this.assets, "fonts/Ba.ttf")
        val tv = findViewById<TextView>(R.id.textView2)
        val tv2 = findViewById<TextView>(R.id.textView9)
        tv.typeface = typeface
        tv2.typeface = typeface
        coinTextView1!!.typeface = typeface
        coinTextView2!!.typeface = typeface
    }

    private fun setSelectedBall(imageresource: Int) {
        returnIntent.putExtra("imageresource", imageresource)
        val ball1 = getSharedPreferences("Ball", 0)
        val e = ball1.edit()
        e.putInt("ball", imageresource)
        e.apply()
    }

    private fun setSelectedBackground(backgroundresource: Int, number: Int) {
        returnIntent.putExtra("backgroundresource", backgroundresource)
        val hg = getSharedPreferences("bh", 0)
        val e = hg.edit()
        e.putInt("Hintergrund", number)
        e.apply()
    }

    private fun notEnoughMoneyToast(type: Int) {
        myToast!!.showWarning(if (type == TYPEBALL) getString(R.string.kaufen) else getString(R.string.hg))
    }

    private fun toastAuswahl() {
        myToast!!.showInfo(getString(R.string.toast))
    }

    private fun initReturnIntent() {
        setResult(RESULT_OK, returnIntent)
    }

    private fun loadAndSetBackground(menue: ConstraintLayout?, resource: Int) {
        val dm = Resources.getSystem().displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        var backgroundresource=0
        if (resource == 0) {
            val hgs = getSharedPreferences("bh", 0)
            val backgroundImage = hgs.getInt("Hintergrund", 0)
            backgroundresource = when (backgroundImage) {
                2 -> R.drawable.hg3
                3 -> R.drawable.hg5
                4 -> R.drawable.hg2
                5 -> R.drawable.hg4
                6 -> R.drawable.zzz
                else -> R.drawable.hggame
            }
        }
        val bitmap = BitmapFactory.decodeResource(resources, backgroundresource)
        val hg = getResizedBitmap(bitmap, width, height)
        val hintergrundbild: Drawable = BitmapDrawable(resources, hg)
        menue!!.background = hintergrundbild
        menue.invalidate()
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
        if (playMusic) {
            mediaPlayer!!.start()
        }
    }

    companion object {
        private const val BALL1KEY = "fb"
        private const val BALL2KEY = "vb"
        private const val BALL3KEY = "bb"
        private const val BALL4KEY = "rb"
        private const val BALL5KEY = "blb"
        private const val BALL6KEY = "gb"
        private const val HINTERGRUND1KEY = "hg1"
        private const val HINTERGRUND2KEY = "hg2"
        private const val HINTERGRUND3KEY = "hg3"
        private const val HINTERGRUND4KEY = "hg4"
        private const val HINTERGRUND5KEY = "hg5"
        private const val HINTERGRUND6KEY = "hg6"
        private const val TYPEBALL = 0
        private const val TYPEBACKGROUND = 1
    }
}
