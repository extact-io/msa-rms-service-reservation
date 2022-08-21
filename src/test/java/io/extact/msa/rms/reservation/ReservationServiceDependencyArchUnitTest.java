package io.extact.msa.rms.reservation;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms.", importOptions = ImportOption.DoNotIncludeTests.class)
class ReservationServiceDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // rms.reservationパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------
    /**
     * webapiパッケージ内のアプリのコードで依存OKなライブラリの定義。
     */
    @ArchTest
    static final ArchRule test_webapiパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.reservation.webapi..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.reservation..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.jwt..",
                                "org.eclipse.microprofile.openapi..",
                                "javax.inject..",
                                "javax.enterprise.context..",
                                "javax.validation..",
                                "javax.ws.rs..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * serviceパッケージ内のアプリのコードで依存OKなライブラリの定義。依存してよいのは以下のモノのみ
     */
    @ArchTest
    static final ArchRule test_serviceパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.reservation.service..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.reservation..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "javax.inject..",
                                "javax.enterprise.context..",
                                "javax.transaction..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * persistenceパッケージ内のアプリのコードで依存OKなライブラリの定義。
     */
    @ArchTest
    static final ArchRule test_persistenceパッケージで依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.reservation.persistence..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.msa.rms.platform.core..",
                                "io.extact.msa.rms.platform.fw..",
                                "io.extact.msa.rms.reservation..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.config..",
                                "javax.inject..",
                                "javax.enterprise.context..",
                                "javax.enterprise.inject..", // for InjectionPoint
                                "javax.persistence..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

    /**
     * domainパッケージ内部の依存関係の定義
     */
    @ArchTest
    static final ArchRule test_domainパッケージのクラスが依存してよいパッケージの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.msa.rms.reservation.domain..")
                .should()
                    .onlyDependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.msa.rms.platform.fw.domain..",
                                "io.extact.msa.rms.reservation.domain..",
                                "org.apache.commons.lang3..",
                                "javax.persistence..",
                                "javax.validation..",
                                "java.."
                                );

    /**
     * javax.persistence.*への依存パッケージの定義
     * <pre>
     * ・javax.persistence.*に依存するのはpersistence.jpaパッケージとdomain(entity)パッケージの2つであること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_JPAへの依存パッケージの定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.msa.rms.reservation..")
                    .and().resideOutsideOfPackage("io.extact.msa.rms.reservation.persistence.jpa..")
                    .and().resideOutsideOfPackage("io.extact.msa.rms.reservation.domain..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "javax.persistence.."
                        );

    /**
     * persistenceの実装パッケージへの依存がないことの定義
     * <pre>
     * ・persistenceパッケージはjpa or fileパッケージに依存してないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_persistenceの実装パッケージへの依存がないことの定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.msa.rms.reservation.persistence") // persistence直下
                .should()
                    .dependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.msa.rms.reservation.persistence.jpa..",
                                "io.extact.msa.rms.reservation.persistence.file.."
                                );
}
