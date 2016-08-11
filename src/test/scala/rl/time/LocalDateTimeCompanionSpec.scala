package rl.time

import java.time.{LocalDateTime, ZoneOffset}

import org.scalatest.{FlatSpec, MustMatchers}

/**
  * @author Ricardo Leon
  */
class LocalDateTimeCompanionSpec extends FlatSpec with MustMatchers {
  behavior of "LocalDateTimeCompanion"

  it should "convert epoch time to LocalDateTime" in {
    val expected = LocalDateTime.ofEpochSecond(1388534400l, 123000000, ZoneOffset.UTC)
    val actual = LocalDateTimeCompanion.unapply("1388534400123")
    actual must contain(expected)
  }
  it should "convert LocalDateTime to epoch" in {
    val expected = 1388534401321l
    val actual = LocalDateTimeCompanion.toEpochMilliseconds(LocalDateTime.ofEpochSecond(1388534401l, 321000000, ZoneOffset.UTC))
    actual mustEqual expected
  }
}
