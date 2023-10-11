## bhai_lang

- These implementations are only for learning purposes.
- Syntaxes are inspired from here https://github.com/DulLabs/bhai-lang

</br>and may include
1. low performance 
2. very bad error handling 
3. and more



### Building and compilation
``` java
cd your/dirs/bhai_lang
javac App.java -d "E:\bhai_lang\target"
java -classpath "E:\bhai_lang\target" App .\your\dirs\filename.bhai 
```

### Basic syntax

Hello World
```
// only support for sinfle line comment
bol_bhai "Hello World";
bol_bhai line_break;
```


Arithmetic (+, -, *, /)
```
bhai_ye_hai x = 10; // only double values
bhai_ye_hai y = 20;

bol_bhai x + y;
bol_bhai line_break;
bol_bhai x * y;
```

Control flow (+, -, *, /)
```
bhai_ye_hai winner = sahi; // var decl
agar_bhai (winner == sahi) {
    bol_bhai "tu winner hai.";
} warna_bhai {
    bol_bhai "tu looser hai.";
}
```
Loops
```
bhai_ye_hai banana = 4;
jab_tak_bhai (banana > 0) { // while loop
    bol_bhai "remaining banana: " + banana;
    banana = banana - 1;
}


// for loop
chalao_bhai (bhai_ye_hai i=0; i<6; i=i+1) {
    bol_bhai i;
    bol_bhai line_break;
}
```

function
```
karna_bhai multiply (a, b) {
    return a * b;
}

bol_bhai multiply(3, 6); // call
```

### mapping
```
        regular lang    bhai lang
        -------------------------
        true            sahi
        false           galat

        if              agar_bhai
        else            warna_bhai
        for             chalao_bhai
        while           jab_tak_bhai

        and             and
        or              or
        print           bol_bhai    
        func            karna_bhai
        return          lauta_bhai
        var             bhai_ye_hai
        null            nalla
        '\n'            line_break
```