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

A extensão é entregue como uma nova linguagem, **OO3**, no módulo `Objetos3`. OO3 é OO2 mais duck typing: todo programa OO2 válido é um programa OO3 válido, com o mesmo comportamento. O módulo `Objetos2` permanece intocado.

## BNF

A gramática abaixo é a de OO3 (conforme `Objetos3/src/loo3/plp/orientadaObjetos3/parser/OO3.jj`), que estende a de OO2. As alterações estão marcadas com `(*)`.

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

## Semântica

### Regras de compatibilidade

Todas as verificações que comparam tipos (declaração de variável, atribuição, `new`, passagem de argumentos) usam a mesma regra, centralizada em `CompatibilidadeTipos.ehCompativel(esperado, real)`. Um valor de tipo `real` é aceito onde se espera `esperado` quando:

| `esperado`  | `real` aceito                                                                                   |
|-------------|--------------------------------------------------------------------------------------------------|
| primitivo   | o mesmo primitivo                                                                                |
| classe `C`  | `C`, uma subclasse de `C`, ou `null`                                                             |
| protocolo `P` | `null`, uma classe que possui todos os métodos de `P`, ou um protocolo que exige ao menos os métodos de `P` |
| `dyn`       | qualquer classe, protocolo, `dyn` ou `null`                                                      |

Um valor `dyn` **nunca** é aceito onde se espera um tipo estático (classe ou protocolo): não há como garantir a compatibilidade antes da execução. Valores primitivos também não são aceitos em `dyn`, que referencia apenas objetos.

### `dyn`

- `dyn x := new C(...)`, `dyn x = null`, `x := expr` e parâmetros `dyn` aceitam qualquer objeto.
- Em `x.m(args)` com `x` de tipo `dyn`, o verificador de tipos checa apenas que as expressões em `args` estão bem tipadas. O método não é procurado.
- Em tempo de execução, `ChamadaMetodoOO2.executar` procura `m` na classe real do objeto (e superclasses). Se não existir, lança `ProcedimentoNaoDeclaradoException`. Se existir, o número e o tipo dos argumentos são checados contra os parâmetros formais (`CompatibilidadeTipos.checaArgumentosEmExecucao`); em caso de incompatibilidade, lança `ChamadaMetodoInvalidaException`.
- Acesso a atributo (`x.a`) sobre um receptor `dyn` é erro de tipo: só métodos participam do duck typing.

### `protocolo`

- `protocolo P { proc m1(...), proc m2(...) }` declara apenas assinaturas (nome e tipos dos parâmetros). Protocolos e classes compartilham o espaço de nomes e devem ser declarados antes do uso, como as classes.
- Uma classe satisfaz `P` quando, para cada assinatura de `P`, possui (própria ou herdada) um método de mesmo nome e mesmos tipos de parâmetros, na mesma ordem. Os nomes dos parâmetros não importam. Nada precisa ser declarado na classe (`ProtocoloUtils.classeSatisfaz`).
- Um protocolo `Q` satisfaz `P` quando contém todas as assinaturas de `P` (`ProtocoloUtils.protocoloSatisfaz`).
- Em `p.m(args)` com `p` de tipo protocolo, `m` deve ser uma das assinaturas do protocolo e os argumentos são verificados contra ela, antes da execução.
- Protocolos também são registrados no ambiente de execução, para que uma chamada sobre `dyn` cujo parâmetro formal é um protocolo seja checada estruturalmente em tempo de execução.

## Implementação

Módulo `Objetos3` (pacote raiz `loo3`). Como em OO2 (que carrega cópias adaptadas de OO1), as classes de OO1/OO2 adaptadas ficam em `loo3.plp.orientadaObjetos1` e `loo3.plp.orientadaObjetos2`; o que é novo em OO3 fica em `loo3.plp.orientadaObjetos3`:

| Pacote / classe | Papel |
|---|---|
| `parser/OO3.jj` | Gramática: tokens `protocolo` e `dyn`, produções `DecOO`, `DecProtocolo`, `ListaAssinatura`, `Assinatura`, `TipoDinamico` |
| `declaracao.DecOO` | Interface comum a `DecClasseSimplesOO2` e `DecProtocolo` (`ListaDeclaracaoOO` passa a ser uma lista de `DecOO`) |
| `declaracao.protocolo.*` | `DecProtocolo`, `Assinatura`, `ListaAssinatura` |
| `memoria.DefProtocolo` | Definição de protocolo guardada nos ambientes (`mapDefProtocolo` / `getDefProtocolo` em `AmbienteCompilacaoOO2` e `AmbienteExecucaoOO2`) |
| `util.TipoDinamico` | O tipo `dyn` |
| `util.TipoClasseOO3` | Tipo nomeado que é válido quando nomeia uma classe **ou** um protocolo |
| `util.CompatibilidadeTipos` | Regras de compatibilidade (compilação e execução) |
| `util.ProtocoloUtils` | Verificação estrutural de protocolos |
| `declaracao.variavel.SimplesDecVariavelOO3`, `comando.ChamadaProcedimentoOO3` | Declaração `Tipo Id = Expressao` e passagem de argumentos usando as regras de compatibilidade |
| `excecao.*` | `ProtocoloJaDeclaradoException`, `ProtocoloNaoDeclaradoException`, `ChamadaMetodoInvalidaException` |

Classes de OO2 adaptadas em `loo3.plp.orientadaObjetos2`: `ChamadaMetodoOO2` (caminhos `dyn` e protocolo, checagem em execução), `AtribuicaoOO2`, `NewOO2`, `DecVariavelObjetoOO2` (compatibilidade; a variável fica com o tipo declarado, não com a classe instanciada), `DecClasseSimplesOO2` (conflito de nome com protocolo), `AcessoAtributoIdOO2` (receptor precisa ser classe), `HierarquiaUtils` (busca de método na hierarquia compartilhada).

Correção herdada de OO2: o comando `x := new C(args)` não executava o construtor, pois `NewOO2` apenas sobrecarregava `executar(AmbienteExecucaoOO2)` e o despacho caía em `New.executar` de OO1. Em OO3 o construtor é executado também nesse comando.

## Como executar

```
cd Objetos3
mvn clean generate-sources compile exec:java          # executa o arquivo "input"
java -cp target/classes loo3.plp.orientadaObjetos3.parser.OO3Parser ../Testes/TesteOO3_Protocolo.txt
```

## Testes

Programas em `Testes/`:

| Arquivo | O que exercita | Resultado esperado |
|---|---|---|
| `TesteOO3_DuckDyn.txt` | `dyn` recebendo objetos de classes sem relação, reatribuição, parâmetro `dyn` | executa, despacho pela classe real |
| `TesteOO3_DuckDyn_MetodoInexistente.txt` | chamada de método inexistente sobre `dyn` | passa na tipagem; `ProcedimentoNaoDeclaradoException` em execução |
| `TesteOO3_DuckDyn_ArgumentoInvalido.txt` | argumento de tipo errado em chamada sobre `dyn` | passa na tipagem; `ChamadaMetodoInvalidaException` em execução |
| `TesteOO3_DuckDyn_ErroTipo.txt` | `dyn` atribuído a variável de classe | `Erro de tipo` |
| `TesteOO3_Protocolo.txt` | classes sem relação satisfazendo um protocolo, método herdado, protocolo → protocolo, atributo e parâmetro de tipo protocolo | executa |
| `TesteOO3_Protocolo_ErroTipo.txt` | classe sem um dos métodos exigidos | `Erro de tipo` |
| `TesteOO3_Protocolo_ErroAssinatura.txt` | método com tipo de parâmetro diferente da assinatura | `Erro de tipo` |
| `TesteOO3_Protocolo_Dyn.txt` | chamada sobre `dyn` cujo parâmetro formal é um protocolo | primeira chamada executa; segunda falha em execução |
| `TesteOO2_*.txt` | programas OO2 originais | mesmo resultado em OO2 e OO3 |
