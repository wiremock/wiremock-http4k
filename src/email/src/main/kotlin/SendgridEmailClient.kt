package wiremock.http4k.email

import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.format.Jackson
import wiremock.http4k.email.Payload.Personalization
import wiremock.http4k.email.Payload.Personalization.AddressAndName
import wiremock.http4k.email.Payload.Personalization.Content

class SendgridEmailClient(
  private val client: HttpHandler,
  private val apiKey: ApiKey,
  private val from: EmailAddress,
) : EmailClient {

  override fun sendEmail(
    recipientAddress: EmailAddress,
    subject: String,
    body: String,
  ) {

    val payload = Payload(
      Personalization(
        to = AddressAndName(recipientAddress),
        from = AddressAndName(from),
        subject = subject,
        content = Content(ContentType.TEXT_PLAIN, body)
      )
    )

    val body1 = payload.toJson()
    val request = Request(
      method = POST,
      uri = "/v3/mail/send",
    )
      .header("Authorization", "Bearer $apiKey")
      .header("Content-Type", APPLICATION_JSON.toHeaderValue())
      .header("Accept", APPLICATION_JSON.toHeaderValue())
      .body(Body(body1))

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

data class Payload(
  val personalizations: List<Personalization>,
) {

  constructor(vararg personalizations: Personalization) : this(personalizations.toList())

  data class Personalization(
    val to: List<AddressAndName>,
    val from: AddressAndName,
    val subject: String,
    val content: List<Content>,
  ) {

    constructor(
      to: AddressAndName,
      from: AddressAndName,
      subject: String,
      content: Content,
    ) : this(
      to = listOf(to),
      from = from,
      subject = subject,
      content = listOf(content),
    )
    data class AddressAndName(
      val email: EmailAddress,
      val name: String? = null,
    )

    data class Content(
      val type: ContentType,
      val value: String,
    )
  }
}
