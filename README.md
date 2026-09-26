# Duck Typing em OO2

Paradigmas de Linguagens de Programação (IN1007), CIn-UFPE, 2026.2
Prof. Augusto Sampaio

## Equipe

- Gabriel Lopes - gls6
- Rafael Labio - rrl3

## Descrição

Este projeto estende a Linguagem Orientada a Objetos 2 (OO2) com **duck typing**. Com ele, um objeto é aceito pelo que sabe fazer (os métodos que possui), e não só pelo nome da classe que declara.

> *"Se anda como um pato e grasna como um pato, então é um pato."*

Hoje, OO2 tem tipagem **nominal**. Em uma chamada `e.m(...)`, o verificador de tipos obtém a classe *declarada* de `e` e procura `m` nessa classe e em suas superclasses. Duas classes sem relação de herança não podem ser usadas uma no lugar da outra, mesmo que tenham exatamente os mesmos métodos.

Em tempo de execução, porém, `ChamadaMetodoOO2.executar` já busca o método pela classe *real* do objeto (via `getProcedimentoHierarquia`). Ou seja, o despacho por nome já existe, e o que impede o duck typing é a verificação estática. O projeto atua nesse ponto e introduz duas formas complementares de duck typing:

1. **Duck typing dinâmico (tipo `dyn`)**, no estilo de Python e Ruby. Uma variável do tipo `dyn` pode referenciar qualquer objeto. As chamadas de método sobre ela não são verificadas estaticamente: a existência do método e a compatibilidade dos argumentos são checadas em tempo de execução.
2. **Duck typing estático (`protocolo`)**, no estilo das interfaces de Go e do `typing.Protocol` de Python. Um protocolo descreve um conjunto de assinaturas de métodos. Qualquer classe que tenha esses métodos satisfaz o protocolo **sem declarar isso explicitamente** (tipagem estrutural), e a verificação continua sendo feita antes da execução.

| | Classe (hoje) | `protocolo` (novo) | `dyn` (novo) |
|---|---|---|---|
| Critério de aceitação | nome / herança | estrutura (métodos) | nenhum |
| Chamada `e.m(...)` verificada em | compilação | compilação | execução |
| Erro "método inexistente" aparece | antes de executar | antes de executar | durante a execução |
| Exige `extends`/declaração explícita | sim | não | não |

## BNF

A gramática abaixo é a de OO2 (conforme `OO2.jj`), com as alterações marcadas com `(*)`.

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

Observações sobre a sintaxe:

- `protocolo` e `dyn` passam a ser **palavras reservadas**.
- `TipoClasse ::= Id` agora pode nomear **uma classe ou um protocolo**. O parser não distingue os dois (ele só vê um `Id`). A distinção é feita na verificação de tipos, consultando o ambiente. Por isso, classes e protocolos compartilham o mesmo espaço de nomes: declarar `classe Pato` e `protocolo Pato` no mesmo programa é erro.
- Um protocolo só tem assinaturas (sem corpo), sem atributos e sem construtor. Por isso ele não pode ser instanciado: `new Falante()` é erro de tipo.

## Semântica

### Relação de compatibilidade de tipos

Hoje, OO2 compara tipos com `equals` na maioria dos pontos: passagem de parâmetros, declaração com expressão, `new`. A herança só é considerada em `AtribuicaoOO2` e em `DecVariavelObjetoOO2`. O projeto troca essas comparações por uma única relação, **`T ≼ U`** ("um valor do tipo `T` pode ser usado onde se espera `U`"), definida por:

| # | Regra | Verificação |
|---|---|---|
| R1 | `T ≼ T` | estática |
| R2 | `null ≼ U`, se `U` é classe, protocolo ou `dyn` | estática |
| R3 | `C ≼ D`, se a classe `C` herda (transitivamente) da classe `D` | estática (já existe em `HierarquiaUtils`) |
| R4 | `C ≼ P`, se a classe `C` **satisfaz** o protocolo `P` (ver abaixo) | estática |
| R5 | `P ≼ Q`, se o protocolo `P` tem **todas** as assinaturas de `Q` | estática |
| R6 | `T ≼ dyn`, para todo `T` que não seja primitivo | estática |
| R7 | `dyn ≼ U`, para todo `U` que não seja primitivo | **aceita na compilação, verificada na execução** |

Tipos primitivos (`int`, `boolean`, `string`) só são compatíveis consigo mesmos (R1). `dyn` representa **referências a objetos**, então `dyn x = 5` é erro de tipo.

A regra R7 funciona como um *cast* implícito. Passar um `dyn` para um parâmetro do tipo `Falante` é aceito na compilação. Na execução, o interpretador confere se a classe real do objeto satisfaz `Falante`; se não satisfizer, lança um erro de execução.

### Quando uma classe satisfaz um protocolo

Uma classe `C` **satisfaz** o protocolo `P` se, para **cada** assinatura `proc m(T1 x1, ..., Tn xn)` de `P`:

1. existe um método `m` em `C` **ou em alguma superclasse de `C`** (a mesma busca de `getProcedimentoHierarquia`, então um método sobrescrito conta);
2. esse método tem exatamente `n` parâmetros;
3. o tipo do i-ésimo parâmetro é **igual** a `Ti`, para todo `i` (invariância).

Os nomes dos parâmetros não importam, só os tipos. A classe pode ter métodos a mais; eles simplesmente não são visíveis através do protocolo.

> A invariância nos parâmetros é uma escolha de simplicidade. A contravariância (aceitar `proc m(Animal a)` onde o protocolo pede `proc m(Cachorro c)`) também seria segura, mas deixaria a regra mais difícil de explicar e testar. Como os métodos de OO2 não retornam valor (`proc`), não é preciso tratar tipo de retorno.

### Chamada de método `e.m(a1, ..., an)`

A sintaxe da chamada não muda. O que muda é o que o verificador faz, conforme o **tipo estático** de `e`:

| Tipo estático de `e` | Verificação estática (`checaTipo`) | Execução (`executar`) |
|---|---|---|
| Classe `C` | como hoje: procura `m` em `C` e superclasses e confere os argumentos com `≼` | despacho pela classe real (como hoje) |
| Protocolo `P` | procura `m` **nas assinaturas de `P`** (não na classe) e confere os argumentos com `≼` | despacho pela classe real; o sucesso é garantido pela verificação estática |
| `dyn` | só verifica se `e` e cada `ai` estão bem tipados isoladamente; **não** procura `m` | procura `m` na classe real e confere aridade e argumentos; se falhar, lança `ErroDuckTypingException` |

Na execução, `ChamadaMetodoOO2.executar` passa a seguir estes passos:

```
executar(e.m(a1..an)):
  v  := avaliar(e)
  se v é null                         -> ErroDuckTypingException("chamada de m sobre null")
  C  := classe real do objeto referenciado por v
  pm := getProcedimentoHierarquia(C, m)
  se pm não existe                    -> ErroDuckTypingException("classe C não possui método m")
  se |parâmetros de pm| != n          -> ErroDuckTypingException("m espera k argumentos, recebeu n")
  vi := avaliar(ai), para cada i
  se não tipoDoValor(vi) ≼ Ti         -> ErroDuckTypingException("argumento i de m incompatível")
  executa pm com this = v             (como hoje)
```

`tipoDoValor` obtém o tipo de um valor em tempo de execução: `ValorInteiro → int`, `ValorBooleano → boolean`, `ValorString → string`, `ValorNull → null` e `ValorRef → TipoClasse(classe real do objeto)`.

Essas checagens são feitas em **toda** chamada, não só quando o receptor é `dyn`, porque o nó da AST não guarda o tipo estático do receptor. Para receptores de classe ou protocolo elas sempre passam (já foram garantidas na compilação), e ainda resolvem de quebra um problema atual: chamar um método sobre `null` hoje gera um `ClassCastException` sem mensagem clara.

### Declarações e atribuições

- `dyn d := new Pato()` e `Falante f := new Pato()`: hoje, `DecVariavelObjetoOO2` associa a variável ao tipo da **classe instanciada**, e não ao tipo declarado. Com isso `dyn` e protocolos não teriam efeito. A regra passa a ser: se o tipo declarado é `dyn` ou um protocolo, a variável recebe o **tipo declarado**, e a compatibilidade é conferida com `≼`. Para classes, o comportamento atual é mantido, para não quebrar os programas existentes.
- `x := e` (`AtribuicaoOO2`), `T x = e` (`SimplesDecVariavel`) e `x := new C(...)` (`NewOO2`) passam a aceitar `tipo(e) ≼ tipo(x)` em vez de igualdade. Quando `e` é `dyn` e `x` não é (regra R7), o `executar` confere `tipoDoValor(valor) ≼ tipo(x)` antes de gravar o valor.
- Em uma variável `dyn`, a mesma variável pode receber objetos de classes diferentes ao longo da execução (exemplo 1).

### Decisões de escopo

- **Acesso a atributo sobre `dyn` não é permitido** (`d.nome` com `d : dyn` é erro de tipo). O duck typing do projeto trata do *comportamento* (métodos). Atributos continuam acessíveis pelo tipo da classe ou por `this` dentro dos métodos.
- **Protocolos só descrevem métodos.** Não há atributos em protocolos.
- **Protocolos são declarados antes de serem usados**, como as classes hoje: `ListaDeclaracaoOO` é processada em ordem, e o `checaTipo` de uma classe já confere os tipos dos parâmetros dos seus métodos.
- Não há `extends` entre protocolos. A "herança" de protocolos já vem da regra estrutural R5.

## Alterações no interpretador

Os caminhos abaixo são relativos a `Objetos2/src/loo2/plp/`. Toda mudança de gramática e AST precisa ser espelhada no pacote `WebDebug` (`OO2Debug.jj`), que duplica o parser de OO2.

### Arquivos novos

| Arquivo | Responsabilidade |
|---|---|
| `orientadaObjetos2/declaracao/DecOO.java` | Interface comum a `DecClasseSimplesOO2` e `DecProtocolo`, com `elabora(AmbienteExecucaoOO2)` e `checaTipo(AmbienteCompilacaoOO2)`. |
| `orientadaObjetos2/declaracao/protocolo/DecProtocolo.java` | Nó da AST de `protocolo Id { ... }`. O `checaTipo` valida os tipos dos parâmetros de cada assinatura, rejeita assinaturas duplicadas e registra o protocolo no ambiente. O `elabora` registra o protocolo também no ambiente de execução, porque a regra R7 precisa dele em runtime. |
| `orientadaObjetos2/declaracao/protocolo/Assinatura.java` e `ListaAssinatura.java` | Nome do método mais `ListaDeclaracaoParametro` (reaproveitada de OO1). |
| `orientadaObjetos2/memoria/DefProtocolo.java` | Definição guardada no ambiente: `getAssinatura(Id)`, `getAssinaturas()`. |
| `orientadaObjetos2/util/TipoDinamico.java` | Implementa `Tipo`. Singleton `TipoDinamico.DYN`, `eValido` sempre `true`, `toString() = "dyn"`. |
| `orientadaObjetos2/util/CompatibilidadeTipos.java` | Implementa `≼` (`ehCompativel`), `satisfaz(classe, protocolo)` e `tipoDoValor(valor, ambiente)`. Reaproveita `HierarquiaUtils.ehSubTipo` para a regra R3. |
| `orientadaObjetos2/excecao/ErroDuckTypingException.java` | Erro de execução das checagens dinâmicas. Estende `RuntimeException` para não precisar alterar a cláusula `throws` de todos os `Comando`. O `main` do parser já captura `Exception` e reporta o erro. |
| `orientadaObjetos2/excecao/ProtocoloJaDeclaradoException.java` e `ProtocoloNaoDeclaradoException.java` | Equivalentes às exceções de classe. |

### Arquivos alterados

| Arquivo | Mudança |
|---|---|
| `orientadaObjetos2/parser/OO2.jj` | Tokens `PROTOCOLO` (`"protocolo"`) e `DYN` (`"dyn"`). Novas produções `PDecOO`, `PDecProtocolo`, `PListaAssinatura`, `PAssinatura` e `PTipoDinamico`. `PListaDeclaracaoOO` passa a usar `PDecOO` no lugar de `PDecClasseAtomica`. `PTipo` ganha a alternativa `< DYN >`. |
| `orientadaObjetos2/declaracao/ListaDeclaracaoOO.java` | Passa de `Lista<DecClasse>` para `Lista<DecOO>`, e os casts para `DecClasseSimplesOO2` são removidos. |
| `orientadaObjetos2/memoria/AmbienteCompilacaoOO2.java`, `ContextoCompilacaoOO2.java`, `AmbienteExecucaoOO2.java` e `ContextoExecucaoOO2.java` | Ganham `mapProtocolo(Id, DefProtocolo)`, `getDefProtocolo(Id)` e `ehProtocolo(Id)`. O `map` de classe e o de protocolo rejeitam nomes já usados pelo outro. |
| `orientadaObjetos1/util/TipoClasse.java` | `eValido` passa a aceitar um `Id` que nomeie uma classe **ou** um protocolo. |
| `orientadaObjetos2/comando/ChamadaMetodoOO2.java` | `checaTipo` se divide nos três casos da tabela de chamada (classe / protocolo / `dyn`). `executar` ganha as checagens dinâmicas descritas acima. |
| `orientadaObjetos1/comando/ChamadaProcedimento.java` | `checaTipo`: `listaTipo.head().equals(tipoFormal)` passa a ser `CompatibilidadeTipos.ehCompativel(...)`. `bindParameters`: confere `tipoDoValor(v) ≼ tipoFormal` antes de ligar cada parâmetro, o que implementa a regra R7 (para argumentos de classe ou protocolo, a checagem sempre passa). |
| `orientadaObjetos1/declaracao/variavel/SimplesDecVariavel.java`, `orientadaObjetos2/comando/AtribuicaoOO2.java` e `orientadaObjetos2/comando/NewOO2.java` | Igualdade de tipos substituída por `≼`. Em `AtribuicaoOO2`, o método privado `defClasseRightExtendsDefClasseLeft` é absorvido por `CompatibilidadeTipos`. |
| `orientadaObjetos2/declaracao/variavel/DecVariavelObjetoOO2.java` | Associa a variável ao tipo declarado quando ele é `dyn` ou protocolo. Rejeita `new P()` quando `P` é protocolo. |
| `orientadaObjetos2/expressao/leftExpression/AcessoAtributoIdOO2.java` | Erro de tipo quando o objeto acessado é `dyn` ou protocolo. |

### Esboço de `CompatibilidadeTipos`

```java
public static boolean ehCompativel(Tipo t, Tipo u, AmbienteCompilacaoOO2 amb) {
    if (t.equals(u))                                        return true;  // R1
    if (t instanceof TipoPrimitivo || u instanceof TipoPrimitivo) return false;
    if (u == TipoDinamico.DYN)                              return true;  // R2, R6
    if (t == TipoDinamico.DYN)                              return true;  // R7 (checado em runtime)
    if (t.equals(TipoClasse.TIPO_NULL))                     return true;  // R2
    boolean tProt = amb.ehProtocolo(t.getTipo());
    boolean uProt = amb.ehProtocolo(u.getTipo());
    if (!tProt && !uProt) return HierarquiaUtils.ehSubTipo(t, u, amb);    // R3
    if (!tProt &&  uProt) return satisfaz(t.getTipo(), u.getTipo(), amb); // R4
    if ( tProt &&  uProt) return contem(t.getTipo(), u.getTipo(), amb);   // R5
    return false;  // protocolo -> classe: não há como garantir estaticamente
}

public static boolean satisfaz(Id classe, Id protocolo, AmbienteCompilacaoOO2 amb) {
    for (Assinatura a : amb.getDefProtocolo(protocolo).getAssinaturas()) {
        Procedimento p = buscaNaHierarquia(classe, a.getNome(), amb);   // null se não achar
        if (p == null || !mesmosTiposDeParametros(p.getParametrosFormais(), a.getParametros()))
            return false;
    }
    return true;
}
```

Em tempo de execução, o mesmo código é usado com `tipoDoValor(v)` no lugar de `t`. Por isso as consultas ao ambiente (`getDefClasse`, `getDefProtocolo`, superclasse) vão ficar em uma interface comum aos ambientes de compilação e de execução.

## Exemplos

A saída de `write` aparece como "Saída". Quando o programa é recusado pelo `checaTipo`, o interpretador imprime `Erro de tipo`, como já faz hoje.

### 1. `dyn`: duas classes sem relação, a mesma chamada

```
{
  classe Pato {
    string nome = "";
    Pato(string nome) { this.nome := nome },
    proc falar() { write(this.nome ++ ": Quack!") },
    proc nadar() { write(this.nome ++ " nadando") }
  },
  classe Robo {
    int serie = 0;
    Robo(int serie) { this.serie := serie },
    proc falar() { write("Robo " ++ this.serie ++ ": Bip-bop") }
  };
  {
    dyn d := new Pato("Donald");
    d.falar();
    d := new Robo(42);
    d.falar()
  }
}
```

Saída:

```
Donald: Quack!
Robo 42: Bip-bop
```

`Pato` e `Robo` não têm relação de herança. Em OO2 atual, uma mesma variável não poderia guardar os dois. Com `dyn`, a chamada `d.falar()` é aceita na compilação e resolvida pela classe real do objeto em cada momento.

### 2. `dyn`: o erro aparece só na execução

Com as mesmas classes do exemplo 1:

```
  {
    dyn d := new Robo(7);
    d.falar();
    d.nadar()
  }
```

O programa **passa** no `checaTipo`, a primeira chamada executa normalmente e a segunda falha:

```
Robo 7: Bip-bop
ErroDuckTypingException: classe Robo não possui método nadar
```

Também são erros de execução: `d.falar(1)` (`falar espera 0 argumentos, recebeu 1`) e chamar `d.falar()` com `d` valendo `null`.

### 3. `dyn` como parâmetro

```
  classe Coral {
    int ensaios = 0;
    Coral() { skip },
    proc apresentar(dyn membro) {
      this.ensaios := this.ensaios + 1;
      membro.falar()
    }
  }
  ...
  {
    Coral c := new Coral(),
    Pato p := new Pato("Margarida"),
    Robo r := new Robo(3);
    c.apresentar(p);
    c.apresentar(r)
  }
```

Saída:

```
Margarida: Quack!
Robo 3: Bip-bop
```

Passar `Pato` e `Robo` para um parâmetro `dyn` usa a regra R6.

### 4. `protocolo`: duck typing verificado antes da execução

```
{
  protocolo Falante {
    proc falar()
  },
  classe Pato { ... como no exemplo 1 ... },
  classe Robo { ... como no exemplo 1 ... },
  classe Plateia {
    int ouvidos = 0;
    Plateia() { skip },
    proc ouvir(Falante f) {
      this.ouvidos := this.ouvidos + 1;
      f.falar()
    }
  };
  {
    Plateia pl := new Plateia(),
    Pato p := new Pato("Donald"),
    Robo r := new Robo(42);
    pl.ouvir(p);
    pl.ouvir(r)
  }
}
```

Saída:

```
Donald: Quack!
Robo 42: Bip-bop
```

Nem `Pato` nem `Robo` mencionam `Falante`. As duas classes satisfazem o protocolo porque têm `proc falar()` (regra R4), e isso é conferido no `checaTipo` da chamada `pl.ouvir(...)`.

### 5. `protocolo`: classe que não satisfaz é recusada

Acrescentando ao exemplo 4:

```
  classe Pedra {
    int peso = 0;
    Pedra(int peso) { this.peso := peso },
    proc rolar() { write("rolando") }
  }
  ...
  {
    Plateia pl := new Plateia(),
    Pedra pd := new Pedra(10);
    pl.ouvir(pd)
  }
```

Saída:

```
Erro de tipo
```

`Pedra` não tem `falar()`, então `Pedra ≼ Falante` é falso. O programa é recusado **antes** de executar qualquer comando. Com `proc ouvir(dyn f)`, o mesmo programa passaria na compilação e só falharia dentro de `ouvir`.

### 6. `protocolo`: só os métodos do protocolo são visíveis

```
  {
    Falante f := new Pato("Donald");
    f.falar();
    f.nadar()
  }
```

Saída:

```
Erro de tipo
```

`Pato` tem `nadar()`, mas `f` foi declarada como `Falante`, e `Falante` não tem `nadar`. A verificação usa o protocolo, não a classe real (primeira linha da tabela de chamada com protocolo). Com `dyn f`, o programa executaria e imprimiria as duas linhas.

### 7. Assinaturas precisam bater nos tipos

```
  protocolo Contador {
    proc incrementar(int passo)
  },
  classe ContadorInteiro {
    int valor = 0;
    ContadorInteiro() { skip },
    proc incrementar(int n) { this.valor := this.valor + n }
  },
  classe ContadorTexto {
    string valor = "";
    ContadorTexto() { skip },
    proc incrementar(string s) { this.valor := this.valor ++ s }
  }
```

- `ContadorInteiro ≼ Contador`: o nome do parâmetro é diferente (`n` × `passo`), mas o tipo é igual.
- `ContadorTexto ⋠ Contador`: existe `incrementar`, mas com parâmetro `string` em vez de `int`.

### 8. Herança e compatibilidade entre protocolos

```
  protocolo Falante  { proc falar() },
  protocolo Anfibio  { proc falar(), proc nadar() },
  classe Ave { string nome = ""; Ave(string nome) { this.nome := nome },
               proc falar() { write(this.nome ++ ": piu") } },
  classe Marreco extends Ave { int x = 0; Marreco(string nome) { this.nome := nome },
               proc nadar() { write(this.nome ++ " nadando") } }
```

- `Marreco ≼ Anfibio`: `nadar` vem de `Marreco` e `falar` é herdado de `Ave` (regra R4 com busca na hierarquia).
- `Ave ⋠ Anfibio`: falta `nadar`.
- `Anfibio ≼ Falante`: todo `Anfibio` sabe `falar` (regra R5). Então `Falante f = a`, com `a : Anfibio`, é válido.
- `Falante ⋠ Anfibio`.

### 9. De `dyn` para um tipo verificado (regra R7)

```
  {
    Plateia pl := new Plateia(),
    dyn d := new Pedra(10);
    pl.ouvir(d)
  }
```

É aceito na compilação (`dyn ≼ Falante`). Na execução, ao ligar o parâmetro `f`, o interpretador confere se a classe real (`Pedra`) satisfaz `Falante`:

```
ErroDuckTypingException: argumento 1 de ouvir: Pedra não satisfaz Falante
```

## Plano de implementação

Cada passo vira um commit na branch `dev` e mantém os testes de OO2 que já existem (`Testes/TesteOO2_*.txt`) passando.

1. **BNF e README**: documentar a gramática e a semântica (este documento).
2. **Parser**: tokens `protocolo` e `dyn` e as novas produções em `OO2.jj`, espelhadas em `WebDebug/.../OO2Debug.jj`. Ao fim deste passo, os programas dos exemplos são reconhecidos pelo parser.
3. **AST e ambientes**: `DecOO`, `DecProtocolo`, `Assinatura`, `DefProtocolo` e `TipoDinamico`, mais os mapas de protocolo em `ContextoCompilacaoOO2` e `ContextoExecucaoOO2`.
4. **Compatibilidade**: `CompatibilidadeTipos` e troca de `equals` por `≼` em `ChamadaProcedimento`, `SimplesDecVariavel`, `AtribuicaoOO2`, `NewOO2` e `DecVariavelObjetoOO2`.
5. **Chamada de método estática**: `ChamadaMetodoOO2.checaTipo` com os casos de protocolo e `dyn`.
6. **Chamada de método dinâmica**: checagens em `ChamadaMetodoOO2.executar` e `ChamadaProcedimento.bindParameters`, e `ErroDuckTypingException`.
7. **Testes**: um arquivo em `Testes/` por exemplo desta seção (`TesteOO2_DuckDyn.txt`, `TesteOO2_DuckDynErro.txt`, `TesteOO2_Protocolo.txt`, `TesteOO2_ProtocoloErro.txt`, ...), cobrindo os casos aceitos, os erros de tipo e os erros de execução.

## Como executar

```bash
cd Objetos2
mvn -q -B compile
java -cp target/classes loo2.plp.orientadaObjetos2.parser.OO2Parser ../Testes/TesteOO2_Protocolo.txt
```
