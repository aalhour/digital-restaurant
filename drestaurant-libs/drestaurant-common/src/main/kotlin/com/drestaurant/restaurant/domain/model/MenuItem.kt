package com.drestaurant.restaurant.domain.model

import com.drestaurant.common.domain.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

/**
 * Item in the restaurant menu
 */
class MenuItem(val id: String, val name: String, val price: Money) {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}
