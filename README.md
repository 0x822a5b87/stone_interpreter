# stone_interpreter

stone_interpreter

## BNF

```
factor: 	NUMBER | "(" expression ")"
term:		factor { ("*" | "/") factor }
expression:	term { ("+" | "-") term }
```

### EBNF symbol

[Extended Backus–Naur form](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form)

| symbol    | description      |
|-----------|------------------|
| =         | definition       |
| ,         | concatenation    |
| ;         | termination      |
| &#123;    | alternation      |
| [...]     | optional         |
| {...}     | repetition       |
| (...)     | grouping         |
| "..."     | terminal string  |
| '...'     | terminal string  |
| (* ... *) | comment          |
| ? ... ?   | special sequence |
| -         | exception        |

### EBNF example

```BNF
digit excluding zero = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;
digit                = "0" | digit excluding zero ;
```

```EBNF
twelve                          = "1", "2" ;
two hundred one                 = "2", "0", "1" ;
three hundred twelve            = "3", twelve ;
twelve thousand two hundred one = twelve, two hundred one ;
```

Expressions that may be omitted or repeated can be represented through curly braces { ... }:

```EBNF
natural number = digit excluding zero, { digit } ;
```

In this case, the strings 1, 2, ..., 10, ..., 10000, ... are correct expressions. To represent this, everything that is set within the curly braces may be repeated arbitrarily often, including not at all.

An option can be represented through squared brackets [ ... ]. That is, everything that is set within the square brackets may be present just once, or not at all:

```EBNF
integer = "0" | [ "-" ], natural number ;
```

## stone EBNF

```
primary:    "(" expr ")" | NUMBER | IDENTIFIER | STRING
factor:        "-" primary | primary
expr:        factor { OP factor }
block:        "{" [ statement ] {(";" | EOL) [ statement ]} "}"
statement:    "if" expr block ["else" block]
| "while" expr block
| simple
 
param: IDENTIFIER
params: IDENTIFIER { "," params }
param_list: "(" [ params ] ")"
def: "def" IDENTIFIER param_list block
args: expr { "," expr }
postfix: "(" args ")"
primary:    ("(" expr ")" | NUMBER | IDENTIFIER | STRING) { postfix }
simple: expr [ args ]
program: [ def | statemen ] ( ";" | EOL )
```


