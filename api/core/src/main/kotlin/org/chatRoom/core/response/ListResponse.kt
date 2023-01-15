package org.chatRoom.core.response

import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset

@Serializable
data class ListResponse<T>(
    val data: T,
    val list: ListInfo?,
) {
    @Serializable
    data class ListInfo(
        val offset: Offset?,
        val limit: Limit?,
        val currentItemCount: Int,
        val totalItemCount: Int,
    ) {
        val totalItemsBefore: Int = offset?.toInt() ?: 0
        val totalItemsAfter: Int = totalItemCount - totalItemsBefore - currentItemCount
    }
}
