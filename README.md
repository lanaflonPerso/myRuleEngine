# myRuleEngine

MyRule Engine is a Java rules engine inspired in EasyRule.

[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](http://opensource.org/licenses/MIT)

## Core features

 * Lightweight library and easy to learn API
 * POJO based development with annotation programming model
 * Useful abstractions to define business rules and apply them easily with Java

## Example

##### First, define your rule..

```java
public class PersonRule {

    @InjectFact(name="email")
    private Stringemail;
    
    @RuleCondition(prioridade=1)
    public Notification validarEmail()
    {
        boolean regra = entidade.getEmail() != null;
        return new Notification()
                .expressaoLogica(regra)
                .addMensagemFalse("invalide e-mail!");
    }
 
}
```

##### Then, fire it!

```java
public class Test {
    public static void main(String[] args) {
        // define facts

        // define rules

        // fire rules on known facts
    }
}
```

## License
myRuleEngine is released under the terms of the MIT license:

```
The MIT License (MIT)

Copyright (c) 2017, Lyndon Tavares (integraldominio@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
