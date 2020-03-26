package com.ktor.repository

import com.ktor.domain.Book
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Books : Table("books") {
    val bookId: Column<String> = varchar("book_id", 60)
    val bookTitle: Column<String> = varchar("book_title", 100)
    val version: Column<Int> = integer("version")
    override val primaryKey = PrimaryKey(bookId, name = "Pk_Book_ID")
}


class BookRepository {
    fun create(book: Book) {
        transaction {
            Books.insert {
                it[bookId] = book.id!!
                it[bookTitle] = book.title
                it[version] = book.version
            }
        }
    }

    fun read(bookId: String) : Book?{
        return transaction {
            Books.select {
                Books.bookId eq bookId
            }.map {
                Book(
                    id = it[Books.bookId],
                    title = it[Books.bookTitle],
                    version = it[Books.version]
                )
            }.firstOrNull()
        }
    }
}