# Análise e Teste de Software — Trabalho Prático 2025/2026

**Nota:** 17,5

**Universidade do Minho — Licenciatura em Engenharia Informática**

## Grupo

| Nome | Número |
|------|--------|
| José Mário Raimundo Lima | a106888 |
| Nuno Francisco Rocha Soares| a107366 |
| Rui Miguel Castro| a100753|

---

## Estrutura do projeto

```
.
├── Projeto1/SpotifyUM/   # Projeto Java/Maven (SpotifyUM)
├── Projeto2/             # Projeto Java/Gradle (SpotifUM TP37)
├── tools/                # EvoSuite JAR e script de correção
└── Makefile              # Automatização de todos os comandos
```

---

## Pré-requisitos

- Java 21 (`/usr/lib/jvm/java-21-openjdk-amd64`)
- Java 8 (`/usr/lib/jvm/java-8-openjdk-amd64`) — necessário para o EvoSuite
- Maven
- Python 3 com Hypothesis (`pip install hypothesis`)
- jqwik 1.8.5 — gerido automaticamente pelo Gradle (Projeto2)

---

## Como correr

### Testes JUnit

```bash
make test1      # Projeto1
make test2      # Projeto2
make test       # ambos
```

### Cobertura JaCoCo

```bash
make coverage1  # Projeto1  →  Projeto1/SpotifyUM/target/site/jacoco/index.html
make coverage2  # Projeto2  →  Projeto2/build/reports/jacoco/test/html/index.html
make coverage   # ambos
```

### Mutation Testing (PIT)

```bash
make pit1       # Projeto1  →  Projeto1/SpotifyUM/target/pit-reports/
make pit2       # Projeto2  →  Projeto2/build/reports/pitest/
make pit        # ambos
```

### EvoSuite (geração automática de testes)

```bash
make evosuite1  # gera testes para as classes do Projeto1 e corre-os
```

### Property-Based Testing

```bash
# Projeto1 — Hypothesis (Python) → CSV → JUnit @ParameterizedTest
make pbt1       # gera os CSVs com Hypothesis e corre os testes parametrizados
make pbt-data1  # só regenera os CSVs

# Projeto2 — jqwik (PBT nativo em Java)
make pbt2       # corre PropertyBasedTest com jqwik
```

### Tudo de uma vez

```bash
make all1   # test + coverage + pit + pbt do Projeto1
make all2   # test + coverage + pit + pbt do Projeto2
make all    # tudo
```

### Limpar artefactos de build

```bash
make clean
```

