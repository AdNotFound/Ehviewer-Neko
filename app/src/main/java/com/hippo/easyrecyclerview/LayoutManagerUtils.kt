/*
 * Copyright 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hippo.easyrecyclerview

import android.content.Context
import android.graphics.PointF
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hippo.yorozuya.MathUtils
import com.hippo.yorozuya.SimpleHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.math.abs

object LayoutManagerUtils {
    private var sCsdfp: Method? = null

    init {
        try {
            sCsdfp = StaggeredGridLayoutManager::class.java.getDeclaredMethod(
                "calculateScrollDirectionForPosition",
                Int::class.javaPrimitiveType,
            )
            sCsdfp!!.isAccessible = true
        } catch (e: NoSuchMethodException) {
            // Ignore
            e.printStackTrace()
        }
    }

    fun scrollToPositionWithOffset(
        layoutManager: RecyclerView.LayoutManager,
        position: Int,
        offset: Int,
    ) {
        when (layoutManager) {
            is LinearLayoutManager -> {
                layoutManager.scrollToPositionWithOffset(position, offset)
            }

            is StaggeredGridLayoutManager -> {
                layoutManager.scrollToPositionWithOffset(position, offset)
            }

            else -> {
                throw IllegalStateException(
                    "Can't do scrollToPositionWithOffset for " +
                        layoutManager.javaClass.getName(),
                )
            }
        }
    }

    fun smoothScrollToPosition(
        layoutManager: RecyclerView.LayoutManager,
        context: Context?,
        position: Int,
        millisecondsPerInch: Int = -1,
    ) {
        val smoothScroller: SimpleSmoothScroller
        when (layoutManager) {
            is LinearLayoutManager -> {
                smoothScroller =
                    object : SimpleSmoothScroller(context!!, millisecondsPerInch.toFloat()) {
                        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? = layoutManager.computeScrollVectorForPosition(targetPosition)
                    }
            }

            is StaggeredGridLayoutManager -> {
                smoothScroller =
                    object : SimpleSmoothScroller(context!!, millisecondsPerInch.toFloat()) {
                        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                            var direction = 0
                            try {
                                direction = sCsdfp!!.invoke(layoutManager, targetPosition) as Int
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            } catch (e: InvocationTargetException) {
                                e.printStackTrace()
                            }
                            if (direction == 0) {
                                return null
                            }
                            return if (layoutManager.orientation == StaggeredGridLayoutManager.HORIZONTAL) {
                                PointF(direction.toFloat(), 0f)
                            } else {
                                PointF(0f, direction.toFloat())
                            }
                        }
                    }
            }

            else -> {
                throw IllegalStateException(
                    "Can't do smoothScrollToPosition for " +
                        layoutManager.javaClass.getName(),
                )
            }
        }
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }

    fun scrollToPositionProperly(
        layoutManager: RecyclerView.LayoutManager,
        context: Context?,
        position: Int,
        listener: OnScrollToPositionListener?,
    ) {
        SimpleHandler.getInstance().postDelayed({
            val first = getFirstVisibleItemPosition(layoutManager)
            val last = getLastVisibleItemPosition(layoutManager)
            val offset = abs((position - first).toDouble()).toInt()
            val max = last - first
            if (offset < max && max > 0) {
                smoothScrollToPosition(
                    layoutManager,
                    context,
                    position,
                    MathUtils.lerp(100, 25, (offset / max).toFloat()),
                )
            } else {
                scrollToPositionWithOffset(layoutManager, position, 0)
                listener?.onScrollToPosition(position)
            }
        }, 200)
    }

    fun getFirstVisibleItemPosition(layoutManager: RecyclerView.LayoutManager): Int = when (layoutManager) {
        is LinearLayoutManager -> {
            layoutManager.findFirstVisibleItemPosition()
        }

        is StaggeredGridLayoutManager -> {
            val positions =
                layoutManager.findFirstVisibleItemPositions(null)
            MathUtils.min(*positions)
        }

        else -> {
            throw IllegalStateException(
                "Can't do getFirstVisibleItemPosition for " +
                    layoutManager.javaClass.getName(),
            )
        }
    }

    fun getLastVisibleItemPosition(layoutManager: RecyclerView.LayoutManager): Int = when (layoutManager) {
        is LinearLayoutManager -> {
            layoutManager.findLastVisibleItemPosition()
        }

        is StaggeredGridLayoutManager -> {
            val positions =
                layoutManager.findLastVisibleItemPositions(null)
            MathUtils.max(*positions)
        }

        else -> {
            throw IllegalStateException(
                "Can't do getLastVisibleItemPosition for " +
                    layoutManager.javaClass.getName(),
            )
        }
    }

    fun interface OnScrollToPositionListener {
        fun onScrollToPosition(position: Int)
    }
}
