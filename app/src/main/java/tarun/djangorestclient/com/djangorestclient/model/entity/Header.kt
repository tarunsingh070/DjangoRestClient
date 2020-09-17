package tarun.djangorestclient.com.djangorestclient.model.entity

import android.text.TextUtils
import androidx.room.*

/**
 * Model class to store header data.
 */
@Entity(tableName = "header", foreignKeys = [ForeignKey(onDelete = ForeignKey.CASCADE,
        entity = Request::class, parentColumns = arrayOf("requestId"),
        childColumns = arrayOf("parentRequestId"))], indices = [Index("parentRequestId")])
data class Header(@ColumnInfo(name = "header_type")
                  var headerType: String,
                  @ColumnInfo(name = "header_value")
                  var headerValue: String) {

    // Enum to define all supported Header types.
    enum class HeaderType(private val headerName: String) {
        ACCEPT("Accept"),
        CONTENT_TYPE("Content-Type"),
        AUTHORIZATION("Authorization"),
        AUTHORIZATION_BASIC("Authorization (Basic)"),
        CUSTOM("Custom");

        override fun toString(): String {
            return headerName
        }
    }

    @PrimaryKey(autoGenerate = true)
    var headerId: Long = 0

    var parentRequestId: Long = 0

    /**
     * Get the corresponding HeaderTypeEnum instance based on the String parameter received.
     *
     * @return HeaderType enum instance.
     */
    fun getHeaderTypeEnum(): HeaderType {
        for (type in HeaderType.values()) {
            if (TextUtils.equals(headerType, type.toString())) {
                return type
            }
        }

        return HeaderType.CUSTOM
    }

    fun clearHeaderId() {
        headerId = 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Header

        if (headerType != other.headerType) return false
        if (headerValue != other.headerValue) return false
        if (headerId != other.headerId) return false
        if (parentRequestId != other.parentRequestId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = headerType.hashCode()
        result = 31 * result + headerValue.hashCode()
        result = 31 * result + headerId.hashCode()
        result = 31 * result + parentRequestId.hashCode()
        return result
    }
}