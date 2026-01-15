package br.com.webstorage.falaserio.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import br.com.webstorage.falaserio.`data`.local.dao.CreditsDao
import br.com.webstorage.falaserio.`data`.local.dao.CreditsDao_Impl
import br.com.webstorage.falaserio.`data`.local.dao.HistoryDao
import br.com.webstorage.falaserio.`data`.local.dao.HistoryDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _historyDao: Lazy<HistoryDao> = lazy {
    HistoryDao_Impl(this)
  }

  private val _creditsDao: Lazy<CreditsDao> = lazy {
    CreditsDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "7daed9d7db7213b54a6513f86022c068", "83f817e453d38c0d0664b0d5dbdb0d5c") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `durationMs` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `microTremor` REAL NOT NULL, `pitchVariation` REAL NOT NULL, `jitter` REAL NOT NULL, `shimmer` REAL NOT NULL, `hnr` REAL NOT NULL, `overallStressScore` REAL NOT NULL, `stressLevel` TEXT NOT NULL, `notes` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `credits` (`id` INTEGER NOT NULL, `available` INTEGER NOT NULL, `subscriptionType` TEXT, `isUnlimited` INTEGER NOT NULL, `subscriptionStartDate` INTEGER, `lastRenewalDate` INTEGER, `shouldShowAds` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7daed9d7db7213b54a6513f86022c068')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `history`")
        connection.execSQL("DROP TABLE IF EXISTS `credits`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistory.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("filePath", TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("fileName", TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("durationMs", TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("microTremor", TableInfo.Column("microTremor", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("pitchVariation", TableInfo.Column("pitchVariation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("jitter", TableInfo.Column("jitter", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("shimmer", TableInfo.Column("shimmer", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("hnr", TableInfo.Column("hnr", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("overallStressScore", TableInfo.Column("overallStressScore", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("stressLevel", TableInfo.Column("stressLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHistory: TableInfo = TableInfo("history", _columnsHistory, _foreignKeysHistory, _indicesHistory)
        val _existingHistory: TableInfo = read(connection, "history")
        if (!_infoHistory.equals(_existingHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |history(br.com.webstorage.falaserio.data.local.entity.HistoryEntity).
              | Expected:
              |""".trimMargin() + _infoHistory + """
              |
              | Found:
              |""".trimMargin() + _existingHistory)
        }
        val _columnsCredits: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCredits.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("available", TableInfo.Column("available", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("subscriptionType", TableInfo.Column("subscriptionType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("isUnlimited", TableInfo.Column("isUnlimited", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("subscriptionStartDate", TableInfo.Column("subscriptionStartDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("lastRenewalDate", TableInfo.Column("lastRenewalDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("shouldShowAds", TableInfo.Column("shouldShowAds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCredits.put("lastUpdated", TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCredits: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCredits: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCredits: TableInfo = TableInfo("credits", _columnsCredits, _foreignKeysCredits, _indicesCredits)
        val _existingCredits: TableInfo = read(connection, "credits")
        if (!_infoCredits.equals(_existingCredits)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |credits(br.com.webstorage.falaserio.data.local.entity.CreditsEntity).
              | Expected:
              |""".trimMargin() + _infoCredits + """
              |
              | Found:
              |""".trimMargin() + _existingCredits)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "history", "credits")
  }

  public override fun clearAllTables() {
    super.performClear(false, "history", "credits")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(HistoryDao::class, HistoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CreditsDao::class, CreditsDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun historyDao(): HistoryDao = _historyDao.value

  public override fun creditsDao(): CreditsDao = _creditsDao.value
}
