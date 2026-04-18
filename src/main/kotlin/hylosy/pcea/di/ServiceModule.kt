package hylosy.pcea.di

import hylosy.pcea.dao.EventDao
import hylosy.pcea.dao.HoldingEventRecordDao
import hylosy.pcea.dao.HoldingEventResultDao
import hylosy.pcea.dao.ShopsDao
import hylosy.pcea.service.ShopService
import hylosy.pcea.service.event.EventService
import hylosy.pcea.service.event.HoldingEventService
import hylosy.pcea.service.event.result.EventResultService

object ServiceModule {
    private val holdingEventRecordDao: HoldingEventRecordDao by lazy { HoldingEventRecordDao() }
    private val holdingEventResultDao: HoldingEventResultDao by lazy { HoldingEventResultDao() }
    private val shopsDao: ShopsDao by lazy { ShopsDao() }
    private val eventDao: EventDao by lazy { EventDao() }

    val eventResultService: EventResultService by lazy {
        EventResultService(eventDao, holdingEventRecordDao, holdingEventResultDao, shopsDao)
    }
    val holdingEventService: HoldingEventService by lazy {
        HoldingEventService(holdingEventRecordDao, holdingEventResultDao)
    }
    val shopService: ShopService by lazy {
        ShopService(shopsDao)
    }
    val eventService: EventService by lazy {
        EventService(eventDao)
    }
}

