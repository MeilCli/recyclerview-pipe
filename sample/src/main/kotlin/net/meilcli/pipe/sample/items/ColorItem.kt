package net.meilcli.pipe.sample.items

import androidx.annotation.ColorInt
import net.meilcli.pipe.IPipeItem

class ColorItem(@ColorInt val color: Int, val number: Int? = 0) : IPipeItem