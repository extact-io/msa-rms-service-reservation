package io.extact.msa.rms.reservation;

import static com.tngtech.archunit.library.Architectures.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.msa.rms.reservation", importOptions = ImportOption.DoNotIncludeTests.class)
class ReservationLayerDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // rms.reservationのアプリケーションアーキテクチャレベルの依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * アプリケーションアーキテクチャのレイヤと依存関係の定義
     * <pre>
     * ・webapiレイヤはどのレイヤからも依存されていないこと（webapiレイヤは誰も使ってはダメ））
     * ・serviceレイヤはwebapiレイヤからのみ依存を許可（serviceレイヤを使って良いのはwebapiレイヤのみ）
     * ・persistenceレイヤはserviceレイヤからのみ依存を許可（persistenceレイヤを使って良いのはserviceレイヤのみ）
     * ・domianレイヤは上記3つのレイヤからの依存を許可
     * </pre>
     */
    @ArchTest
    static final ArchRule test_レイヤー間の依存関係の定義 = layeredArchitecture()
            .layer("webapi").definedBy("io.extact.msa.rms.reservation.webapi..")
            .layer("service").definedBy("io.extact.msa.rms.reservation.service..")
            .layer("persistence").definedBy("io.extact.msa.rms.reservation.persistence..")
            .layer("domain").definedBy("io.extact.msa.rms.reservation.domain..")

            .whereLayer("webapi").mayNotBeAccessedByAnyLayer()
            .whereLayer("service").mayOnlyBeAccessedByLayers("webapi")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("service")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("webapi", "service", "persistence");
}
