/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RequestWithHeaders(@Embedded
                              var request: Request? = null,
                              @Relation(parentColumn = "requestId", entityColumn = "parentRequestId")
                              var headers: List<Header>? = null) {

    // We need these equals and hashcode methods to identify if the original live data object has been
    // modified or some other object while observing a live instance of this class.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestWithHeaders

        return request == other.request
    }

    override fun hashCode(): Int {
        return request?.hashCode() ?: 0
    }
}