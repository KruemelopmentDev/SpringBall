package org.kruemelopment.springball

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class Settings : Activity(), OnCompletionListener {
    private var playMusic = false
    private var playSounds = false
    private var mediaPlayer: MediaPlayer? = null
    private var googlePlayGamesHandler: GPGHandler? = GPGHandler.instance
    private var erfolge: TextView? = null
    private var login: ImageView? = null
    private var connected: TextView? = null
    private var bestenlisten: TextView? = null
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
        val typeface = Typeface.createFromAsset(this.assets, "fonts/Ba.ttf")
        setContentView(R.layout.activity_settings)
        val option = findViewById<TextView>(R.id.textView7)
        option.typeface=typeface
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.decorView.setOnApplyWindowInsetsListener { _: View?, insets: WindowInsets ->
                if (insets.displayCutout != null) {
                    val a = insets.displayCutout!!.boundingRects[0].height()
                    if (a != 0) {
                        val params = option.layoutParams as RelativeLayout.LayoutParams
                        params.setMargins(
                            0,
                            a + (10 * Resources.getSystem().displayMetrics.density).toInt(),
                            0,
                            0
                        )
                        option.layoutParams = params
                    }
                }
                insets
            }
        }
        loadSettings()
        if (playMusic) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hg4)
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.start()
        }
        val settingsLayout = findViewById<RelativeLayout>(R.id.activity_settings)
        loadAndSetBackground(settingsLayout)
        loadAndSetupSwitches(typeface)
        setupAGB(typeface)
        setUpAndLoadSeekbars(typeface)
        setUpGooglePlayGames(typeface)
    }

    private fun loadSettings() {
        val sp2 = getSharedPreferences("Sound", 0)
        playMusic = sp2.getInt(MUSIC_KEY, 1) == 1
        playSounds = sp2.getInt(SOUNDS_KEY, 1) == 1
    }

    private fun saveSetting(key: String, value: Int) {
        val sp2 = getSharedPreferences("Sound", 0)
        val ed = sp2.edit()
        ed.putInt(key, value)
        ed.apply()
    }

    private fun setupAGB(typeface: Typeface) {
        val nutzungsbedingungenTextView = findViewById<TextView>(R.id.textView16)
        val datenschutzTextView = findViewById<TextView>(R.id.textView17)
        nutzungsbedingungenTextView.typeface = typeface
        datenschutzTextView.typeface = typeface
        nutzungsbedingungenTextView.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.kruemelopment-dev.de/nutzungsbedingungen")
            )
            startActivity(browserIntent)
        }
        datenschutzTextView.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.kruemelopment-dev.de/datenschutzerklaerung")
            )
            startActivity(browserIntent)
        }
        val contact = findViewById<TextView>(R.id.textView15)
        contact.typeface = typeface
        contact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:kontakt@kruemelopment-dev.de"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun loadAndSetupSwitches(typeface: Typeface) {
        val musicswitch = findViewById<SwitchCompat>(R.id.switch1)
        val soundswitch = findViewById<SwitchCompat>(R.id.switch2)
        musicswitch.typeface = typeface
        soundswitch.typeface = typeface
        musicswitch.isChecked = playMusic
        soundswitch.isChecked = playSounds
        musicswitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            musicswitch.isChecked = isChecked
            saveSetting(MUSIC_KEY, if (isChecked) 1 else 0)
            playMusic = isChecked
            if (isChecked) {
                mediaPlayer = MediaPlayer.create(this@Settings, R.raw.hg4)
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.start()
            } else {
                mediaPlayer!!.pause()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        }
        soundswitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            soundswitch.isChecked = isChecked
            saveSetting(SOUNDS_KEY, if (isChecked) 1 else 0)
            playSounds = isChecked
        }
    }

    private fun setUpAndLoadSeekbars(typeface: Typeface) {
        val sp2 = getSharedPreferences("Einstellungen", 0)
        val ones = sp2.getInt("one", 0)
        val twos = sp2.getInt("two", 60)
        val threes = sp2.getInt("three", 0)
        val one = findViewById<SeekBar>(R.id.seekBar2)
        val two = findViewById<SeekBar>(R.id.seekBar3)
        val three = findViewById<SeekBar>(R.id.seekBar4)
        val textView1 = findViewById<TextView>(R.id.textView14)
        val textView2 = findViewById<TextView>(R.id.textView18)
        val textView3 = findViewById<TextView>(R.id.textView19)
        textView1.typeface = typeface
        textView2.typeface = typeface
        textView3.typeface = typeface
        val tv1 = findViewById<TextView>(R.id.textView11)
        val tv2 = findViewById<TextView>(R.id.textView12)
        val tv3 = findViewById<TextView>(R.id.textView13)
        tv1.typeface = typeface
        tv2.typeface = typeface
        tv3.typeface = typeface
        one.progress = ones + 5
        two.progress = twos - 30
        three.progress = threes + 5
        textView1.text = ones.toString()
        textView2.text = twos.toString()
        textView3.text = threes.toString()
        if (one.progress == 0) {
            one.setPadding(20, 0, 20, 0)
        } else {
            one.setPadding(0, 0, 20, 0)
        }
        if (two.progress == 0) {
            two.setPadding(20, 0, 20, 0)
        } else {
            two.setPadding(0, 0, 20, 0)
        }
        if (three.progress == 0) {
            three.setPadding(20, 0, 20, 0)
        } else {
            three.setPadding(0, 0, 20, 0)
        }
        one.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (seekBar.progress == 0) {
                    one.setPadding(20, 0, 20, 0)
                } else {
                    one.setPadding(0, 0, 20, 0)
                }
                val ed = sp2.edit()
                ed.putInt("one", i - 5)
                ed.apply()
                textView1.text = (i - 5).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        two.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (seekBar.progress == 0) {
                    two.setPadding(20, 0, 20, 0)
                } else {
                    two.setPadding(0, 0, 20, 0)
                }
                val ed = sp2.edit()
                ed.putInt("two", i + 30)
                ed.apply()
                val help=i+30
                textView2.text = help.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        three.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (seekBar.progress == 0) {
                    three.setPadding(20, 0, 20, 0)
                } else {
                    three.setPadding(0, 0, 20, 0)
                }
                val ed = sp2.edit()
                ed.putInt("three", i - 5)
                ed.apply()
                textView3.text = (i - 5).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setUpGooglePlayGames(typeface: Typeface) {
        erfolge = findViewById(R.id.textView21)
        login = findViewById(R.id.imageView14)
        connected = findViewById(R.id.textView20)
        bestenlisten = findViewById(R.id.textView22)
        erfolge!!.typeface=typeface
        bestenlisten!!.typeface=typeface
        connected!!.typeface=typeface
        erfolge!!.setOnClickListener {
            if (googlePlayGamesHandler!!.isSignedIn) {
                googlePlayGamesHandler!!.onShowAchievementsRequested(this@Settings)
            } else {
                login!!.setImageResource(R.drawable.playr)
                connected!!.text=getString(R.string.notconnected)
                erfolge!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_red_light
                    )
                )
                bestenlisten!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_red_light
                    )
                )
            }
        }
        bestenlisten!!.setOnClickListener {
            if (googlePlayGamesHandler!!.isSignedIn) {
                val dialog = Dialog(this@Settings, R.style.AppDialog)
                dialog.setContentView(R.layout.auswahl_leaderboard)
                val coins = dialog.findViewById<TextView>(R.id.textView24)
                val score = dialog.findViewById<TextView>(R.id.textView23)
                score.typeface = typeface
                coins.typeface = typeface
                coins.setOnClickListener {
                    googlePlayGamesHandler!!.onShowLeaderboardsRequested(
                        this@Settings,
                        getString(R.string.leaderboard_coins)
                    )
                    dialog.dismiss()
                }
                score.setOnClickListener {
                    dialog.dismiss()
                    googlePlayGamesHandler!!.onShowLeaderboardsRequested(
                        this@Settings,
                        getString(R.string.leaderboard_highscore)
                    )
                }
                dialog.show()
            } else {
                login!!.setImageResource(R.drawable.playr)
                connected!!.text = getString(R.string.notconnected)
                erfolge!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_red_light
                    )
                )
                bestenlisten!!.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_red_light
                    )
                )
            }
        }
        if (googlePlayGamesHandler!!.isSignedIn) {
            login!!.setImageResource(R.drawable.playgr)
            connected!!.text = getString(R.string.connected)
            erfolge!!.setBackgroundColor(ContextCompat.getColor(this, R.color.hellblau))
            bestenlisten!!.setBackgroundColor(ContextCompat.getColor(this, R.color.hellblau))
        } else {
            login!!.setImageResource(R.drawable.playr)
            connected!!.text = getString(R.string.notconnected)
            erfolge!!.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            bestenlisten!!.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_red_light
                )
            )
        }
        login!!.setOnClickListener {
            if (!googlePlayGamesHandler!!.isSignedIn) {
                googlePlayGamesHandler!!.signIn(this)
            }
        }
    }

    private fun loadAndSetBackground(menue: RelativeLayout) {
        val dm = Resources.getSystem().displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val hgs = getSharedPreferences("bh", 0)
        val rsource: Int = when (hgs.getInt("Hintergrund", 0)) {
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
            if (playMusic) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
            finish()
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

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener(this)
    }

    companion object {
        var MUSIC_KEY = "tb"
        var SOUNDS_KEY = "tb2"
    }
}