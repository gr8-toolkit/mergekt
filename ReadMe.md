## MergeKt overview

MergeKt is a universal library for merging any Kotlin objects.
 
If you have an "old" entity with local data, the MergeKt library will merge it with any partial data updates received for some of its fields, thus updating the whole entity.

## Use case

The MergeKt library can be useful when the back end sends incomplete data updates to apply the difference and get all front end local data updated.

This saves user's Internet traffic, which can be important for the speed of an application.
## Reason
We did not find a suitable library that would do this for our app and so we decided to create our own. 

Feel free to contribute and/or contact us if you happen to find any error.

## Add to project (gradle)

```kotlin
implementation("com.github.parimatch-tech.mergekt:mergekt:0.2.2")
kapt("com.github.parimatch-tech.mergekt:mergekt-compiler:0.2.2")
```

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
