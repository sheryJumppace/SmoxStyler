package com.ibcemobile.smoxstyler.recyclerdraghelper

interface OnDragStartEndListener {
    fun onDragStartListener(fromPosition: Int)
    fun onDragEndListener(toPosition: Int)
}