package com.danapple.openexchange.engine

import com.danapple.openexchange.dto.CancelReplace
import com.danapple.openexchange.dto.NewOrder
import com.danapple.openexchange.dto.Order

class EngineIntake {
    fun newOrder(customerId: String, clientOrderId: String, newOrder: NewOrder) : Order {
        TODO("Not yet implemented")
    }

    fun cancelReplace(customerId: String, clientOrderId: String, cancelReplace: CancelReplace) : Order {
        TODO("Not yet implemented")
    }
}
