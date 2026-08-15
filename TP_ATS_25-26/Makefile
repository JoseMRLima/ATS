# ATS 2025/2026
# Requer: Java 21, Java 8 (EvoSuite), Maven, Gradle wrapper em Projeto2/

PROJETO1_DIR  := Projeto1/SpotifyUM
PROJETO2_DIR  := Projeto2
JAVA_HOME     := /usr/lib/jvm/java-21-openjdk-amd64
MVN           := JAVA_HOME=$(JAVA_HOME) mvn -f $(PROJETO1_DIR)/pom.xml
GRADLE        := JAVA_HOME=$(JAVA_HOME) bash $(PROJETO2_DIR)/gradlew -p $(PROJETO2_DIR)
PBT_PYTHON    := python3
PBT_SCRIPT    := $(PROJETO1_DIR)/property-based-testing/generate_test_data.py

JAVA8         := /usr/lib/jvm/java-8-openjdk-amd64/bin/java
EVOSUITE_JAR  := $(abspath tools/evosuite-1.0.6.jar)
EVOSUITE_CLASSES := \
    org.Model.Music.Music \
    org.Model.Music.MusicMultimedia \
    org.Model.Music.MusicReproduction \
    org.Model.Album.Album \
    org.Model.User.User \
    org.Model.Plan.PlanFree \
    org.Model.Plan.PlanPremiumBase \
    org.Model.Plan.PlanPremiumTop \
    org.Model.Playlist.Playlist \
    org.Model.Playlist.PlaylistCreator \
    org.Model.Playlist.PlaylistFavorites \
    org.Model.Playlist.PlaylistRandom

.PHONY: all all1 all2 \
        test test1 test2 \
        coverage coverage1 coverage2 \
        pit pit1 pit2 \
        pbt1 pbt-data1 pbt2 \
        evosuite1 \
        clean clean1 clean2 \
        help

# ─── Default ──────────────────────────────────────────────────────────────────

all: all1 all2

# coverage corre por último: pbt1/pbt2 fazem 'test' com filtro e
# regenerariam o relatório JaCoCo só com a PropertyBasedTest
all1: test1 pit1 pbt1 coverage1

all2: test2 pit2 pbt2 coverage2

# ─── Tests ────────────────────────────────────────────────────────────────────

test: test1 test2

test1:
	@echo "==> Projeto1: testes JUnit"
	$(MVN) test

test2:
	@echo "==> Projeto2: testes JUnit"
	$(GRADLE) test

# ─── Coverage ─────────────────────────────────────────────────────────────────

coverage: coverage1 coverage2

coverage1:
	@echo "==> Projeto1: cobertura JaCoCo"
	$(MVN) test jacoco:report
	@echo ">>> Relatório: $(PROJETO1_DIR)/target/site/jacoco/index.html"

coverage2:
	@echo "==> Projeto2: cobertura JaCoCo"
	$(GRADLE) test jacocoTestReport
	@echo ">>> Relatório: $(PROJETO2_DIR)/build/reports/jacoco/test/html/index.html"

# ─── Mutation Testing ─────────────────────────────────────────────────────────

pit: pit1 pit2

pit1:
	@echo "==> Projeto1: mutation testing PIT"
	$(MVN) test-compile org.pitest:pitest-maven:mutationCoverage
	@echo ">>> Relatório: $(PROJETO1_DIR)/target/pit-reports/"

pit2:
	@echo "==> Projeto2: mutation testing PIT"
	$(GRADLE) pitest
	@echo ">>> Relatório: $(PROJETO2_DIR)/build/reports/pitest/"

# ─── EvoSuite: geração automática de testes ──────────────────────────────────

evosuite1:
	@echo "==> Projeto1: compilar com Java 8 bytecode (necessário para EvoSuite)"
	$(MVN) clean compile -Dmaven.compiler.source=1.8 -Dmaven.compiler.target=1.8 -q
	@echo "==> Projeto1: gerar testes com EvoSuite (search_budget=60s por classe)"
	@cd $(PROJETO1_DIR) && for cls in $(EVOSUITE_CLASSES); do \
	    echo "  -> $$cls"; \
	    $(JAVA8) -jar $(EVOSUITE_JAR) -class $$cls \
	        -projectCP target/classes \
	        -Dsearch_budget=60 2>&1 | grep -E "Generated|mutation score|coverage|Error|Problem" | tail -3; \
	done
	@echo "==> Projeto1: copiar testes gerados para src/test/java"
	@find $(PROJETO1_DIR)/evosuite-tests -name "*.java" | while read f; do \
	    dir=$$(dirname "$$f" | sed "s|$(PROJETO1_DIR)/evosuite-tests/|$(PROJETO1_DIR)/src/test/java/|"); \
	    mkdir -p "$$dir"; cp "$$f" "$$dir/"; \
	done
	@echo "==> Projeto1: corrigir incompatibilidades Java 8 -> Java 11 nos testes gerados"
	$(PBT_PYTHON) tools/fix_evosuite_tests.py $(PROJETO1_DIR)
	@echo "==> Projeto1: recompilar com Java 11 e correr testes EvoSuite"
	$(MVN) test jacoco:report -q
	@echo ""
	@echo ">>> Testes gerados : $(PROJETO1_DIR)/src/test/java (padrão *_ESTest.java)"
	@echo ">>> Relatório JaCoCo: $(PROJETO1_DIR)/target/site/jacoco/index.html"
	@echo ""
	@echo "==> Resumo EvoSuite (testes + cobertura):"
	@$(MVN) surefire-report:report-only -q 2>/dev/null || true
	@echo ">>> Relatório Surefire: $(PROJETO1_DIR)/target/site/surefire-report.html"

# ─── Property-Based Testing (Hypothesis + JUnit @ParameterizedTest) ──────────

pbt-data1:
	@echo "==> Projeto1: gerar dados de teste com Hypothesis"
	$(PBT_PYTHON) $(PBT_SCRIPT)

pbt1: pbt-data1
	@echo "==> Projeto1: correr testes Property-Based (JUnit @ParameterizedTest)"
	$(MVN) test -Dtest=PropertyBasedTest

pbt2:
	@echo "==> Projeto2: correr testes Property-Based (jqwik)"
	$(GRADLE) test --tests "org.spotifumtp37.PropertyBasedTest"
	@echo ">>> Relatório: $(PROJETO2_DIR)/build/reports/tests/test/index.html"

# ─── Clean ────────────────────────────────────────────────────────────────────

clean: clean1 clean2

clean1:
	@echo "==> Projeto1: limpar"
	$(MVN) clean

clean2:
	@echo "==> Projeto2: limpar"
	$(GRADLE) clean

# ─── Help ─────────────────────────────────────────────────────────────────────

help:
	@echo ""
	@echo "Targets disponíveis:"
	@echo "  make test1      testes JUnit — Projeto1"
	@echo "  make test2      testes JUnit — Projeto2"
	@echo "  make test       testes de ambos"
	@echo "  make coverage1  JaCoCo — Projeto1"
	@echo "  make coverage2  JaCoCo — Projeto2"
	@echo "  make coverage   JaCoCo de ambos"
	@echo "  make pit1       PIT mutation testing — Projeto1"
	@echo "  make pit2       PIT mutation testing — Projeto2"
	@echo "  make pit        PIT de ambos"
	@echo "  make evosuite1  EvoSuite — gerar testes automáticos — Projeto1"
	@echo "  make pbt1       Property-Based Testing (Hypothesis + JUnit) — Projeto1"
	@echo "  make pbt-data1  regenerar CSVs com Hypothesis — Projeto1"
	@echo "  make pbt2       Property-Based Testing (jqwik) — Projeto2"
	@echo "  make all1       tudo para o Projeto1"
	@echo "  make all2       tudo para o Projeto2"
	@echo "  make all        tudo para ambos"
	@echo "  make clean      limpar artefactos de build"
	@echo ""
