package io.dinosaur
import scalanative.native._

object CgiUtils {
  def env(key: CString): String = {
    val lookup = stdlib.getenv(key)
    if (lookup == null) {
      ""
    } else {
      fromCString(lookup)
    }
  }

  def parsePathInfo(pathInfo: String): Seq[String] = {
    pathInfo.split("/").filter(_ != "")
  }

  def parseQueryString(queryString: String): Function1[String, Seq[String]] = {
    val pairs = queryString
      .split("&")
      .map(_.split("="))
      .collect {
        case Array(key, value) => (key, value)
      }
      .groupBy(_._1)
      .toSeq
    val groupedValues = for ((k, v) <- pairs;
                             values = v.toSeq.map(_._2))
      yield (k -> values)
    groupedValues.toMap.getOrElse(_, Seq.empty)
  }
}
