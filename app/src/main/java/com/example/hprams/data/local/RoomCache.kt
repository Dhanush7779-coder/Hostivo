package com.example.hprams.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Entities
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val roll: String,
    val name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val block: String,
    val room: String,
    val feePaidStatus: String,
    val paymentStatus: String,
    val approvalStatus: String,
    val fatherName: String,
    val emergencyPhone: String,
    val role: String,
    val dob: String
)

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val number: String,
    val block: String,
    val type: String,
    val floor: String,
    val isAvailable: Boolean
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val studentId: String,
    val amount: String,
    val currency: String,
    val paymentMethod: String,
    val paymentType: String,
    val paymentStatus: String,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignatureReference: String,
    val paymentReference: String,
    val receiptUrl: String,
    val paymentDate: String,
    val verifiedBy: String,
    val verifiedAt: String,
    val rejectionReason: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey val id: String,
    val studentName: String,
    val title: String,
    val category: String,
    val description: String,
    val status: String,
    val date: String,
    val gender: String,
    val assignedHandyman: String,
    val imageUrl: String
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val date: String,
    val content: String,
    val targetHostel: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: String,
    val deepLink: String,
    val isRead: Boolean
)

// 2. DAOs
@Dao
interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users")
    suspend fun getCachedUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Query("SELECT * FROM rooms")
    suspend fun getCachedRooms(): List<RoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Query("SELECT * FROM payments")
    suspend fun getCachedPayments(): List<PaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(complaints: List<ComplaintEntity>)

    @Query("SELECT * FROM complaints")
    suspend fun getCachedComplaints(): List<ComplaintEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    @Query("SELECT * FROM announcements")
    suspend fun getCachedAnnouncements(): List<AnnouncementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("SELECT * FROM notifications")
    suspend fun getCachedNotifications(): List<NotificationEntity>
}

// 3. Database
@Database(
    entities = [
        UserEntity::class,
        RoomEntity::class,
        PaymentEntity::class,
        ComplaintEntity::class,
        AnnouncementEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao

    companion object {
        @Volatile
        private var INSTANCE: CacheDatabase? = null

        fun getDatabase(context: android.content.Context): CacheDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CacheDatabase::class.java,
                    "hprams_cache_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
