package br.com.webstorage.falaserio.data.repository

import br.com.webstorage.falaserio.data.local.dao.CreditsDao
import br.com.webstorage.falaserio.data.local.entity.CreditsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerenciar créditos do usuário.
 */
@Singleton
class CreditsRepository @Inject constructor(
    private val creditsDao: CreditsDao
) {

    fun getCredits(): Flow<CreditsEntity?> = creditsDao.getCredits()

    suspend fun getCreditsOnce(): CreditsEntity? = creditsDao.getCreditsOnce()

    /**
     * Usa 1 crédito. Retorna true se tinha crédito disponível.
     */
    suspend fun useCredit(): Boolean {
        val credits = creditsDao.getCreditsOnce()

        // Se é unlimited, não gasta
        if (credits?.isUnlimited == true) return true

        // Se não tem créditos, falha
        if (credits == null || credits.available <= 0) return false

        // Gasta 1 crédito
        val rowsAffected = creditsDao.useCredit()
        return rowsAffected > 0
    }

    suspend fun addCredits(amount: Int) {
        ensureInitialized()
        creditsDao.addCredits(amount)
    }

    suspend fun setUnlimitedCredits() {
        ensureInitialized()
        creditsDao.setUnlimited()
    }

    suspend fun setShouldShowAds(showAds: Boolean) {
        ensureInitialized()
        creditsDao.setShouldShowAds(showAds)
    }

    suspend fun setSubscription(type: String, showAds: Boolean) {
        ensureInitialized()
        creditsDao.setSubscription(type, System.currentTimeMillis(), showAds)
    }

    suspend fun renewMonthlyCredits(credits: Int) {
        ensureInitialized()
        creditsDao.renewCredits(credits, System.currentTimeMillis())
    }

    /**
     * Garante que existe um registro de créditos no banco.
     */
    private suspend fun ensureInitialized() {
        if (creditsDao.getCreditsOnce() == null) {
            creditsDao.insert(CreditsEntity())
        }
    }

    /**
     * Inicializa créditos para novo usuário (3 grátis).
     */
    suspend fun initializeForNewUser() {
        if (creditsDao.getCreditsOnce() == null) {
            creditsDao.insert(CreditsEntity(available = 3))
        }
    }
}
