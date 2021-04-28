package com.weather.model

import java.io.Serializable

data class ErrorData(
    var ok: Boolean,
    var error_msg: String
): Serializable {}