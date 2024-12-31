package cn.yue.test.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.View
import android.widget.OverScroller
import androidx.core.view.ViewCompat

/**
 * Description:
 * Created by yue on 27/11/24
 */
class TestView(context: Context, attributeSet: AttributeSet? = null)
    : View(context, attributeSet) {

    private var scroller: OverScroller = OverScroller(context)

    private val mDetector = GestureDetector(context, object : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            scroller.forceFinished(true)
            ViewCompat.postInvalidateOnAnimation(this@TestView)
            return true
        }

        override fun onShowPress(e: MotionEvent) {
            super.onShowPress(e)
            Log.d("luo", "onShowPress: ")
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Log.d("luo", "onSingleTapUp: ")
            return super.onSingleTapUp(e)
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            Log.d("luo", "onLongPress: ")
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d("luo", "onFling: ")
            scroller.forceFinished(true)
            // Begins the animation.
            scroller.fling(
                // Current scroll position.
                x.toInt(),
                y.toInt(),
                velocityX.toInt(),
                velocityY.toInt(),
                /*
                 * Minimum and maximum scroll positions. The minimum scroll
                 * position is generally 0 and the maximum scroll position
                 * is generally the content size less the screen size. So if the
                 * content width is 1000 pixels and the screen width is 200
                 * pixels, the maximum scroll offset is 800 pixels.
                 */
                0, 500,
                0, 500,
                // The edges of the content. This comes into play when using
                // the EdgeEffect class to draw "glow" overlays.
                250,
                250
            )
            // Invalidates to trigger computeScroll().
            postInvalidateOnAnimation()
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Log.d("luo", "onSingleTapConfirmed: ")
            return super.onSingleTapConfirmed(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d("luo", "onDoubleTap: ")
            return super.onDoubleTap(e)
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            Log.d("luo", "onDoubleTapEvent: ")
            return super.onDoubleTapEvent(e)
        }

        override fun onContextClick(e: MotionEvent): Boolean {
            Log.d("luo", "onContextClick: ")
            return super.onContextClick(e)
        }
    })

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (mDetector.onTouchEvent(event)) {
//            return true
//        } else {
//            return super.onTouchEvent(event)
//        }
//    }

    // The "active pointer" is the one moving the object.
    private var mActivePointerId = INVALID_POINTER_ID
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mPosX = 0f
    private var mPosY = 0f

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events.
        mDetector.onTouchEvent(ev)

        val action = ev.actionMasked

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                ev.actionIndex.also { pointerIndex ->
                    // Remember where you start for dragging.
                    mLastTouchX = ev.getX(pointerIndex)
                    mLastTouchY = ev.getY(pointerIndex)
                }

                // Save the ID of this pointer for dragging.
                mActivePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position.
                val (x: Float, y: Float) =
                    ev.findPointerIndex(mActivePointerId).let { pointerIndex ->
                        // Calculate the distance moved.
                        ev.getX(pointerIndex) to ev.getY(pointerIndex)
                    }

                mPosX = x - mLastTouchX
                mPosY = y - mLastTouchY
                setX(getX() + mPosX)
                setY(getY() + mPosY)
                Log.d("luo", "onTouchEvent: $mPosX $mPosY")

                invalidate()

                // Remember this touch position for the next move event.
//                mLastTouchX = x
//                mLastTouchY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {

                ev.actionIndex.also { pointerIndex ->
                    ev.getPointerId(pointerIndex)
                        .takeIf { it == mActivePointerId }
                        ?.run {
                            // This is the active pointer going up. Choose a new
                            // active pointer and adjust it accordingly.
                            val newPointerIndex = if (pointerIndex == 0) 1 else 0
                            mLastTouchX = ev.getX(newPointerIndex)
                            mLastTouchY = ev.getY(newPointerIndex)
                            mActivePointerId = ev.getPointerId(newPointerIndex)
                        }
                }
            }
        }
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            val currX: Int = scroller.currX
            val currY: Int = scroller.currY
            Log.d("luo", "computeScroll: $currX $currY")
            x = currX.toFloat()
            y = currY.toFloat()
            invalidate()
        }
    }

    private val destPaint = Paint()
    private val srcPaint = Paint()
    private val path = Path()

    init {
        destPaint.color = Color.BLUE
        destPaint.style = Paint.Style.STROKE
        destPaint.isAntiAlias = true
        destPaint.strokeWidth = 10f
        srcPaint.color = Color.RED
        srcPaint.style = Paint.Style.STROKE
        srcPaint.isAntiAlias = true
        srcPaint.strokeWidth = 10f
        srcPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val layerId = canvas.saveLayer(
            0f, 0f,
            measuredWidth.toFloat(), measuredHeight.toFloat(),
            destPaint
        )

        path.addRoundRect(
        0f + 5, 0f + 5,
            measuredWidth.toFloat()  - 5,
            measuredHeight.toFloat() - 5,
        measuredHeight/ 2f, measuredHeight/ 2f, Path.Direction.CW)

        canvas.drawPath(path, destPaint)

        path.reset()
        path.moveTo(55f, 5f)
        path.lineTo(155f, 5f)
        canvas.drawPath(path, srcPaint)

        canvas.restoreToCount(layerId)
//        canvas.drawPath(path, paint)
//        paint.color = Color.RED
//        canvas.drawPath(path2, paint)
    }
}