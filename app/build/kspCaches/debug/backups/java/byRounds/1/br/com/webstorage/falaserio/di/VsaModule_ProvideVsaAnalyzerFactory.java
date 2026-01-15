package br.com.webstorage.falaserio.di;

import br.com.webstorage.falaserio.domain.audio.VsaAnalyzer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class VsaModule_ProvideVsaAnalyzerFactory implements Factory<VsaAnalyzer> {
  @Override
  public VsaAnalyzer get() {
    return provideVsaAnalyzer();
  }

  public static VsaModule_ProvideVsaAnalyzerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VsaAnalyzer provideVsaAnalyzer() {
    return Preconditions.checkNotNullFromProvides(VsaModule.INSTANCE.provideVsaAnalyzer());
  }

  private static final class InstanceHolder {
    static final VsaModule_ProvideVsaAnalyzerFactory INSTANCE = new VsaModule_ProvideVsaAnalyzerFactory();
  }
}
