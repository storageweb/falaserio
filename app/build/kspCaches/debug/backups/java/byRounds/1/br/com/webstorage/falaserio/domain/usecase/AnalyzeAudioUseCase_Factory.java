package br.com.webstorage.falaserio.domain.usecase;

import br.com.webstorage.falaserio.domain.audio.VsaAnalyzer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class AnalyzeAudioUseCase_Factory implements Factory<AnalyzeAudioUseCase> {
  private final Provider<VsaAnalyzer> vsaAnalyzerProvider;

  private AnalyzeAudioUseCase_Factory(Provider<VsaAnalyzer> vsaAnalyzerProvider) {
    this.vsaAnalyzerProvider = vsaAnalyzerProvider;
  }

  @Override
  public AnalyzeAudioUseCase get() {
    return newInstance(vsaAnalyzerProvider.get());
  }

  public static AnalyzeAudioUseCase_Factory create(Provider<VsaAnalyzer> vsaAnalyzerProvider) {
    return new AnalyzeAudioUseCase_Factory(vsaAnalyzerProvider);
  }

  public static AnalyzeAudioUseCase newInstance(VsaAnalyzer vsaAnalyzer) {
    return new AnalyzeAudioUseCase(vsaAnalyzer);
  }
}
