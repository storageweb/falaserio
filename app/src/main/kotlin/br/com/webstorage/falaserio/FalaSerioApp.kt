package br.com.webstorage.falaserio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class do FalaSério.
 *
 * @HiltAndroidApp é OBRIGATÓRIO para o Hilt funcionar.
 * Sem isso, a injeção de dependência não funciona!
 */
@HiltAndroidApp
class FalaSerioApp : Application() {

}
