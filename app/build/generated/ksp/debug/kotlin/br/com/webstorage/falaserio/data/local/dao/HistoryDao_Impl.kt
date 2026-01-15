package br.com.webstorage.falaserio.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import br.com.webstorage.falaserio.`data`.local.entity.HistoryEntity
import javax.`annotation`.processing.Generated
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class HistoryDao_Impl(
  __db: RoomDatabase,
) : HistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistoryEntity: EntityInsertAdapter<HistoryEntity>

  private val __deleteAdapterOfHistoryEntity: EntityDeleteOrUpdateAdapter<HistoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHistoryEntity = object : EntityInsertAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `history` (`id`,`filePath`,`fileName`,`durationMs`,`createdAt`,`microTremor`,`pitchVariation`,`jitter`,`shimmer`,`hnr`,`overallStressScore`,`stressLevel`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.filePath)
        statement.bindText(3, entity.fileName)
        statement.bindLong(4, entity.durationMs)
        statement.bindLong(5, entity.createdAt)
        statement.bindDouble(6, entity.microTremor.toDouble())
        statement.bindDouble(7, entity.pitchVariation.toDouble())
        statement.bindDouble(8, entity.jitter.toDouble())
        statement.bindDouble(9, entity.shimmer.toDouble())
        statement.bindDouble(10, entity.hnr.toDouble())
        statement.bindDouble(11, entity.overallStressScore.toDouble())
        statement.bindText(12, entity.stressLevel)
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpNotes)
        }
      }
    }
    this.__deleteAdapterOfHistoryEntity = object : EntityDeleteOrUpdateAdapter<HistoryEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `history` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntity) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insert(history: HistoryEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfHistoryEntity.insertAndReturnId(_connection, history)
    _result
  }

  public override suspend fun delete(history: HistoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfHistoryEntity.handle(_connection, history)
  }

  public override fun getAllHistory(): Flow<List<HistoryEntity>> {
    val _sql: String = "SELECT * FROM history ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFilePath: Int = getColumnIndexOrThrow(_stmt, "filePath")
        val _columnIndexOfFileName: Int = getColumnIndexOrThrow(_stmt, "fileName")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfMicroTremor: Int = getColumnIndexOrThrow(_stmt, "microTremor")
        val _columnIndexOfPitchVariation: Int = getColumnIndexOrThrow(_stmt, "pitchVariation")
        val _columnIndexOfJitter: Int = getColumnIndexOrThrow(_stmt, "jitter")
        val _columnIndexOfShimmer: Int = getColumnIndexOrThrow(_stmt, "shimmer")
        val _columnIndexOfHnr: Int = getColumnIndexOrThrow(_stmt, "hnr")
        val _columnIndexOfOverallStressScore: Int = getColumnIndexOrThrow(_stmt, "overallStressScore")
        val _columnIndexOfStressLevel: Int = getColumnIndexOrThrow(_stmt, "stressLevel")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: MutableList<HistoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFilePath: String
          _tmpFilePath = _stmt.getText(_columnIndexOfFilePath)
          val _tmpFileName: String
          _tmpFileName = _stmt.getText(_columnIndexOfFileName)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpMicroTremor: Float
          _tmpMicroTremor = _stmt.getDouble(_columnIndexOfMicroTremor).toFloat()
          val _tmpPitchVariation: Float
          _tmpPitchVariation = _stmt.getDouble(_columnIndexOfPitchVariation).toFloat()
          val _tmpJitter: Float
          _tmpJitter = _stmt.getDouble(_columnIndexOfJitter).toFloat()
          val _tmpShimmer: Float
          _tmpShimmer = _stmt.getDouble(_columnIndexOfShimmer).toFloat()
          val _tmpHnr: Float
          _tmpHnr = _stmt.getDouble(_columnIndexOfHnr).toFloat()
          val _tmpOverallStressScore: Float
          _tmpOverallStressScore = _stmt.getDouble(_columnIndexOfOverallStressScore).toFloat()
          val _tmpStressLevel: String
          _tmpStressLevel = _stmt.getText(_columnIndexOfStressLevel)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          _item = HistoryEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpDurationMs,_tmpCreatedAt,_tmpMicroTremor,_tmpPitchVariation,_tmpJitter,_tmpShimmer,_tmpHnr,_tmpOverallStressScore,_tmpStressLevel,_tmpNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): HistoryEntity? {
    val _sql: String = "SELECT * FROM history WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFilePath: Int = getColumnIndexOrThrow(_stmt, "filePath")
        val _columnIndexOfFileName: Int = getColumnIndexOrThrow(_stmt, "fileName")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfMicroTremor: Int = getColumnIndexOrThrow(_stmt, "microTremor")
        val _columnIndexOfPitchVariation: Int = getColumnIndexOrThrow(_stmt, "pitchVariation")
        val _columnIndexOfJitter: Int = getColumnIndexOrThrow(_stmt, "jitter")
        val _columnIndexOfShimmer: Int = getColumnIndexOrThrow(_stmt, "shimmer")
        val _columnIndexOfHnr: Int = getColumnIndexOrThrow(_stmt, "hnr")
        val _columnIndexOfOverallStressScore: Int = getColumnIndexOrThrow(_stmt, "overallStressScore")
        val _columnIndexOfStressLevel: Int = getColumnIndexOrThrow(_stmt, "stressLevel")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: HistoryEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFilePath: String
          _tmpFilePath = _stmt.getText(_columnIndexOfFilePath)
          val _tmpFileName: String
          _tmpFileName = _stmt.getText(_columnIndexOfFileName)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpMicroTremor: Float
          _tmpMicroTremor = _stmt.getDouble(_columnIndexOfMicroTremor).toFloat()
          val _tmpPitchVariation: Float
          _tmpPitchVariation = _stmt.getDouble(_columnIndexOfPitchVariation).toFloat()
          val _tmpJitter: Float
          _tmpJitter = _stmt.getDouble(_columnIndexOfJitter).toFloat()
          val _tmpShimmer: Float
          _tmpShimmer = _stmt.getDouble(_columnIndexOfShimmer).toFloat()
          val _tmpHnr: Float
          _tmpHnr = _stmt.getDouble(_columnIndexOfHnr).toFloat()
          val _tmpOverallStressScore: Float
          _tmpOverallStressScore = _stmt.getDouble(_columnIndexOfOverallStressScore).toFloat()
          val _tmpStressLevel: String
          _tmpStressLevel = _stmt.getText(_columnIndexOfStressLevel)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          _result = HistoryEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpDurationMs,_tmpCreatedAt,_tmpMicroTremor,_tmpPitchVariation,_tmpJitter,_tmpShimmer,_tmpHnr,_tmpOverallStressScore,_tmpStressLevel,_tmpNotes)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecentHistory(limit: Int): Flow<List<HistoryEntity>> {
    val _sql: String = "SELECT * FROM history ORDER BY createdAt DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFilePath: Int = getColumnIndexOrThrow(_stmt, "filePath")
        val _columnIndexOfFileName: Int = getColumnIndexOrThrow(_stmt, "fileName")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfMicroTremor: Int = getColumnIndexOrThrow(_stmt, "microTremor")
        val _columnIndexOfPitchVariation: Int = getColumnIndexOrThrow(_stmt, "pitchVariation")
        val _columnIndexOfJitter: Int = getColumnIndexOrThrow(_stmt, "jitter")
        val _columnIndexOfShimmer: Int = getColumnIndexOrThrow(_stmt, "shimmer")
        val _columnIndexOfHnr: Int = getColumnIndexOrThrow(_stmt, "hnr")
        val _columnIndexOfOverallStressScore: Int = getColumnIndexOrThrow(_stmt, "overallStressScore")
        val _columnIndexOfStressLevel: Int = getColumnIndexOrThrow(_stmt, "stressLevel")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: MutableList<HistoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFilePath: String
          _tmpFilePath = _stmt.getText(_columnIndexOfFilePath)
          val _tmpFileName: String
          _tmpFileName = _stmt.getText(_columnIndexOfFileName)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpMicroTremor: Float
          _tmpMicroTremor = _stmt.getDouble(_columnIndexOfMicroTremor).toFloat()
          val _tmpPitchVariation: Float
          _tmpPitchVariation = _stmt.getDouble(_columnIndexOfPitchVariation).toFloat()
          val _tmpJitter: Float
          _tmpJitter = _stmt.getDouble(_columnIndexOfJitter).toFloat()
          val _tmpShimmer: Float
          _tmpShimmer = _stmt.getDouble(_columnIndexOfShimmer).toFloat()
          val _tmpHnr: Float
          _tmpHnr = _stmt.getDouble(_columnIndexOfHnr).toFloat()
          val _tmpOverallStressScore: Float
          _tmpOverallStressScore = _stmt.getDouble(_columnIndexOfOverallStressScore).toFloat()
          val _tmpStressLevel: String
          _tmpStressLevel = _stmt.getText(_columnIndexOfStressLevel)
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          _item = HistoryEntity(_tmpId,_tmpFilePath,_tmpFileName,_tmpDurationMs,_tmpCreatedAt,_tmpMicroTremor,_tmpPitchVariation,_tmpJitter,_tmpShimmer,_tmpHnr,_tmpOverallStressScore,_tmpStressLevel,_tmpNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM history"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM history WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM history"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
