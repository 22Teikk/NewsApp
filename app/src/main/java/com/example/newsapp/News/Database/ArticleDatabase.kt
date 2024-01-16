package com.example.newsapp.News.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.News.Models.Article

@Database(
    entities = [Article::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var instance: ArticleDatabase ?= null
        fun getInstaceDatabae(context: Context): ArticleDatabase {
            if (instance != null)
                return instance!!
            synchronized(this) {
                val tempInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "articles.db"
                ).build()
                instance = tempInstance
                return instance!!
            }
        }
    }
}