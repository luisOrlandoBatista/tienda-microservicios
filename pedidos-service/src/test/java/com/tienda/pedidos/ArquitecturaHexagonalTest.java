package com.tienda.pedidos;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArquitecturaHexagonalTest {

    private static final JavaClasses CLASES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.tienda.pedidos");

    @Test
    void el_dominio_es_java_puro_y_no_es_reactivo() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..", "..infrastructure..",
                        "org.springframework..", "jakarta..",
                        "reactor..")                       // ← NUEVO: el dominio no conoce Mono/Flux
                .because("el dominio no depende de frameworks ni del modelo reactivo")
                .check(CLASES);
    }

    @Test
    void la_aplicacion_no_depende_de_la_infraestructura() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(CLASES);
    }

    @Test
    void la_aplicacion_solo_usa_spring_para_las_transacciones() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat(
                        resideInAPackage("org.springframework..")
                                .and(not(resideInAPackage("org.springframework.transaction.annotation.."))))
                .check(CLASES);
    }

    @Test
    void los_adaptadores_de_entrada_no_usan_los_de_salida() {
        noClasses().that().resideInAPackage("..adapter.in..")
                .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
                .check(CLASES);
    }
}