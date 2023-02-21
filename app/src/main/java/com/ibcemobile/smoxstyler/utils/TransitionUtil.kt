package com.ibcemobile.smoxstyler.utils

import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition

/**
 * Created by shineweb on 7/9/18.
 */
class TransitionUtil {

    companion object {
        private val duration: Long = 200

        fun slide(gravity: Int): Transition {
            val slideTransition = Slide(gravity)
            slideTransition.duration = duration
            return slideTransition
        }

        fun fade(mode: Int): Transition {
            val fadeTransition = Fade(mode)
            fadeTransition.duration = duration
            return fadeTransition
        }

        fun explode(): Transition {
            val enterTransition = Explode()
            enterTransition.duration = 500
            return enterTransition
        }

    }

}

abstract class FirstClass {

    abstract fun abstractFun()

    interface ClickListener {
        fun hitMe()
    }

    fun publicFunction() {
        println("I am public")
    }

    private fun privateFunction() {
        println("I am private")
    }

    protected fun protectedFunction() {
        println("I am protected")
    }

    private fun canAccessWithSameClass() {
        println("In Same Class we can access private and protected")
        privateFunction()
        protectedFunction()
        publicFunction()
    }

}

open class SecondClass : FirstClass(), FirstClass.ClickListener {


    private fun canAccessFromParentClass() {
        println("In derived Class i can access public and protected")
        protectedFunction()
        publicFunction()
    }

    override fun abstractFun() {
        println("I am abstract function")
    }

    override fun hitMe() {
        println("I am interface function")
    }
}

class ThirdClass : SecondClass() {

    fun getAllFunction() {
        hitMe()
        abstractFun()
        protectedFunction()
        publicFunction()
    }

}