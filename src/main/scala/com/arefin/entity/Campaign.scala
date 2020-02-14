package com.arefin.entity

import scala.collection.mutable.ListBuffer

final case class Campaign(id: Int, userId: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)
final case class Targeting(cities: List[String], targetedSiteIds: List[Int])
final case class Banner(id: Int, src: String, width: Int, height: Int)

object CampaignData {

  def getRunningCampaignList(): List[Campaign] = {

    val cityList: ListBuffer[String] = ListBuffer.empty[String]
    val targetedSiteIdList: ListBuffer[Int] = ListBuffer.empty[Int]

    // populate city and targetedSite list
    for (i <- 1 to 10) {
      cityList += "city_"+i
      targetedSiteIdList += i
    }

    val targeting = Targeting(cityList.toList, targetedSiteIdList.toList)

    val banner_1: Banner = Banner(1, "http://arefin.com", 300, 100)
    val banner_2: Banner = Banner(2, "http://arefin.com", 400, 140)
    val banner_3: Banner = Banner(3, "http://arefin.com", 500, 100)
    val banner_4: Banner = Banner(4, "http://arefin.com", 100, 100)
    val bannerList: List[Banner] = List(banner_1, banner_2, banner_3, banner_4)

    val campaign_1 = Campaign(1, 1, "BD", targeting, bannerList, 100)

    val campaignList: List[Campaign] = List(campaign_1)
    return campaignList
  }
}
