/*
 * Copyright 2016 Hippo Seven
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
package com.hippo.ehviewer.client.data

import android.os.Parcelable
import com.hippo.util.isAtLeastQ
import com.hippo.widget.LoadImageView
import kotlinx.parcelize.Parcelize

@Parcelize
class GalleryPreview(
    var imageKey: String? = null,
    var imageUrl: String? = null,
    var pageUrl: String? = null,
    var position: Int = 0,
    var offsetX: Int = Int.MIN_VALUE,
    var offsetY: Int = Int.MIN_VALUE,
    var clipWidth: Int = Int.MIN_VALUE,
    var clipHeight: Int = Int.MIN_VALUE,
) : Parcelable {
    fun load(view: LoadImageView) {
        view.setClip(offsetX, offsetY, clipWidth, clipHeight)
        view.load(imageKey!!, imageUrl!!, offsetY == Int.MIN_VALUE || isAtLeastQ)
    }
}
