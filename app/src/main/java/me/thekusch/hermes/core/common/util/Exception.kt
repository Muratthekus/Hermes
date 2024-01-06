package me.thekusch.hermes.core.common.util

class OtpRequestLimitException(
    override val message: String = "You reached the OTP request limit. Please wait a few minutes and try again"
): Exception()