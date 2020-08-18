# MergeKt
Library for merge kotlin objects

# Reason
One of way for saving users traffic receive only data change data. In this case we need to merge local data with new data. We don't find library for implement with without reflection and create mergeKt.
//TBD

# Example

```kotlin
data class Entity(
    val count: Optional<Long>,
    val name: Optional<String>
)

val initClass = Entity(Optional.Data(27L), Optional.NoData)
val updateClass =  Entity(Optional.NoData, Optional.Data("test"))

val result = initClass.merge(updateClass)

result.count //Optional.Data(27L)
result.name //Optional.Data("test")
```
