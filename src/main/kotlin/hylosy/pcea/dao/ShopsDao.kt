package hylosy.pcea.dao

import hylosy.pcea.model.Shop
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.select

class ShopsDao {
    fun getShops(ids: List<Int>): List<Shop> =
        ShopTable
            .select { ShopTable.id inList ids }
            .map { it.toShop() }

    fun createMultiple(shops: List<Shop>) {
        ShopTable.batchUpsert(shops) { shop ->
            this[ShopTable.id] = shop.id
            this[ShopTable.name] = shop.name
        }
    }

    private fun ResultRow.toShop(): Shop =
        Shop(
            id = this[ShopTable.id],
            name = this[ShopTable.name],
        )
}

object ShopTable : Table("shops") {
    // NOTE: Set default value because of batch insert.
    val id = integer("id").default(0)
    val name = varchar("name", 100)

    override val primaryKey = PrimaryKey(id)
}
