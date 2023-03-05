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
package com.hippo.text

import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import com.hippo.drawable.UnikeryDrawable
import com.hippo.ehviewer.EhApplication.Companion.conaco
import com.hippo.image.ImageBitmap
import com.hippo.widget.ObservedTextView

class URLImageGetter(
    private val mTextView: ObservedTextView
) : ImageGetter {
    override fun getDrawable(source: String): Drawable {
        return UnikeryDrawable(mTextView, conaco).apply { load(source) }
    }
}