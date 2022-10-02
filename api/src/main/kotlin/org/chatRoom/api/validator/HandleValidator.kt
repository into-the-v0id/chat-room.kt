package org.chatRoom.api.validator

import org.apache.commons.validator.routines.RegexValidator

object HandleValidator {
    val instance = RegexValidator(arrayOf("^[a-zA-Z0-9-_]$"))
}
