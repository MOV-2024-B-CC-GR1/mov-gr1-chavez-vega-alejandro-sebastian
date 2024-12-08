package com.example.examen

import java.io.File

class TransactionRepository(private val fileName: String) {
    init {
        val file = File(fileName)
        if (!file.exists()) file.createNewFile()
        if (!file.canWrite()) {
            throw IllegalStateException("No se puede escribir en el archivo $fileName")
        }
    }
    fun addTransaction(transaction: Transaction) {
        val file = File(fileName)
        file.appendText("${transaction.id},${transaction.date},${transaction.amount},${transaction.paymentMethod},${transaction.frequency},${transaction.userId}\n")
    }

    fun getAllTransactions(): List<Transaction> {
        val file = File(fileName)
        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull { line ->
            val fields = line.split(",")
            if (fields.size < 5) null else Transaction(
                id = fields[0],
                date = fields[1],
                amount = fields[2].toDouble(),
                paymentMethod = fields[3],
                frequency = fields[4],
                userId = fields[5]
            )
        }
    }

    fun getTransactionsByUserId(userId: String): List<Transaction> {
        return getAllTransactions().filter { it.userId == userId }
    }

    fun getTransactionById(id: String): Transaction? {
        return getAllTransactions().find { it.id == id }
    }

    fun updateTransaction(transaction: Transaction) {
        val transactions = getAllTransactions().map {
            if (it.id == transaction.id) transaction else it
        }
        saveAllTransactions(transactions)
    }

    fun deleteTransaction(id: String) {
        val transactions = getAllTransactions().filterNot { it.id == id }
        saveAllTransactions(transactions)
    }

    private fun saveAllTransactions(transactions: List<Transaction>) {
        val file = File(fileName)
        file.writeText("") // Limpiar contenido
        transactions.forEach { transaction ->
            file.appendText("${transaction.id},${transaction.date},${transaction.amount},${transaction.paymentMethod},${transaction.frequency},${transaction.userId}\n")
        }
    }
}
