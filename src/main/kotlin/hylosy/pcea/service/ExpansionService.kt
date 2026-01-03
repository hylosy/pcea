package hylosy.pcea.service

import hylosy.pcea.model.Expansion
import hylosy.pcea.model.ExpansionTable
import hylosy.pcea.model.Regulation
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExpansionService {
    fun getAllExpansions(): List<Expansion> =
        transaction {
            ExpansionTable
                .selectAll()
                .map {
                    Expansion(
                        id = it[ExpansionTable.id],
                        name = it[ExpansionTable.name],
                        regulation = Regulation(it[ExpansionTable.regulation]),
                        releaseDate = it[ExpansionTable.releaseDate],
                    )
                }
        }
}
