package com.example.myweather

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.myweather.domain.room.*
import java.util.*

private const val URI_ALL = 1 // URI для всех записей
private const val URI_ID = 2 // URI для конкретной записи
private const val ENTITY_PATH =
    "HistoryEntity"

class HistoryContentProvider: ContentProvider() {

    private var authorities: String? = null // Адрес URI
    private lateinit var uriMatcher: UriMatcher // Помогает определить тип адреса URI

    private var entityContentType: String? = null // Набор строк
    private var entityContentItemType: String? = null // Одна строка

    private lateinit var contentUri: Uri // Адрес URI Provider

    override fun onCreate(): Boolean {
        authorities = context?.resources?.getString(R.string.authorities)
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(authorities, ENTITY_PATH, URI_ALL)
        uriMatcher.addURI(authorities, "$ENTITY_PATH/#", URI_ID)
        entityContentType = "vnd.android.cursor.dir/vnd.$authorities.$ENTITY_PATH"
        entityContentItemType = "vnd.android.cursor.item/vnd.$authorities.$ENTITY_PATH"
        contentUri = Uri.parse("content://$authorities/$ENTITY_PATH")
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        val historyDao = MyApp.getHistoryDao()
        val cursor = when (uriMatcher.match(p0)) {
            URI_ALL -> {
                historyDao.getHistoryCursor()
            }
            URI_ID -> {
                val id = ContentUris.parseId(p0)
                historyDao.getHistoryCursor(id)
            }
            else -> {
                throw IllformedLocaleException("плохолой URI")
            }
        }
        context?.let {
            cursor.setNotificationUri(context!!.contentResolver, contentUri)
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            URI_ALL -> {
                entityContentType
            }
            URI_ID -> {
                entityContentItemType
            }
            else -> {
                throw IllformedLocaleException("плохолой URI")
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        require(uriMatcher.match(uri) == URI_ALL) {
            throw IllformedLocaleException("плохолой URI")
        }
        val historyDao = MyApp.getHistoryDao()
        return mapper(values)?.let { it ->
            historyDao.insert(it)
            val loggerUri = ContentUris.withAppendedId(contentUri, it.id)
            context?.contentResolver?.notifyChange(loggerUri, null)
            loggerUri
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        require(uriMatcher.match(uri) == URI_ID) {
            throw IllformedLocaleException("плохолой URI")
        }
        val id = ContentUris.parseId(uri)
        val historyDao = MyApp.getHistoryDao()

        historyDao.deleteByID(id)
        context?.contentResolver?.notifyChange(uri, null)
        return 1 // FIXME
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        require(uriMatcher.match(uri) == URI_ID) {
            throw IllformedLocaleException("плохолой URI")
        }

        val historyDao = MyApp.getHistoryDao()

        mapper(values)?.let {
            historyDao.update(it)
        }
        context?.contentResolver?.notifyChange(uri, null)
        return 1 // FIXME
    }

    // Переводим ContentValues в HistoryEntity
    private fun mapper(values: ContentValues?): HistoryEntity? {
        return if (values == null) {
            null//HistoryEntity()
        } else {
            val id = if (values.containsKey(ID)) values[ID] as Long else 0
            val city = values[NAME] as String
            val temperature = values[TEMPERATURE] as Int
            val feelsLike = values[FEELS_LIKE] as Int
            val icon = values[ICON] as String
            val time = values[TIME] as String
            HistoryEntity(id, city, temperature,feelsLike, icon, time)
        }
    }
}