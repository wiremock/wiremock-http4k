package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.matchingJsonSchema
import com.github.tomakehurst.wiremock.client.WireMock.not
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.wiremock.ListenerMode.PER_TEST
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.shouldBe
import org.http4k.core.HttpHandler
import wiremock.http4k.email.http4k.toUri
import java.util.UUID.randomUUID

abstract class SendgridEmailClientSpec(
  private val client: HttpHandler,
  private val sendgridMock: WireMockServer,
) : StringSpec(
  {

    val validApiKey: ApiKey = randomUUID().toString().toApiKey()

    beforeSpec {

      sendgridMock.givenThat(
        any(anyUrl())
          .withHeader("Authorization", not(equalTo("Bearer $validApiKey")))
          .willReturn(
            aResponse().withStatus(401)
              .withBody("""{ "error":  "no token or invalid token" }""")
          )
          .atPriority(2)
      )

      sendgridMock.givenThat(
        post(anyUrl())
          .withHeader("Content-Type", not(containing("application/json")))
          .willReturn(
            aResponse().withStatus(400)
              .withBody("""{ "error":  "only application/json accepted" }""")
          )
          .atPriority(3)
      )

      sendgridMock.givenThat(
        post("/v3/mail/send")
          .withRequestBody(
            not(
              matchingJsonSchema(
                """
                {
                  "type": "object",
                  "properties": {
                    "emails": {
                      "type": "array",
                      "items": { 
                        "type": "object",
                        "properties": {
                          "to": { "type": "array", "items": { "${'$'}ref": "#/schemas/address" } },
                          "from": { "type": "object", "${'$'}ref": "#/schemas/address" },
                          "subject": { "type": "string" }
                        },
                        "required": [ "from", "subject" ]
                      }
                    }  
                  },
                  "required": [ "emails" ],
                  "schemas": {
                    "address": {
                      "type": "object",
                      "properties": {
                        "email": { "type": "string", "format": "email" }
                      },
                      "required": [ "email" ]
                    }
                  }
                }
                """.trimIndent(),
              ),
            ),
          )
          .willReturn(
            aResponse()
              .withStatus(400)
              .withBody(
                """{ "error": "invalid_request_format" }"""
              )
          )
          .atPriority(4),
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

    "successfully sends valid email" {

      // given
      val sender: EmailAddress = "sender@example.com".toEmailAddress()
      val recipient: EmailAddress = "recipient@example.com".toEmailAddress()

      val sendgridClient = SendgridEmailClient(
        client = client,
        baseUrl = sendgridMock.baseUrl().toUri(),
        apiKey = validApiKey,
        from = sender,
      )

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
              "emails" : [
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

    "rejects invalid api key" {

      // given
      val sendgridClient = SendgridEmailClient(
        client = client,
        baseUrl = sendgridMock.baseUrl().toUri(),
        apiKey = "invalid API Key".toApiKey(),
        from = "sender@example.com".toEmailAddress(),
      )

      // when
      val e = shouldThrow<IllegalArgumentException> {
        sendgridClient.sendEmail(
          recipientAddress = "recipient@example.com".toEmailAddress(),
          subject = "Test Email",
          body = "Hello, world!",
        )
      }

      // then
      e.message shouldBe
        """Got [401 Unauthorized] with body [{ "error":  "no token or invalid token" }]"""
    }

    "rejects invalid email address" {

      // given
      val sendgridClient = SendgridEmailClient(
        client = client,
        baseUrl = sendgridMock.baseUrl().toUri(),
        apiKey = validApiKey,
        from = "sender@example.com".toEmailAddress(),
      )

      // when
      val e = shouldThrow<IllegalArgumentException> {
        sendgridClient.sendEmail(
          recipientAddress = "Not An Email Address".toEmailAddress(),
          subject = "Test Email",
          body = "Hello, world!",
        )
      }

      // then
      e.message shouldBe
        """Got [400 Bad Request] with body [{ "error": "invalid_request_format" }]"""
    }

    listener(WireMockListener(sendgridMock, PER_TEST))

    beforeTest {
      sendgridMock.resetRequests()
    }
  },
)
