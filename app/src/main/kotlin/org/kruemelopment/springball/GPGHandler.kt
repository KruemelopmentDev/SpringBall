package org.kruemelopment.springball

import android.app.Activity
import android.content.Intent
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.tasks.Task

class GPGHandler private constructor() {
    var isSignedIn = false
        private set

    fun init(activity: Activity?) {
        PlayGamesSdk.initialize(activity!!)
        checkSignIn(activity)
    }

    private fun checkSignIn(activity: Activity?) {
        val gamesSignInClient = PlayGames.getGamesSignInClient(activity!!)
        gamesSignInClient.isAuthenticated()
            .addOnCompleteListener { isAuthenticatedTask: Task<AuthenticationResult> ->
                isSignedIn =
                    isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated
            }
    }

    fun signIn(activity: Activity?) {
        val gamesSignInClient = PlayGames.getGamesSignInClient(activity!!)
        gamesSignInClient.signIn().addOnCompleteListener { task: Task<AuthenticationResult> ->
            isSignedIn = task.isSuccessful && task.result.isAuthenticated
        }
    }

    fun unlockscorer(score: Int, activity: Activity) {
        if (!isSignedIn) return
        val sese = activity.getSharedPreferences("Achievement", 0)
        var erfolglevel = sese.getInt("Erfolg", 0)
        if (erfolglevel == 3) return
        if (score >= 50 && erfolglevel == 0) {
            PlayGames.getAchievementsClient(activity)
                .unlock(activity.getString(R.string.achievement_scorer_1))
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_2), 1)
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_3), 1)
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_4), 1)
            erfolglevel++
        }
        if (score >= 100 && erfolglevel == 1) {
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_2), 1)
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_3), 1)
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_4), 1)
            erfolglevel++
        }
        if (score >= 150 && erfolglevel == 2) {
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_3), 1)
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_4), 1)
            erfolglevel++
        }
        if (score >= 200 && erfolglevel == 3) {
            PlayGames.getAchievementsClient(activity)
                .increment(activity.getString(R.string.achievement_scorer_4), 1)
            erfolglevel++
        }
        val e = sese.edit()
        e.putInt("Erfolg", erfolglevel)
        e.apply()
    }

    fun unlockbigballs(activity: Activity) {
        if (!isSignedIn) return
        PlayGames.getAchievementsClient(activity)
            .unlock(activity.getString(R.string.achievement_big_balls_1))
        PlayGames.getAchievementsClient(activity)
            .increment(activity.getString(R.string.achievement_big_balls_2), 1)
        PlayGames.getAchievementsClient(activity)
            .increment(activity.getString(R.string.achievement_big_balls_3), 1)
    }

    fun unlocksmallballs(activity: Activity) {
        if (!isSignedIn) return
        PlayGames.getAchievementsClient(activity)
            .unlock(activity.getString(R.string.achievement_small_balls_1))
        PlayGames.getAchievementsClient(activity)
            .increment(activity.getString(R.string.achievement_small_balls_2), 1)
        PlayGames.getAchievementsClient(activity)
            .increment(activity.getString(R.string.achievement_small_balls_3), 1)
    }

    fun unlockbackgrounds(activity: Activity) {
        if (!isSignedIn) return
        PlayGames.getAchievementsClient(activity)
            .unlock(activity.getString(R.string.achievement_backgrounder_1))
        PlayGames.getAchievementsClient(activity)
            .increment(activity.getString(R.string.achievement_backgrounder_2), 1)
        PlayGames.getAchievementsClient(activity)
            .increment(activity.getString(R.string.achievement_backgrounder_3), 1)
    }

    fun submithighscore(score: Int, activity: Activity) {
        if (!isSignedIn) return
        val sese = activity.getSharedPreferences("PlayGames", 0)
        val currentscore = sese.getInt("Score", 0)
        if (score > currentscore) {
            PlayGames.getLeaderboardsClient(activity)
                .submitScore(activity.getString(R.string.leaderboard_highscore), score.toLong())
            val ed = sese.edit()
            ed.putInt("Score", score)
            ed.apply()
        }
    }

    fun submitcoins(coins: Int, activity: Activity) {
        if (!isSignedIn) return
        val sese = activity.getSharedPreferences("PlayGames", 0)
        val currentcoins = sese.getInt("Coins", 0)
        if (coins > currentcoins) {
            PlayGames.getLeaderboardsClient(activity)
                .submitScore(activity.getString(R.string.leaderboard_coins), coins.toLong())
            val ed = sese.edit()
            ed.putInt("Coins", coins)
            ed.apply()
        }
    }

    fun onShowAchievementsRequested(activity: Activity) {
        if (!isSignedIn) return
        PlayGames.getAchievementsClient(activity).achievementsIntent
            .addOnSuccessListener { intent: Intent? ->
                activity.startActivityForResult(
                    intent,
                    4001
                )
            }
            .addOnFailureListener { }
    }

    fun onShowLeaderboardsRequested(activity: Activity, s: String?) {
        if (!isSignedIn) return
        PlayGames.getLeaderboardsClient(activity).getLeaderboardIntent(s!!)
            .addOnSuccessListener { intent: Intent? ->
                activity.startActivityForResult(
                    intent,
                    4001
                )
            }
            .addOnFailureListener { }
    }

    companion object {
        private var ourInstance: GPGHandler? = null
        val instance: GPGHandler?
            get() {
                if (ourInstance == null) {
                    ourInstance = GPGHandler()
                }
                return ourInstance
            }
    }
}
