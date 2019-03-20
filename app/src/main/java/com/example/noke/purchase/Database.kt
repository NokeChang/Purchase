package com.example.noke.purchase

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.util.Log

@Entity(tableName = "merches",
        indices = [Index(name = "merch_item", value = ["item"], unique = true)])
class Merch {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

    @ColumnInfo(name = "item")
    var item: String = ""

    @ColumnInfo(name = "photoName")
    var photoName: String = ""

    @ColumnInfo(name = "remark")
    var remark: String = ""
}

@Entity(tableName = "orders",
        foreignKeys = [ForeignKey(entity = Merch::class, parentColumns = ["id"], childColumns = ["mid"], onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Client::class, parentColumns = ["id"], childColumns = ["cid"], onDelete = ForeignKey.CASCADE)])
class Order {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

    @ColumnInfo(name = "number")
    var number: String = ""

    @ColumnInfo(name = "mid")
    var mid: Int = 1

    @ColumnInfo(name = "cid")
    var cid: Int = 1

    @ColumnInfo(name = "quantity")
    var quantity: Int = 1

    @ColumnInfo(name = "date")
    var date: String = ""

    @ColumnInfo(name = "price")
    var price: Int = 0
}

@Entity(tableName = "clients",
        indices = [Index(name = "client_name", value = ["name"], unique = true)])
class Client {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "locale")
    var locale: String = ""
}

@Entity(tableName = "purchases",
        foreignKeys = [ForeignKey(entity = Merch::class, parentColumns = ["id"], childColumns = ["mid"], onDelete = ForeignKey.CASCADE)],
        indices = [Index(name = "merch_catalog", value = ["mid"])])
class Purchase {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

    @ColumnInfo(name = "number")
    var number: String = ""

    @ColumnInfo(name = "mid")
    var mid: Int = 1

    @ColumnInfo(name = "quantity")
    var quantity: Int = 1

    @ColumnInfo(name = "date")
    var date: String = ""

    @ColumnInfo(name = "price")
    var price: Int = 0
}

@Dao
interface MerchDao {
    @Query("SELECT * FROM merches")
    fun getAll(): List<Merch>

    @Query("SELECT * FROM merches WHERE id = :id")
    fun getRecordById(id: Int): Merch

    @Query("SELECT id FROM merches WHERE id = (SELECT MAX(id) FROM merches)")
    fun getLastId(): Int

    @Query("SELECT item FROM merches")
    fun getItems(): Array<String>

    @Query("SELECT id FROM merches")
    fun getIds(): Array<Int>

    @Query("SELECT photoName FROM merches WHERE id = :id")
    fun getPhotoName(id: Int): String

    @Query("SELECT * FROM merches WHERE item like :word")
    fun getRecordsByItem(word: String): List<Merch>

    @Query("SELECT remark FROM merches WHERE id = :id")
    fun getRemarkById(id: Int): String

    @Insert
    fun insert(merch: Merch): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun update(merch: Merch)

    @Delete
    fun delete(merch: Merch)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    fun getAll(): List<Order>

    @Query("SELECT T1.id, T1.number, T2.item, T3.name, T1.quantity, T1.date, T1.price FROM orders T1, merches T2, clients T3 WHERE T1.mid = T2.id AND T1.cid = T3.id")
    fun getAllA(): List<OrderA>

    @Query("SELECT T1.id, T1.number, T2.item, T3.name, T1.quantity, T1.date, T1.price FROM orders T1, merches T2, clients T3 " +
            "WHERE T1.mid = T2.id AND T1.cid = T3.id AND T3.name like :word " +
            "AND strftime('%Y', date) like :year AND strftime('%m', date) like :month AND strftime('%d', date) like :day " +
            "ORDER BY T1.date")
    fun getRecordsBySearch(word: String, year: String, month: String, day: String): List<OrderA>

    @Query("SELECT * FROM orders WHERE id = :id")
    fun getRecordById(id: Int): Order

    @Insert
    fun insert(order: Order): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun update(order: Order)

    @Delete
    fun delete(order: Order)
}
class OrderA(var id: Int, var number: String, var item: String, var name: String, var quantity: Int, var date: String, var price: Int)

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients")
    fun getAll(): List<Client>

    @Query("SELECT * FROM clients WHERE id = :id")
    fun getRecordById(id: Int): Client

    @Query("SELECT name FROM clients")
    fun getNames(): Array<String>

    @Query("SELECT id FROM clients")
    fun getIds(): Array<Int>

    @Query("SELECT * FROM clients WHERE name like :word")
    fun getRecordsByName(word: String): List<Client>

    @Insert
    fun insert(client: Client): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun update(client: Client)

    @Delete
    fun delete(client: Client)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases")
    fun getAll(): List<Purchase>

    @Query("SELECT T1.id, T1.number, T2.item, T1.quantity, T1.date, T1.price FROM purchases T1, merches T2 WHERE T1.mid = T2.id")
    fun getAllA(): List<PurchaseA>

    @Query("SELECT T1.id, T1.number, T2.item, T1.quantity, T1.date, T1.price FROM purchases T1, merches T2 " +
            "WHERE T1.mid = T2.id AND T2.item like :word " +
            "AND strftime('%Y', date) like :year AND strftime('%m', date) like :month AND strftime('%d', date) like :day " +
            "ORDER BY T1.date")
    fun getRecordsBySearch(word: String, year: String, month: String, day: String): List<PurchaseA>

    @Query("SELECT * FROM purchases WHERE id = :id")
    fun getRecordById(id: Int): Purchase

    @Insert
    fun insert(purchase: Purchase): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun update(purchase: Purchase)

    @Delete
    fun delete(purchase: Purchase)
}
class PurchaseA(var id: Int, var number: String, var item: String, var quantity: Int, var date: String, var price: Int)

@Dao
interface ComboDao {
    @Query("SELECT T2.item, coalesce(SUM(T1.quantity), 0) sum FROM merches T2 LEFT JOIN purchases T1 ON T1.mid = T2.id GROUP BY T2.id")
    fun getPurchaseSum(): List<ItemSum>

    @Query("SELECT T2.item, coalesce(SUM(T1.quantity), 0) sum FROM merches T2 LEFT JOIN orders T1 ON T1.mid = T2.id GROUP BY T2.id")
    fun getOrderSum(): List<ItemSum>
}
class ItemSum(var item: String = "none", var sum: Int = 0)

@Database(entities = [Merch::class, Order::class, Client::class, Purchase::class], version = 2)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun getMerchDao(): MerchDao
    abstract fun getOrderDao(): OrderDao
    abstract fun getClientDao(): ClientDao
    abstract fun getPurchaseDao(): PurchaseDao
    abstract fun getComboDao(): ComboDao

    companion object {
        private val migration_1_2 = (object: Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("UPDATE merches SET item = id")
//                database.execSQL("CREATE UNIQUE INDEX merch_item ON merches(item)")
                database.execSQL("ALTER TABLE merches ADD COLUMN remark TEXT NOT NULL DEFAULT 'x'")
            }
        })
        private var instance: RecordDatabase? = null
        fun getInstance(context: Context): RecordDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, RecordDatabase::class.java, "purchase").allowMainThreadQueries()
                        .addMigrations(migration_1_2)
                        .build()
//                instance = Room.inMemoryDatabaseBuilder(context, RecordDatabase::class.java).allowMainThreadQueries().build()
            }
            return instance!!
        }
    }

    fun createView() {
        var sql_create_view1 = "CREATE VIEW IF NOT EXISTS PurchaseSumView AS SELECT T2.id id, coalesce(SUM(T1.quantity), 0) sum, coalesce(SUM(T1.price), 0) outcome FROM merches T2 LEFT JOIN purchases T1 ON T1.mid = T2.id GROUP BY T2.id"
        openHelper.writableDatabase.execSQL(sql_create_view1)
        var sql_create_view2 = "CREATE VIEW IF NOT EXISTS OrderSumView AS SELECT T2.id id, coalesce(SUM(T1.quantity), 0) sum, coalesce(SUM(T1.price), 0) income FROM merches T2 LEFT JOIN orders T1 ON T1.mid = T2.id GROUP BY T2.id"
        openHelper.writableDatabase.execSQL(sql_create_view2)
        var sql_create_view3 = "CREATE VIEW IF NOT EXISTS StockSumView AS SELECT V1.id id, V1.sum sum1, V2.sum sum2, V1.sum-V2.sum stock, V1.outcome outcome, V2.income income, V2.income-V1.outcome surplus FROM PurchaseSumView V1 LEFT JOIN OrderSumView V2 ON V1.id = V2.id"
        openHelper.writableDatabase.execSQL(sql_create_view3)
        var sql_create_view4 = "CREATE VIEW IF NOT EXISTS SpentSumView AS SELECT T2.id id, coalesce(SUM(T1.price), 0) spent FROM clients T2 LEFT JOIN orders T1 ON T1.cid = T2.id GROUP BY T2.id"
        openHelper.writableDatabase.execSQL(sql_create_view4)
    }

    fun getMerchStock(word: String): MutableList<MerchStock> {
        var sql_get_view = "SELECT T1.id id, T1.item item, T1.photoName, V1.sum1 sum1, V1.sum2 sum2, V1.stock stock, V1.outcome outcome, V1.income income, V1.surplus surplus FROM merches T1 LEFT JOIN StockSumView V1 ON T1.id = V1.id WHERE T1.item like '$word'"
        var cursor = openHelper.writableDatabase.query(sql_get_view)
        var list: MutableList<MerchStock> = mutableListOf()
        cursor.moveToNext()  //move to the first record
        for (i in 0 until cursor.count) {
            list.add(MerchStock(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8)))
            cursor.moveToNext()
        }
        return list
    }

    fun getClientSpent(word: String): MutableList<ClientSpent> {
        var sql_get_view = "SELECT T1.id id, T1.name name, T1.locale locale, V1.spent FROM clients T1 LEFT JOIN SpentSumView V1 ON T1.id = V1.id WHERE T1.name like '$word'"
        var cursor = openHelper.writableDatabase.query(sql_get_view)
        var list: MutableList<ClientSpent> = mutableListOf()
        cursor.moveToNext()  //move to the first record
        for (i in 0 until cursor.count) {
            list.add(ClientSpent(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3)))
            cursor.moveToNext()
        }
        return list
    }

    fun hasLowStock(lowStock: Int): Boolean {
        var sql_check_stock = "SELECT V1.sum-V2.sum stock FROM PurchaseSumView V1 LEFT JOIN OrderSumView V2 ON V1.id = V2.id WHERE stock < $lowStock"
        var cursor = openHelper.writableDatabase.query(sql_check_stock)
        return cursor.count > 0
    }
}
class MerchStock(var id: Int = 0, var item: String = "none", var photoName: String = "none", var sum1: Int = 0, var sum2: Int = 0, var stock: Int = 0, var outcome: Int = 0, var income: Int = 0, var surplus: Int = 0)
class ClientSpent(var id: Int, var name: String, var locale: String, var spent: Int)