# Clunk

## Examples

```
record User(name: String)

test "text from text element is read" {
    val element = textElement("Hello!");
    
    val result = readElement(element);
    
    assertThat(result, equalTo(documents.text("Hello!")));
}

test "" {
    val user = User(.name = "Bob");

    val result = user.name;

    assertThat(result, equalTo("Bob"));
}
```

## Backends

### Java

[Java syntax](https://docs.oracle.com/javase/specs/jls/se17/html/jls-19.html)
