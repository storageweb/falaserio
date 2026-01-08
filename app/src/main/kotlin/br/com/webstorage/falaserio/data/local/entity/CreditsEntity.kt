package br.com.webstorage.falaserio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para créditos e estado de assinatura do usuário.
 */
@Entity(tableName = "credits")
data class CreditsEntity(
    @PrimaryKey
    val id: Int = 1, // Sempre ID 1 (único registro)

    // Créditos disponíveis
    val available: Int = 3, // 3 créditos iniciais grátis

    // Estado de assinatura
    val subscriptionType: String? = null, // FREE, PACK_10, PACK_20, SUBSCRIBER_30, SUBSCRIBER_50, LIFETIME, PERPETUAL
    val isUnlimited: Boolean = false,

    // Datas de assinatura
    val subscriptionStartDate: Long? = null,
    val lastRenewalDate: Long? = null,

    // Controle de anúncios
    val shouldShowAds: Boolean = true,

    // Timestamp
    val lastUpdated: Long = System.currentTimeMillis()
)
