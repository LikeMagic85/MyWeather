package com.example.myweather

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myweather.domain.room.HistoryDao
import com.example.myweather.domain.room.MyDB

class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        private var db:MyDB? = null
        private var appContext:MyApp? = null


        fun getHistoryDao(): HistoryDao {
            if(db == null){
                if(appContext!=null){
                    db = Room.databaseBuilder(appContext!!, MyDB::class.java, "MyDb")
                        /*.addMigrations(migration1To2)*/
                        .allowMainThreadQueries()
                        .build()

                }else{
                    throw IllegalStateException("some error")
                }
            }
            return db!!.historyDao()
        }
    }

    private val migration1To2: Migration = object :Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE history_table ADD column condition TEXT NOT NULL DEFAULT ''")
        }

    }
}