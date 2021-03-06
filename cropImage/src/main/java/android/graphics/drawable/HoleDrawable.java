/*
 * Copyright (c) 2017. The ReadyShowShow@gmail Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 说明：支持中间出现透明区域的drawable <br/>
 * 通过{@link #setSrcPath(Path)}设定透明区域的形状 <br/>
 * 作者：杨健
 * 时间：2017/9/4.
 */

public class HoleDrawable extends Drawable {
    private Paint srcPaint;
    private Path srcPath = new Path();
    private RectF srcRect = new RectF(100, 100, 300, 200);

    private Drawable innerDrawable;


    public HoleDrawable(Drawable innerDrawable) {
        this.innerDrawable = innerDrawable;
        srcPath.addRect(100, 100, 200, 200, Path.Direction.CW);
        srcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        srcPaint.setColor(0xff000000);
    }

    /**
     * 设置内部透明的部分
     *
     * @param srcPath
     */
    public void setSrcPath(Path srcPath) {
        this.srcPath = srcPath;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        innerDrawable.setBounds(getBounds());
        if (srcPath == null || srcPath.isEmpty()) {
            innerDrawable.draw(canvas);
        } else {
            //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
            int saveCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), srcPaint, Canvas.ALL_SAVE_FLAG);

            //dst 绘制目标图
            innerDrawable.draw(canvas);

            //设置混合模式
            srcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            //src 绘制源图
            canvas.drawPath(srcPath, srcPaint);
            //清除混合模式
            srcPaint.setXfermode(null);
            //还原画布
            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        innerDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        innerDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return innerDrawable.getOpacity();
    }
}
