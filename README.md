# recyclerview-pipe
RecyclerView components for multiple source collection

focus on:
- multiple source collection
- observable collection
- split responsibilities to source collection, collections designer, view holder creator and recyclerview adapter
- design collections by method chain

source collection:
```kotlin
// MutableListPipe has operators alike MutableList
val colorPipe = MutableListPipe<ColorItem>()
val textPipe = MutableListPipe<TextItem>()

colorPipe.add(ColorItem(Color.BLUE))
textPipe.add(TextItem("Hello World"))
// ...
```

collections designer:
```kotlin
// linearStack, combine, take, skip, insertAt, insertEvery
val pipe = combine(colorPipe, textPipe, InsertStrategy.End)
```

view holder creator:
```kotlin
class ColorViewHolderSelector : IPipeViewHolderSelector<ColorViewHolder, ColorItem> {

    override val viewType: Int = R.layout.holder_color

    override fun match(index: Int, item: IPipeItem): Boolean {
        return item is ColorItem
    }

    override fun create(parent: ViewGroup): ColorViewHolder {
        return ColorViewHolder(parent)
    }
}

class TextViewHolderSelector : IPipeViewHolderSelector<TextViewHolder, TextItem> {

    override val viewType: Int = R.layout.holder_text

    override fun match(index: Int, item: IPipeItem): Boolean {
        return item is TextItem
    }

    override fun create(parent: ViewGroup): TextViewHolder {
        return TextViewHolder(parent)
    }
}

val viewHolderCreator = PipeViewHolderCreator(ColorViewHolderSelector(), TextViewHolderSelector())
```

recyclerview adapter:
```kotlin
val pipeAdapter = PipeAdapter(viewHolderCreator)
pipeAdapter.set(pipe)
```

## License
MIT License