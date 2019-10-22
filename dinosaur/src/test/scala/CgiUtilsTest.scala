package io.dinosaur

import org.scalatest._

class CgiUtilsTest extends FunSuite {
  test("parseQueryString") {
    assert(CgiUtils.parseQueryString("")("") == Seq.empty[String])
  }
}
