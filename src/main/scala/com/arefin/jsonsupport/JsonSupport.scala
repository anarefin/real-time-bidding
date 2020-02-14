package com.arefin.jsonsupport

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.arefin.entity.Banner
import spray.json.DefaultJsonProtocol


// Bidding Request and Response
final case class BidRequest(id: String, imp: Option[List[Impression]], site: Site, user: Option[User], device: Option[Device])
final case class Impression(id: String, wmin: Option[Int], wmax: Option[Int], w: Option[Int], hmin: Option[Int], hmax: Option[Int], h: Option[Int], bidFloor: Option[Double])
final case class Site(id: Int, domain: String)
final case class User(id: String, geo: Option[Geo])
final case class Device(id: String, geo: Option[Geo])
final case class Geo(country: Option[String], city: Option[String], lat: Option[Double], lon: Option[Double])

final case class BidResponse(id: String, bidRequestId: String, price: Double, adid: Option[String], banner: Option[Banner])



trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  import spray.json._
  implicit val printer = PrettyPrinter

  // bidding
  implicit val impressionJsonFormat = jsonFormat8(Impression)
  implicit val siteJsonFormat = jsonFormat2(Site)
  implicit val geoJsonFormat = jsonFormat4(Geo)
  implicit val deviceJsonFormat = jsonFormat2(Device)
  implicit val userJsonFormat = jsonFormat2(User)
  implicit val bidRequestJsonFormat = jsonFormat5(BidRequest)

  implicit val bannerJsonFormat = jsonFormat4(Banner)
  implicit val bidResponseJsonFormat = jsonFormat5(BidResponse)

}


