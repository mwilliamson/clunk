# Clunk

## Examples

```
record User(name: String)

test "user slug is derived from user name" {
    val user = User(.name = "Bob");

    val result = user.slug();

    assertThat(result, equalTo("bob"));
}
```

## Backends

### Java

[Java syntax](https://docs.oracle.com/javase/specs/jls/se17/html/jls-19.html)
