package wiremock.http4k.email.http4k

import org.http4k.core.Uri

fun Uri.isAbsolute(): Boolean = scheme.isNotEmpty()
fun String.toUri() = Uri.of(this)
