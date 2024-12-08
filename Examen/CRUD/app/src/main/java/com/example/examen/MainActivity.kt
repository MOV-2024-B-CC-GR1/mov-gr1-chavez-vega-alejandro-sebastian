package com.example.examen
import java.util.*

fun main() {
    val userRepo = UserRepository("./users.txt")
    val transactionRepo = TransactionRepository("./transactions.txt")
    val scanner = Scanner(System.`in`)

    while (true) {
        println("\n=== Principal menu ===")
        println("1. Create user")
        println("2. All users")
        println("3. Modify user")
        println("4. Delete user")
        println("5. Transactions user")
        println("6. Create transaction")
        println("7. Modify transaction")
        println("8. Delete transaction")
        println("9. Exit")
        print("Select an option: ")

        when (scanner.nextLine().toIntOrNull()) {
            1 -> createUser(userRepo, scanner)
            2 -> viewAllUsers(userRepo)
            3 -> updateUser(userRepo, scanner)
            4 -> deleteUser(userRepo, transactionRepo, scanner)
            5 -> viewUserTransactions(transactionRepo, scanner)
            6 -> createTransaction(transactionRepo, scanner)
            7 -> updateTransaction(transactionRepo, scanner)
            8 -> deleteTransaction(transactionRepo, scanner)
            9 -> {
                println("Saving....")
                break
            }
            else -> println("Not valid try again")
        }
    }
}

fun createUser(userRepo: UserRepository, scanner: Scanner) {
    println("\n--- Create user ---")
    print("ID: ")
    val id = scanner.nextLine()
    print("Email: ")
    val email = scanner.nextLine()
    print("Birthday(YYYY-MM-DD): ")
    val birthday = scanner.nextLine()
    print("Country: ")
    val country = scanner.nextLine()
    print("Preferences: ")
    val preferences = scanner.nextLine()
    print("Income: ")
    val income = scanner.nextLine().toDoubleOrNull() ?: 0.0
    print("Password: ")
    val password = scanner.nextLine()
    print("premium? (yes/no): ")
    val isPremium = if (scanner.nextLine().lowercase() == "yes") "1" else "No"

    val user = User(id, email, birthday, country, preferences, income, password, isPremium)
    userRepo.addUser(user)
    println("Usuario creado con exito.")
}

fun viewAllUsers(userRepo: UserRepository) {
    println("\n--- Lista de Usuarios ---")
    val users = userRepo.getAllUsers()
    if (users.isEmpty()) {
        println("No hay usuarios registrados.")
    } else {
        users.forEach { user ->
            println("ID: ${user.id}, Email: ${user.email}, Pais: ${user.country}, Ingreso: ${user.income}, Premium: ${user.isPremium}")
        }
    }
}

fun updateUser(userRepo: UserRepository, scanner: Scanner) {
    println("\n--- Actualizar Usuario ---")
    print("Ingrese el ID del usuario a actualizar: ")
    val id = scanner.nextLine()
    val user = userRepo.getUserById(id)
    if (user == null) {
        println("Usuario no encontrado.")
        return
    }

    print(" Email (actual: ${user.email}): ")
    user.email = scanner.nextLine()
    print("country (actual: ${user.country}): ")
    user.country = scanner.nextLine()
    print("New income (actual: ${user.income}): ")
    user.income = scanner.nextLine().toDoubleOrNull() ?: user.income
    print("¿ premium? (actual: ${user.isPremium}, yes/no): ")
    user.isPremium = if (scanner.nextLine().lowercase() == "yes") "yes" else "No"

    userRepo.updateUser(user)
    println("Usuario actualizado con exito.")
}

fun deleteUser(userRepo: UserRepository, transactionRepo: TransactionRepository, scanner: Scanner) {
    println("\n--- Delete User ---")
    print(" User id to delete: ")
    val id = scanner.nextLine()
    val user = userRepo.getUserById(id)
    if (user == null) {
        println("User Not Found.")
        return
    }

    // Eliminar transacciones asociadas al usuario
    val transactions = transactionRepo.getTransactionsByUserId(id)
    transactions.forEach { transaction ->
        transactionRepo.deleteTransaction(transaction.id)
    }

    userRepo.deleteUser(id)
    println("Usuario y sus transacciones eliminados con exito.")
}

fun viewUserTransactions(transactionRepo: TransactionRepository, scanner: Scanner) {
    println("\n--- Ver Transacciones de un Usuario ---")
    print("Ingrese el ID del usuario: ")
    val userId = scanner.nextLine()
    val transactions = transactionRepo.getTransactionsByUserId(userId)
    if (transactions.isEmpty()) {
        println("No hay transacciones registradas para este usuario.")
    } else {
        transactions.forEach { transaction ->
            println("ID: ${transaction.id}, Fecha: ${transaction.date}, Monto: ${transaction.amount}, Metodo: ${transaction.paymentMethod}")
        }
    }
}

fun createTransaction(transactionRepo: TransactionRepository, scanner: Scanner) {
    println("\n--- Create transaction ---")
    print("ID: ")
    val id = scanner.nextLine()
    print("DATE (YYYY-MM-DD): ")
    val date = scanner.nextLine()
    print("Amount: ")
    val amount = scanner.nextLine().toDoubleOrNull() ?: 0.0
    print("Payment Method: ")
    val paymentMethod = scanner.nextLine()
    print("Frequency: ")
    val frequency = scanner.nextLine()
    print("ID user: ")
    val userId = scanner.nextLine()

    val transaction = Transaction(id, date, amount, paymentMethod, frequency, userId)
    transactionRepo.addTransaction(transaction)
    println("Created Successfully")
}

fun updateTransaction(transactionRepo: TransactionRepository, scanner: Scanner) {
    println("\n--- Actualizar Transacción ---")
    print("Ingrese el ID de la transacción a actualizar: ")
    val id = scanner.nextLine()
    val transaction = transactionRepo.getTransactionById(id)
    if (transaction == null) {
        println("Transacción no encontrada.")
        return
    }

    println("Nueva Fecha (actual: ${transaction.date}): ")
    transaction.date = scanner.nextLine()
    println("Nuevo Monto (actual: ${transaction.amount}): ")
    transaction.amount = scanner.nextLine().toDoubleOrNull() ?: transaction.amount
    println("Nuevo Método de Pago (actual: ${transaction.paymentMethod}): ")
    transaction.paymentMethod = scanner.nextLine()

    transactionRepo.updateTransaction(transaction)
    println("Transaccion actualizada con exito.")
}

fun deleteTransaction(transactionRepo: TransactionRepository, scanner: Scanner) {
    println("\n--- Eliminar Transaccion ---")
    print("Id transaction to delete: ")
    val id = scanner.nextLine()
    val transaction = transactionRepo.getTransactionById(id)
    if (transaction == null) {
        println("Not found")
        return
    }

    transactionRepo.deleteTransaction(id)
    println("Deleted Successfully ")
}
