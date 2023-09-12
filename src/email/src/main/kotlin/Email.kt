package wiremock.http4k.email

interface Email {
  fun sendEmail(
    recipientAddress: String,
    subject: String,
    body: String,
  )
}
