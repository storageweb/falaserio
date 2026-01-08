package br.com.webstorage.falaserio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.webstorage.falaserio.data.local.entity.CreditsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operações de créditos do usuário.
 */
@Dao
interface CreditsDao {

    @Query("SELECT * FROM credits WHERE id = 1")
    fun getCredits(): Flow<CreditsEntity?>

    @Query("SELECT * FROM credits WHERE id = 1")
    suspend fun getCreditsOnce(): CreditsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(credits: CreditsEntity)

    @Update
    suspend fun update(credits: CreditsEntity)

    @Query("UPDATE credits SET available = available - 1 WHERE id = 1 AND available > 0")
    suspend fun useCredit(): Int // Retorna número de linhas afetadas

    @Query("UPDATE credits SET available = available + :amount WHERE id = 1")
    suspend fun addCredits(amount: Int)

    @Query("UPDATE credits SET isUnlimited = 1, shouldShowAds = 0 WHERE id = 1")
    suspend fun setUnlimited()

    @Query("UPDATE credits SET shouldShowAds = :showAds WHERE id = 1")
    suspend fun setShouldShowAds(showAds: Boolean)

    @Query("UPDATE credits SET subscriptionType = :type, subscriptionStartDate = :startDate, shouldShowAds = :showAds WHERE id = 1")
    suspend fun setSubscription(type: String, startDate: Long, showAds: Boolean)

    @Query("UPDATE credits SET available = :credits, lastRenewalDate = :renewalDate WHERE id = 1")
    suspend fun renewCredits(credits: Int, renewalDate: Long)
}
