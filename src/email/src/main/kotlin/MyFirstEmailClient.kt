package wiremock.http4k.email

import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.format.Jackson

class MyFirstEmailClient(
  private val client: HttpHandler,
  private val baseUrl: Uri,
  private val apiKey: ApiKey,
  private val from: EmailAddress,
) : EmailClient {

  override fun sendEmail(
    recipientAddress: EmailAddress,
    subject: String,
    body: String,
  ) {

    val email = Email(
      to = recipientAddress,
      from = from,
      subject = subject,
      content = body,
    )

    val request = Request(
      method = POST,
      uri = baseUrl.path("/v3/mail/send"),
    )
      .header("Authorization", "Bearer $apiKey")
      .header("Content-Type", APPLICATION_JSON.toHeaderValue())
      .header("Accept", APPLICATION_JSON.toHeaderValue())
      .body(Body(email.toJson()))

    val response = client.invoke(request)

    require(response.status.successful) {
      "Got [${response.status}] with body [${response.bodyString()}]"
    }
  }
}

private fun Any.toJson(): String = Jackson.asFormatString(this)

@JvmInline
value class ApiKey(private val value: String) {
  override fun toString(): String = value
}

fun String.toApiKey() = ApiKey(this)

data class Email(
  val to: EmailAddress,
  val from: EmailAddress,
  val subject: String,
  val content: String,
)
