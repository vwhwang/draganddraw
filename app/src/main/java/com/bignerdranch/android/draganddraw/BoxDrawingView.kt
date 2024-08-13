package com.bignerdranch.android.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

private const val TAG = "BoxDrawingView"
private const val BOXES = "BOXES"
private const val VIEW_STATE = "VIEW_STATE"
class BoxDrawingView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var currentBox: Box? = null
    private var boxes = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelableArrayList(BOXES, ArrayList<Parcelable>(boxes))
        bundle.putParcelable(VIEW_STATE, state)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle){
            boxes = state.getParcelableArrayList<Box>(BOXES)?.toMutableList() ?: mutableListOf()
            super.onRestoreInstanceState(state.getParcelable(VIEW_STATE))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                currentBox = Box(current).also {
                    boxes.add(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current)
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                currentBox = null
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
        }
        Log.i(TAG, "$action at x = ${current.x} and y = ${current.y}")
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
        boxes.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            invalidate()
        }
    }
}