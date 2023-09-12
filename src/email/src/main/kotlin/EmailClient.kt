package wiremock.http4k.email

interface EmailClient {
  fun sendEmail(
    recipientAddress: EmailAddress,
    subject: String,
    body: String,
  )
}

@JvmInline
value class EmailAddress(val value: String) {
  override fun toString(): String = value
}

fun String.toEmailAddress() = EmailAddress(this)
