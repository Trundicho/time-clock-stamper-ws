package de.trundicho.timeclockstamper;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

class ArchitectureTest {

    @Test
    void testPortsAndAdaptersArchitecture() {
        String basePackage = "de.trundicho.timeclockstamper";
        JavaClasses jc = new ClassFileImporter()
                .importPackages(basePackage);
        Architectures.OnionArchitecture arch = onionArchitecture()
                .domainModels(basePackage + ".domain..")
                .domainServices(basePackage + ".service..")
                .applicationServices(basePackage + ".application..")
                .adapter("persistence", basePackage + ".adapters.persistence..");
        arch.check(jc);
    }
}
