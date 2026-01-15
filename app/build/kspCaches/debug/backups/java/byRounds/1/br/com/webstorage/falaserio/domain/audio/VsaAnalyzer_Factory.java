package br.com.webstorage.falaserio.domain.audio;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class VsaAnalyzer_Factory implements Factory<VsaAnalyzer> {
  @Override
  public VsaAnalyzer get() {
    return newInstance();
  }

  public static VsaAnalyzer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VsaAnalyzer newInstance() {
    return new VsaAnalyzer();
  }

  private static final class InstanceHolder {
    static final VsaAnalyzer_Factory INSTANCE = new VsaAnalyzer_Factory();
  }
}
