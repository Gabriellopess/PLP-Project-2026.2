#!/bin/bash
# Roda os testes de OO2 que trazem a saida esperada num comentario
# ("// Saida esperada:") e compara com a saida real do interpretador.
#
# Uso, a partir da raiz do repositorio:
#   mvn -q -B compile
#   Testes/roda_testes_oo2.sh

DIR=$(cd "$(dirname "$0")" && pwd)
CP="$DIR/../Objetos2/target/classes"
if [ ! -d "$CP" ]; then
  echo "Classes nao encontradas em $CP. Rode antes: mvn -q -B compile"
  exit 2
fi

total=0
falhas=0
for teste in "$DIR"/TesteOO2_*.txt; do
  grep -q '^// Saida esperada:' "$teste" || continue
  total=$((total + 1))
  nome=$(basename "$teste" .txt)

  # linhas "//   ..." logo depois de "// Saida esperada:"
  esperado=$(sed -n '/^\/\/ Saida esperada:/,/^[^\/]/ s#^//   ##p' "$teste")

  # tira as linhas do proprio parser e as do stack trace
  obtido=$(java -cp "$CP" loo2.plp.orientadaObjetos2.parser.OO2Parser "$teste" 2>&1 \
    | grep -v '^OO2 PLP Parser Version' | grep -v '^[[:space:]]*at ')

  if [ "$esperado" == "$obtido" ]; then
    echo "ok      $nome"
  else
    echo "DIFERE  $nome"
    diff <(echo "$esperado") <(echo "$obtido") | sed 's/^/        /'
    falhas=$((falhas + 1))
  fi
done

echo "$((total - falhas))/$total ok"
[ "$falhas" -eq 0 ]
