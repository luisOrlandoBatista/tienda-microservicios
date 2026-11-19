package com.tienda.catalogo;

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
            .importPackages("com.tienda.catalogo");

    @Test
    void el_dominio_no_depende_de_ninguna_otra_capa_ni_de_frameworks() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..", "..infrastructure..", "org.springframework..", "jakarta..")
                .because("el dominio debe ser Java puro")
                .check(CLASES);
    }

    @Test
    void la_aplicacion_no_depende_de_la_infraestructura() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .because("las dependencias apuntan hacia dentro")
                .check(CLASES);
    }

    @Test
    void la_aplicacion_solo_usa_spring_para_las_transacciones() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat(
                        resideInAPackage("org.springframework..")
                                .and(not(resideInAPackage("org.springframework.transaction.annotation.."))))
                .because("@Transactional es la única concesión permitida")
                .check(CLASES);
    }

    @Test
    void los_adaptadores_de_entrada_no_usan_los_de_salida() {
        noClasses().that().resideInAPackage("..adapter.in..")
                .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
                .because("el controlador debe pasar por los casos de uso, no ir directo a la base de datos")
                .check(CLASES);
    }
}