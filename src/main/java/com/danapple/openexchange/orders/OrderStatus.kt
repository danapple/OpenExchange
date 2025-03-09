package com.danapple.openexchange.orders

enum class OrderStatus(val viability: Viability) {
    OPEN(Viability.ALIVE),
    CANCELED(Viability.DEAD),
    FILLED(Viability.DEAD);

    enum class Viability {
        ALIVE,
        DEAD
    }
}