package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath
import com.github.tomakehurst.wiremock.client.WireMock.not
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.wiremock.ListenerMode.PER_TEST
import io.kotest.extensions.wiremock.WireMockListener
import org.http4k.core.HttpHandler
import org.intellij.lang.annotations.Language
import java.util.UUID.randomUUID

@Language("RegExp")
private const val emailRegex = """[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+"""

abstract class SendgridEmailClientSpec(
  private val client: HttpHandler,
  private val sendgridMock: WireMockServer,
) : StringSpec(
  {

    val sender: EmailAddress = "sender@example.com".toEmailAddress()
    val recipient: EmailAddress = "recipient@example.com".toEmailAddress()
    val apiKey: ApiKey = randomUUID().toString().toApiKey()

    listener(WireMockListener(sendgridMock, PER_TEST))

    lateinit var sendgridClient: SendgridEmailClient

    beforeTest {

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .withRequestBody(
            not(
              matchingJsonPath("$.personalizations[0].from.email", matching(emailRegex))
            )
          )
          .willReturn(
            aResponse()
              .withStatus(400)
              .withBody(
                """
              { 
                "error": {
                  "type": "invalid_email",
                  "field": "${'$'}.personalizations[0].from.email"
                }
              }
                """.trimIndent()
              )
          )
          .atPriority(4)
      )

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .withRequestBody(
            not(
              matchingJsonPath("$.personalizations[0].to[0].email", matching(emailRegex))
            )
          )
          .willReturn(
            aResponse()
              .withStatus(400)
              .withBody(
                """
              { 
                "error": {
                  "type": "invalid_email",
                  "field": "${'$'}.personalizations[0].to[0].email"
                }
              }
                """.trimIndent()
              )
          )
          .atPriority(4)
      )

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .withRequestBody(
            not(
              matchingJsonPath("$.personalizations[0].subject")
            )
          )
          .willReturn(
            aResponse()
              .withStatus(400)
              .withBody(
                """
              { 
                "error": {
                  "type": "missing_field",
                  "field": "${'$'}.personalizations[0].subject"
                }
              }
                """.trimIndent()
              )
          )
          .atPriority(4)
      )

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .withRequestBody(
            not(
              matchingJsonPath("$.personalizations[0].content[0].type")
            )
          )
          .willReturn(
            aResponse()
              .withStatus(400)
              .withBody(
                """
              { 
                "error": {
                  "type": "missing_field",
                  "field": "${'$'}.personalizations[0].content[0].type"
                }
              }
                """.trimIndent()
              )
          )
          .atPriority(4)
      )

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .withRequestBody(
            not(
              matchingJsonPath("$.personalizations[0].content[0].value")
            )
          )
          .willReturn(
            aResponse()
              .withStatus(400)
              .withBody(
                """
              { 
                "error": {
                  "type": "missing_field",
                  "field": "${'$'}.personalizations[0].content[0].value"
                }
              }
                """.trimIndent()
              )
          )
          .atPriority(4)
      )

      sendgridMock.givenThat(
        any(anyUrl())
          .withHeader("Authorization", not(equalTo("Bearer $apiKey")))
          .willReturn(
            aResponse().withStatus(403)
          )
          .atPriority(4)
      )

      sendgridMock.givenThat(
        any(anyUrl())
          .withHeader("Content-Type", not(containing("application/json")))
          .willReturn(
            aResponse().withStatus(400)
          )
          .atPriority(4)
      )

      sendgridClient = SendgridEmailClient(
        client = client,
        apiKey = apiKey,
        from = sender
      )

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBody("""{ "status": "sent" }""")
          )
      )
    }

    "initial test" {

      repeat(100) {
        sendgridMock.resetRequests()

        // when
        sendgridClient.sendEmail(
          recipientAddress = recipient,
          subject = "Test Email",
          body = "Hello, world!",
        )

        // then
        sendgridMock.verify(
          postRequestedFor(
            urlEqualTo("/v3/mail/send"),
          )
            .withRequestBody(
              equalToJson(
                """
          {
            "personalizations" : [
              {
                "to" : [
                  {
                    "email" : "recipient@example.com",
                    "name" : null
                  }
                ],
                "from" : {
                  "email" : "sender@example.com",
                  "name" : null
                },
                "subject" : "Test Email",
                "content" : [
                  {
                    "type" : {
                      "value" : "text/plain",
                      "directives" : [
                        {
                          "first" : "charset",
                          "second" : "utf-8"
                        }
                      ]
                    },
                    "value" : "Hello, world!"
                  }
                ]
              }
            ]
          }
                """.trimIndent(),
              ),
            ),
        )
      }
    }
  },
)
