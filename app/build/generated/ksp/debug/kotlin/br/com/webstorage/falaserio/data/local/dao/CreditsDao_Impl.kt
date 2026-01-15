package br.com.webstorage.falaserio.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import br.com.webstorage.falaserio.`data`.local.entity.CreditsEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CreditsDao_Impl(
  __db: RoomDatabase,
) : CreditsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCreditsEntity: EntityInsertAdapter<CreditsEntity>

  private val __updateAdapterOfCreditsEntity: EntityDeleteOrUpdateAdapter<CreditsEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCreditsEntity = object : EntityInsertAdapter<CreditsEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `credits` (`id`,`available`,`subscriptionType`,`isUnlimited`,`subscriptionStartDate`,`lastRenewalDate`,`shouldShowAds`,`lastUpdated`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CreditsEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.available.toLong())
        val _tmpSubscriptionType: String? = entity.subscriptionType
        if (_tmpSubscriptionType == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSubscriptionType)
        }
        val _tmp: Int = if (entity.isUnlimited) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        val _tmpSubscriptionStartDate: Long? = entity.subscriptionStartDate
        if (_tmpSubscriptionStartDate == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpSubscriptionStartDate)
        }
        val _tmpLastRenewalDate: Long? = entity.lastRenewalDate
        if (_tmpLastRenewalDate == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpLastRenewalDate)
        }
        val _tmp_1: Int = if (entity.shouldShowAds) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
        statement.bindLong(8, entity.lastUpdated)
      }
    }
    this.__updateAdapterOfCreditsEntity = object : EntityDeleteOrUpdateAdapter<CreditsEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `credits` SET `id` = ?,`available` = ?,`subscriptionType` = ?,`isUnlimited` = ?,`subscriptionStartDate` = ?,`lastRenewalDate` = ?,`shouldShowAds` = ?,`lastUpdated` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: CreditsEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.available.toLong())
        val _tmpSubscriptionType: String? = entity.subscriptionType
        if (_tmpSubscriptionType == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpSubscriptionType)
        }
        val _tmp: Int = if (entity.isUnlimited) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        val _tmpSubscriptionStartDate: Long? = entity.subscriptionStartDate
        if (_tmpSubscriptionStartDate == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpSubscriptionStartDate)
        }
        val _tmpLastRenewalDate: Long? = entity.lastRenewalDate
        if (_tmpLastRenewalDate == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpLastRenewalDate)
        }
        val _tmp_1: Int = if (entity.shouldShowAds) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
        statement.bindLong(8, entity.lastUpdated)
        statement.bindLong(9, entity.id.toLong())
      }
    }
  }

  public override suspend fun insert(credits: CreditsEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCreditsEntity.insert(_connection, credits)
  }

  public override suspend fun update(credits: CreditsEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfCreditsEntity.handle(_connection, credits)
  }

  public override fun getCredits(): Flow<CreditsEntity?> {
    val _sql: String = "SELECT * FROM credits WHERE id = 1"
    return createFlow(__db, false, arrayOf("credits")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAvailable: Int = getColumnIndexOrThrow(_stmt, "available")
        val _columnIndexOfSubscriptionType: Int = getColumnIndexOrThrow(_stmt, "subscriptionType")
        val _columnIndexOfIsUnlimited: Int = getColumnIndexOrThrow(_stmt, "isUnlimited")
        val _columnIndexOfSubscriptionStartDate: Int = getColumnIndexOrThrow(_stmt, "subscriptionStartDate")
        val _columnIndexOfLastRenewalDate: Int = getColumnIndexOrThrow(_stmt, "lastRenewalDate")
        val _columnIndexOfShouldShowAds: Int = getColumnIndexOrThrow(_stmt, "shouldShowAds")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: CreditsEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAvailable: Int
          _tmpAvailable = _stmt.getLong(_columnIndexOfAvailable).toInt()
          val _tmpSubscriptionType: String?
          if (_stmt.isNull(_columnIndexOfSubscriptionType)) {
            _tmpSubscriptionType = null
          } else {
            _tmpSubscriptionType = _stmt.getText(_columnIndexOfSubscriptionType)
          }
          val _tmpIsUnlimited: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsUnlimited).toInt()
          _tmpIsUnlimited = _tmp != 0
          val _tmpSubscriptionStartDate: Long?
          if (_stmt.isNull(_columnIndexOfSubscriptionStartDate)) {
            _tmpSubscriptionStartDate = null
          } else {
            _tmpSubscriptionStartDate = _stmt.getLong(_columnIndexOfSubscriptionStartDate)
          }
          val _tmpLastRenewalDate: Long?
          if (_stmt.isNull(_columnIndexOfLastRenewalDate)) {
            _tmpLastRenewalDate = null
          } else {
            _tmpLastRenewalDate = _stmt.getLong(_columnIndexOfLastRenewalDate)
          }
          val _tmpShouldShowAds: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfShouldShowAds).toInt()
          _tmpShouldShowAds = _tmp_1 != 0
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result = CreditsEntity(_tmpId,_tmpAvailable,_tmpSubscriptionType,_tmpIsUnlimited,_tmpSubscriptionStartDate,_tmpLastRenewalDate,_tmpShouldShowAds,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCreditsOnce(): CreditsEntity? {
    val _sql: String = "SELECT * FROM credits WHERE id = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAvailable: Int = getColumnIndexOrThrow(_stmt, "available")
        val _columnIndexOfSubscriptionType: Int = getColumnIndexOrThrow(_stmt, "subscriptionType")
        val _columnIndexOfIsUnlimited: Int = getColumnIndexOrThrow(_stmt, "isUnlimited")
        val _columnIndexOfSubscriptionStartDate: Int = getColumnIndexOrThrow(_stmt, "subscriptionStartDate")
        val _columnIndexOfLastRenewalDate: Int = getColumnIndexOrThrow(_stmt, "lastRenewalDate")
        val _columnIndexOfShouldShowAds: Int = getColumnIndexOrThrow(_stmt, "shouldShowAds")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "lastUpdated")
        val _result: CreditsEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpAvailable: Int
          _tmpAvailable = _stmt.getLong(_columnIndexOfAvailable).toInt()
          val _tmpSubscriptionType: String?
          if (_stmt.isNull(_columnIndexOfSubscriptionType)) {
            _tmpSubscriptionType = null
          } else {
            _tmpSubscriptionType = _stmt.getText(_columnIndexOfSubscriptionType)
          }
          val _tmpIsUnlimited: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsUnlimited).toInt()
          _tmpIsUnlimited = _tmp != 0
          val _tmpSubscriptionStartDate: Long?
          if (_stmt.isNull(_columnIndexOfSubscriptionStartDate)) {
            _tmpSubscriptionStartDate = null
          } else {
            _tmpSubscriptionStartDate = _stmt.getLong(_columnIndexOfSubscriptionStartDate)
          }
          val _tmpLastRenewalDate: Long?
          if (_stmt.isNull(_columnIndexOfLastRenewalDate)) {
            _tmpLastRenewalDate = null
          } else {
            _tmpLastRenewalDate = _stmt.getLong(_columnIndexOfLastRenewalDate)
          }
          val _tmpShouldShowAds: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfShouldShowAds).toInt()
          _tmpShouldShowAds = _tmp_1 != 0
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result = CreditsEntity(_tmpId,_tmpAvailable,_tmpSubscriptionType,_tmpIsUnlimited,_tmpSubscriptionStartDate,_tmpLastRenewalDate,_tmpShouldShowAds,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun useCredit(): Int {
    val _sql: String = "UPDATE credits SET available = available - 1 WHERE id = 1 AND available > 0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun addCredits(amount: Int) {
    val _sql: String = "UPDATE credits SET available = available + ? WHERE id = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, amount.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setUnlimited() {
    val _sql: String = "UPDATE credits SET isUnlimited = 1, shouldShowAds = 0 WHERE id = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setShouldShowAds(showAds: Boolean) {
    val _sql: String = "UPDATE credits SET shouldShowAds = ? WHERE id = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (showAds) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setSubscription(
    type: String,
    startDate: Long,
    showAds: Boolean,
  ) {
    val _sql: String = "UPDATE credits SET subscriptionType = ?, subscriptionStartDate = ?, shouldShowAds = ? WHERE id = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        val _tmp: Int = if (showAds) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun renewCredits(credits: Int, renewalDate: Long) {
    val _sql: String = "UPDATE credits SET available = ?, lastRenewalDate = ? WHERE id = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, credits.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, renewalDate)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
