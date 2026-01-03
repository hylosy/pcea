package hylosy.pcea.service

import hylosy.pcea.dao.ShopsDao
import hylosy.pcea.model.Shop
import org.jetbrains.exposed.sql.transactions.transaction

class ShopService(
    val shopsDao: ShopsDao,
) {
    fun createShops(shops: List<Shop>) {
        transaction {
            shopsDao.createMultiple(shops)
        }
    }
}