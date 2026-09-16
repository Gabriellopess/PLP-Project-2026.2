# Duck Typing em OO2

Paradigmas de Linguagens de Programação (IN1007), CIn-UFPE, 2026.2
Prof. Augusto Sampaio

## Equipe

- Gabriel Lopes - gls6
- Rafael Labio - rrl3

## Descrição

Este projeto estende a Linguagem Orientada a Objetos 2 (OO2) com **duck typing**: um objeto passa a ser aceito por aquilo que ele sabe fazer (os métodos que possui), e não apenas pelo nome da classe que declara.
Hoje, OO2 tem tipagem **nominal**. Em uma chamada `e.m(...)`, o verificador de tipos obtém a classe *declarada* de `e` e procura `m` nessa classe e em suas superclasses. Duas classes sem relação de herança não podem ser usadas de forma intercambiável, mesmo que tenham exatamente os mesmos métodos.

Em tempo de execução, porém, `ChamadaMetodoOO2.executar` já busca o método pela classe *real* do objeto (via `getProcedimentoHierarquia`). O mecanismo de despacho por nome já existe. O que impede o duck typing é a verificação estática. O projeto atua nesse ponto e introduz duas formas complementares de duck typing:

1. **Duck typing dinâmico (tipo `dyn`)**, no estilo de Python e Ruby. Uma variável de tipo `dyn` pode referenciar qualquer objeto. Chamadas de método sobre ela não são verificadas estaticamente, e a existência e compatibilidade do método são checadas em tempo de execução.
2. **Duck typing estático (`protocolo`)**, no estilo das interfaces de Go e do `typing.Protocol` de Python. Um protocolo descreve um conjunto de assinaturas de métodos. Qualquer classe que possua esses métodos satisfaz o protocolo **sem declarar isso explicitamente** (tipagem estrutural), e a verificação continua sendo feita antes da execução.

## BNF

A gramática abaixo é a de OO2 (conforme `OO2.jj`), com as alterações marcadas: `(*)`

Produções sem marcação permanecem como em OO2.

```
Programa           ::= "{" ListaDeclaracaoOO ";" Comando "}"

ListaDeclaracaoOO  ::= DecOO                                        (*)
                     | DecOO "," ListaDeclaracaoOO                  (*)

DecOO              ::= DecClasse | DecProtocolo                     (*)

DecClasse          ::= "classe" Id [ "extends" Id ]
                       "{" DecVariavel ";" DecConstrutor "," DecProcedimento "}"

DecProtocolo       ::= "protocolo" Id "{" ListaAssinatura "}"       (*)

ListaAssinatura    ::= Assinatura                                   (*)
                     | Assinatura "," ListaAssinatura

Assinatura         ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")"   (*)

DecConstrutor      ::= Id "(" [ ListaDeclaracaoParametro ] ")" "{" Comando "}"

DecProcedimento    ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")" "{" Comando "}"
                     | DecProcedimento "," DecProcedimento

ListaDeclaracaoParametro ::= Tipo Id
                           | Tipo Id "," ListaDeclaracaoParametro

DecVariavel        ::= Tipo Id "=" Expressao
                     | Tipo Id ":=" "new" Id "(" [ ListaExpressao ] ")"
                     | DecVariavel "," DecVariavel

Tipo               ::= TipoPrimitivo | TipoClasse | TipoDinamico    (*)
TipoPrimitivo      ::= "int" | "boolean" | "string"
TipoClasse         ::= Id                                           (*)
TipoDinamico       ::= "dyn"                                        (*)

Comando            ::= Atribuicao
                     | ComDeclaracao
                     | While
                     | IfThenElse
                     | IO
                     | Comando ";" Comando
                     | Skip
                     | New
                     | ChamadaMetodo

Skip               ::= "skip"
ComDeclaracao      ::= "{" DecVariavel ";" Comando "}"
While              ::= "while" Expressao "do" "{" Comando "}"
IfThenElse         ::= "if" Expressao "then" "{" Comando "}"
                     | "if" Expressao "then" "{" Comando "}" "else" "{" Comando "}"
IO                 ::= "write" "(" Expressao ")" | "read" "(" Id ")"
New                ::= LeftExpression ":=" "new" Id "(" [ ListaExpressao ] ")"
Atribuicao         ::= LeftExpression ":=" Expressao

ChamadaMetodo      ::= Expressao "." Id "(" [ ListaExpressao ] ")"
                       (* sintaxe inalterada; verificação de tipos e execução alteradas *)

ListaExpressao     ::= Expressao | Expressao "," ListaExpressao

Expressao          ::= Valor | ExpUnaria | ExpBinaria | LeftExpression | "this"
Valor              ::= ValorInteiro | ValorBooleano | ValorString | "null"
ExpUnaria          ::= "-" Expressao | "not" Expressao | "length" Expressao
ExpBinaria         ::= Expressao "+" Expressao | Expressao "-" Expressao
                     | Expressao "and" Expressao | Expressao "or" Expressao
                     | Expressao "==" Expressao | Expressao "++" Expressao
LeftExpression     ::= Id | AcessoAtributo
AcessoAtributo     ::= LeftExpression "." Id | "this" "." Id
```
