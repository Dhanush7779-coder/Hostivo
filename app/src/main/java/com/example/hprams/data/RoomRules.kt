package com.example.hprams.data

object RoomRules {
    fun getRoomCapacity(roomNumber: String): Int {
        val num = roomNumber.toIntOrNull() ?: return 4
        return when (num) {
            in 101..105 -> 4
            in 106..107 -> 4
            in 108..110 -> 6
            in 111..114 -> 3
            in 115..117 -> 3
            in 118..120 -> 6
            else -> 4
        }
    }

    fun isAc(roomNumber: String): Boolean {
        val num = roomNumber.toIntOrNull() ?: return false
        return when (num) {
            in 106..107 -> true
            in 115..117 -> true
            else -> false
        }
    }

    fun getRoomType(roomNumber: String): String {
        val cap = getRoomCapacity(roomNumber)
        val ac = if (isAc(roomNumber)) "AC" else "Non-AC"
        return "$cap Sharing $ac"
    }

    fun getFloor(roomNumber: String): String {
        val num = roomNumber.toIntOrNull() ?: return "1"
        return if (num in 111..120) "2" else "1"
    }

    val allRoomNumbers = (101..120).map { it.toString() }

    // Check capacity: count only approved students currently occupying this room in this block.
    // If capacity is exceeded or full, return false.
    fun isRoomAvailable(block: String, roomNumber: String): Boolean {
        val currentOccupants = HostelDataStore.students.count {
            it.block.lowercase().trim() == block.lowercase().trim() && 
            it.room.lowercase().trim() == roomNumber.lowercase().trim() &&
            it.approvalStatus == "Approved"
        }
        return currentOccupants < getRoomCapacity(roomNumber)
    }

    // Get number of current occupants in a room
    fun getCurrentOccupantsCount(block: String, roomNumber: String): Int {
        return HostelDataStore.students.count {
            it.block.lowercase().trim() == block.lowercase().trim() && 
            it.room.lowercase().trim() == roomNumber.lowercase().trim() &&
            it.approvalStatus == "Approved"
        }
    }
}
