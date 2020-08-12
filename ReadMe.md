# MergeKt
Library for merge kotlin objects

# Reason
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
